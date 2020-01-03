package com.example.meeting2020.Comment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.R;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Meeting2Activity extends AppCompatActivity {
    private static final int STATUS_CLOSE = 0;
    private static final int STATUS_CONNECT = 1;
    private static final int STATUS_MESSAGE = 2;
    SQLiteDatabase db;
    ContentValues values;
    MyHelper myHelper;
    private ScrollView svContent;
    private Button btSend;
    private View viewMain;
    private EditText etMessage;
    private Button btn_sup,btn_against;
    private TextView tv_supnum,tv_disnum,tvMsg,tv_speakername,tv_meetingtheme,tv_publisher,tv_meetingintroduce;
    private String attitute="";
    private Client mClient;
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String message = String.format("%s\n", msg.obj.toString());
            tvMsg.append(message);
            switch (msg.what) {
                case STATUS_CONNECT:
                    btSend.setEnabled(true);
                    break;
                case STATUS_CLOSE:
                    btSend.setEnabled(false);
                    break;
                case STATUS_MESSAGE:
                    // TODO: 16/8/24
                    break;
                default:
                    break;
            }
            svContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    svContent.fullScroll(View.FOCUS_DOWN);
                }
            }, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_layout2);
        btn_sup= findViewById(R.id.btn_sup);
        btn_against=findViewById(R.id.btn_against);
        tv_speakername=findViewById(R.id.tv_speakername);
        tv_meetingtheme=findViewById(R.id.tv_theme2);
        tv_publisher=findViewById(R.id.tv_publisher2);
        tv_meetingintroduce=findViewById(R.id.tv_meetingintroduce);
        tv_supnum=findViewById(R.id.tv_supnum);
        tv_disnum=findViewById(R.id.tv_disnum);
        tvMsg=findViewById(R.id.tvMsg);
        etMessage=findViewById(R.id.etMessage);
        svContent=findViewById(R.id.svContent);
        btSend=findViewById(R.id.btSend);
        connectToServer();
        getSupportNum();
        myHelper=new MyHelper(this);
        Bundle bundle=getIntent().getExtras();
        final String speaker_name=bundle.getString("speaker_name");
        final String meeting_theme=bundle.getString("meeting_theme");
        final String publisher=bundle.getString("publisher");
        final String introduce=bundle.getString("introduce");
        tv_speakername.setText("演讲人："+speaker_name);
        tv_meetingtheme.setText("大会主题："+meeting_theme);
        tv_publisher.setText("负责人："+publisher);
        tv_meetingintroduce.setText("主题："+introduce);
        final Data app = (Data) getApplication();
        final String username=app.getUserInfo().getName();
//        SharedPreferences sp=getSharedPreferences("userInfo", MODE_PRIVATE);
//        final String username=sp.getString("UserName","");
        //点赞事件
        btn_sup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               final String IsSupport="true";
                db=myHelper.getReadableDatabase();
                Cursor cursor=db.rawQuery("select* from remark where username=? and meeting_theme=? and speaker_name=?",
                        new String[]{username,meeting_theme,speaker_name});
                if (cursor.getCount()!=0){
                    btn_sup.setEnabled(false);
                    Toast.makeText(Meeting2Activity.this,"您已点过赞啦！",Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = HttpUtilsHttpURLConnection.BASE_URL2 + "/Support_or_Against";
                            Map<String, String> params = new HashMap<String, String>();
                            attitute = "support";
                            params.put("attitute", attitute);
                            params.put("meeting_theme", meeting_theme);
                            params.put("speaker_name", speaker_name);
                            String result = HttpUtilsHttpURLConnection.getContextByHttp1(url, params);
                            Message msg = new Message();
                            msg.what = 0x12;
                            Bundle data = new Bundle();
                            data.putString("result", result);
                            msg.setData(data);
                            hander.sendMessage(msg);
                        }

                        Handler hander = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 0x12) {
                                    Bundle data = msg.getData();
                                    String key = data.getString("result");
                                    try {
                                        JSONObject jsonObject = new JSONObject(key);
                                        String result = jsonObject.getString("result");
                                        if ("success".equals(result)) {
                                            db = myHelper.getWritableDatabase();
                                            values=new ContentValues();
                                            values.put("username", username);
                                            values.put("meeting_theme", meeting_theme);
                                            values.put("speaker_name", speaker_name);
                                            values.put("IsSupport", IsSupport);
                                            db.insert("remark", null, values);
                                            Toast.makeText(Meeting2Activity.this, "感謝支持", Toast.LENGTH_SHORT).show();
                                            db.close();
                                            btn_sup.setEnabled(false);
                                            getSupportNum();
                                        } else {
                                            Toast.makeText(Meeting2Activity.this, "系统繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                    }).start();
                }
            }
        });

        //踩事件
        btn_against.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String IsSupport="false";
                db=myHelper.getReadableDatabase();
                Cursor cursor=db.rawQuery("select* from remark where username=? and meeting_theme=? and speaker_name=?",
                        new String[]{username,meeting_theme,speaker_name});
                if (cursor.getCount()!=0){
                    btn_sup.setEnabled(false);
                    Toast.makeText(Meeting2Activity.this,"您已投过票啦！",Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = HttpUtilsHttpURLConnection.BASE_URL2 + "/Support_or_Against";
                            Map<String, String> params = new HashMap<String, String>();
                            attitute = "against";
                            params.put("attitute", attitute);
                            params.put("meeting_theme", meeting_theme);
                            params.put("speaker_name", speaker_name);
                            String result = HttpUtilsHttpURLConnection.getContextByHttp1(url, params);
                            Message msg = new Message();
                            msg.what = 0x12;
                            Bundle data = new Bundle();
                            data.putString("result", result);
                            msg.setData(data);
                            hander.sendMessage(msg);
                        }

                        Handler hander = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 0x12) {
                                    Bundle data = msg.getData();
                                    String key = data.getString("result");
                                    try {
                                        JSONObject jsonObject = new JSONObject(key);
                                        String result = jsonObject.getString("result");
                                        if ("success".equals(result)) {
                                            db = myHelper.getWritableDatabase();
                                            values = new ContentValues();
                                            values.put("username",username);
                                            values.put("meeting_theme", meeting_theme);
                                            values.put("speaker_name", speaker_name);
                                            values.put("IsSupport", IsSupport);
                                            db.insert("remark", null, values);
                                            Toast.makeText(Meeting2Activity.this, "我觉得不行", Toast.LENGTH_SHORT).show();
                                            db.close();
                                            btn_sup.setEnabled(false);
                                            getSupportNum();
                                        } else {
                                            Toast.makeText(Meeting2Activity.this, "系统繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                    }).start();
                }
            }
        });
        //发送评论
        btSend.setOnClickListener(new View.OnClickListener() {
            final Data app = (Data) getApplication();
            final String name=app.getUserInfo().getName();
            @Override
            public void onClick(View v) {
                if (null != mClient) {
                    String msg =name+":"+ etMessage.getText().toString();
                    if (!TextUtils.isEmpty(msg)) {
                        try {
                            mClient.send(msg);
                        } catch (NotYetConnectedException e) {
                            e.printStackTrace();
                            return;
                        }
                        // 发送完成之后 清除输入框里面的内容
                        etMessage.setText("");
                    }
                }
            }
        });
        System.setProperty("java.net.preferIPv4Stack", "true");

    }


    private void connectToServer() {
        String ip = "192.168.43.188";
        String port = "2443";
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            Toast.makeText(viewMain.getContext(),"IP and Port 不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        String address = String.format("ws://%s:%s", ip, port);
        Draft draft = new Draft_17();

        try {
            URI uri = new URI(address);
            mClient = new Client(uri, draft);
            mClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

    }

    private class Client extends WebSocketClient {

        public Client(URI serverURI) {
            super(serverURI);
        }

        public Client(URI serverUri, Draft draft) {
            super(serverUri, draft);
        }

        @Override
        public void onOpen(ServerHandshake handShakeData) {
            Message msg = new Message();
            msg.what = STATUS_CONNECT;
            msg.obj = String.format("欢迎来到大会点评");
            mHandle.sendMessage(msg);
        }

        @Override
        public void onMessage(String message) {
            Message msg = new Message();
            msg.what = STATUS_MESSAGE;
            msg.obj = message;
            mHandle.sendMessage(msg);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Message msg = new Message();
            msg.what = STATUS_CLOSE;
            msg.obj = String.format("[Bye：%s]", getURI());
            mHandle.sendMessage(msg);
        }

        @Override
        public void onWebsocketPong(WebSocket conn, Framedata f) {
            super.onWebsocketPong(conn, f);

            String value = parseFramedata(f);

            Message msg = new Message();
            msg.what = STATUS_MESSAGE;
            msg.obj = "pong:" + value;
            mHandle.sendMessage(msg);
        }

        @Override
        public void onWebsocketPing(WebSocket conn, Framedata f) {
            super.onWebsocketPing(conn, f);

            String value = parseFramedata(f);

            Message msg = new Message();
            msg.what = STATUS_MESSAGE;
            msg.obj = "ping:" + value;
            mHandle.sendMessage(msg);
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }

        public String parseFramedata(Framedata framedata){
            String result = "null";
            ByteBuffer buffer = framedata.getPayloadData();
            if(null == buffer){
                return result;
            }
            byte[] data = buffer.array();
            if(null != data && data.length > 0){
                return new String(data);
            }
            return result;
        }
    }
    private void getSupportNum(){
//        OkHttpClient okHttpClient=new OkHttpClient();
//        Request request=new Request.Builder().url(HttpUtilsHttpURLConnection.BASE_URL2+"/MeetingSpeaker").build();
//        Call call=okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Toast.makeText(Meeting2Activity.this,"系统繁忙，请稍后再试",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String res=response.body().string();
//                try {
//                            JSONObject jsonObject=new JSONObject(res);
//                            String result=jsonObject.getString("result");
//                            int supnum=jsonObject.getInt("speaker_SupNum");
//                            int disnum=jsonObject.getInt("speaker_DisNum");
//                            if ("success".equals(result)){
//                                tv_supnum.setText(supnum+"");
//                                tv_disnum.setText(disnum+"");
//                            }else {
//                                Toast.makeText(Meeting2Activity.this,"系统繁忙，请稍后再试",Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = HttpUtilsHttpURLConnection.BASE_URL2+"/MeetingSpeaker";
                Map<String, String> params = new HashMap<String, String>();

                Bundle bundle=getIntent().getExtras();
                String meeting_theme=bundle.getString("meeting_theme");
                String speaker_name=bundle.getString("speaker_name");
                params.put("meeting_theme",meeting_theme);
                params.put("speaker_name",speaker_name);
                String result = HttpUtilsHttpURLConnection.getContextByHttp1(url, params);
                Message msg = new Message();
                msg.what = 0x12;
                Bundle data = new Bundle();
                data.putString("result", result);
                msg.setData(data);
                hander.sendMessage(msg);
            }
            Handler hander = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x12) {
                        Bundle data = msg.getData();
                        String key = data.getString("result");
                        try {
                            JSONObject jsonObject=new JSONObject(key);
                            String result=jsonObject.getString("result");
                            int supnum=jsonObject.getInt("speaker_SupNum");
                            int disnum=jsonObject.getInt("speaker_DisNum");
                            if ("success".equals(result)){
                                tv_supnum.setText(supnum+"");
                                tv_disnum.setText(disnum+"");
                            }else {
                                Toast.makeText(Meeting2Activity.this,"系统繁忙，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }).start();
    }
    class MyHelper extends SQLiteOpenHelper{
        public MyHelper(Context context){
            super(context,"remark.db",null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE remark(_id INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR(10) ,meeting_theme VARCHAR(20)," +
                    "speaker_name INTEGER(10),IsSupport VARCHAR(10))");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode==KeyEvent.KEYCODE_BACK){
            mClient.close();
            Meeting2Activity.this.finish();
        }
        return true;
    }

}
