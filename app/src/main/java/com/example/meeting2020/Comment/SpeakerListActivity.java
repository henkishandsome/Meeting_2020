package com.example.meeting2020.Comment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.Bean.SpeakerBean;
import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpeakerListActivity extends AppCompatActivity {
    private SmartRefreshLayout srfresh;
    private Handler mHandler;
    private ListView lv_meeting;
    private Button btn_deleteAll,btn_addMeetingSpeaker;
    private List<SpeakerBean> Speakerlist = new ArrayList<>();
//    private final String meeting_theme="bbbb";
//    private Bundle bundle=getIntent().getExtras();
//    private Meeting meeting=(Meeting) bundle.getSerializable("meeting");
//    private final String meeting_theme=meeting.getTheme();
    private static final String WEB_SITE="http://192.168.43.188:8082/HttpServlet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meetingspeaker);
        srfresh=findViewById(R.id.meeting_refresh);
        mHandler=new Handler();
        aboutAapter();
        getMeeting();
        lv_meeting=findViewById(R.id.lv_meeting);
        btn_deleteAll=findViewById(R.id.btn_deleteAll);
        btn_addMeetingSpeaker=findViewById(R.id.btn_addMeetingSpeaker);
        final Data app = (Data) getApplication();
        final int status=app.getStatus();
         Bundle bundle=getIntent().getExtras();
         final Meeting meeting=(Meeting) bundle.getSerializable("meeting");
         final String meeting_theme=meeting.getTheme();
//        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
//        final String status = sp.getString("Status", "");
        if (status!=1){
            btn_deleteAll.setEnabled(false);
            btn_deleteAll.setVisibility(View.GONE);
            btn_addMeetingSpeaker.setVisibility(View.GONE);
            btn_addMeetingSpeaker.setEnabled(false);
        }

        final meetingBaseAdapter meetingAdapter=new meetingBaseAdapter();
        lv_meeting.setAdapter(meetingAdapter);
        lv_meeting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               Bundle bundle=new Bundle();
               bundle.putString("speaker_name",Speakerlist.get(position).getSpeaker_name());
               bundle.putInt("speaker_SupNum",Speakerlist.get(position).getSpeaker_SupNum());
               bundle.putInt("speaker_DisNum",Speakerlist.get(position).getSpeaker_DisNum());
               bundle.putString("meeting_theme",meeting_theme);
               bundle.putString("introduce",meeting.getIntroduce());
               bundle.putString("publisher",meeting.getPublisher());
                Intent intent=new Intent();
                intent.putExtras(bundle);
                intent.setClass(SpeakerListActivity.this,Meeting2Activity.class);
                startActivity(intent);
            }
        });
        //长按删除
        lv_meeting.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position,long id) {
                if (status==1) {
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SpeakerListActivity.this)
                            .setMessage("是否删除此演讲人?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    OkHttpClient okHttpClient = new OkHttpClient();
                                    Request request = new Request.Builder().url(WEB_SITE + "/DelSpeaker"
                                            + "?meeting_theme=" + meeting_theme
                                            + "&speaker_name=" + Speakerlist.get(position).getSpeaker_name()).build();
                                    Call call = okHttpClient.newCall(request);
                                    call.enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                            System.out.println("请求失败");
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            System.out.println("请求成功");
                                        }
                                    });
                                    Speakerlist.remove(position);
                                    meetingAdapter.notifyDataSetChanged();
                                    Toast.makeText(SpeakerListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog = builder.create();
                    dialog.show();
                }
                return true;
            }

        });
btn_deleteAll.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(SpeakerListActivity.this)
                    .setMessage("是否删除全部演讲人?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request request = new Request.Builder().url(WEB_SITE + "/DelAllSpeaker" + "?meeting_theme=" + meeting_theme).build();
                            Call call = okHttpClient.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    System.out.println("请求失败");
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    System.out.println("请求成功");
                                }
                            });
                            meetingAdapter.notifyDataSetChanged();
                            Toast.makeText(SpeakerListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }

});
btn_addMeetingSpeaker.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        intent.setClass(SpeakerListActivity.this,AddSpeakerActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("meeting_theme",meeting_theme);
        intent.putExtras(bundle);
        startActivity(intent);
    }
});
    }
    private void aboutAapter() {
        //设置 Header 为 贝塞尔雷达 样式
        srfresh.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
        //设置 Footer 为 球脉冲 样式
        srfresh.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        srfresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                //延时展示，延时2秒
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Speakerlist.removeAll(Speakerlist);
                        getMeeting();
                        refreshlayout.finishRefresh();
                    }
                },1500);
            }
        });
        srfresh.setEnableLoadMore(true);
        srfresh.autoRefresh();
    }
    private void getMeeting(){
        Bundle bundle=getIntent().getExtras();
        final Meeting meeting=(Meeting) bundle.getSerializable("meeting");
        final String meeting_theme=meeting.getTheme();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = HttpUtilsHttpURLConnection.BASE_URL2+"/MeetingSpeakerAll";
                Map<String, String> params = new HashMap<String, String>();
                params.put("meeting_theme",meeting_theme);
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
                            JSONArray jsonArray = new JSONArray(key);
                            for (int i = 0; i <jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                String speaker_name = json.getString("speaker_name");
                                int speaker_SupNum=json.getInt("speaker_SupNum");
                                int speaker_DisNum=json.getInt("speaker_DisNum");
                                SpeakerBean speakerBean=new SpeakerBean();
                                speakerBean.setSpeaker_name(speaker_name);
                                speakerBean.setSpeaker_SupNum(speaker_SupNum);
                                speakerBean.setSpeaker_DisNum(speaker_DisNum);
                                Speakerlist.add(speakerBean);
                                System.out.println(Speakerlist);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }).start();
    }
    class ViewHolder{
        TextView tv_speakername,item_supnum,item_disnum;
    }
    class meetingBaseAdapter extends BaseAdapter {
        @Override
        public int getCount(){
            return Speakerlist.size();
        }
        @Override
        public Object getItem(int position){
            return null;
//            return Speakerlist.get(position).getSpeaker_name();
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder=null;
            if (convertView==null){
                convertView=View.inflate(SpeakerListActivity.this,R.layout.addmeetingspeaker_item_layout,null);
                holder=new ViewHolder();
                holder.tv_speakername=(TextView) convertView.findViewById(R.id.tv_speakername);
                holder.item_supnum=(TextView) convertView.findViewById(R.id.item_supnum);
                holder.item_disnum=(TextView) convertView.findViewById(R.id.item_disnum);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder) convertView.getTag();
            }
            holder.tv_speakername.setText(Speakerlist.get(position).getSpeaker_name());
            holder.item_supnum.setText(Speakerlist.get(position).getSpeaker_SupNum()+"");
            holder.item_disnum.setText(Speakerlist.get(position).getSpeaker_DisNum()+"");
            return convertView;
        }
}
    }
