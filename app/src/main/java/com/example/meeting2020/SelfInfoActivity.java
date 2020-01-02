package com.example.meeting2020;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SelfInfoActivity extends AppCompatActivity {
    private TextView tv_selfname,tv_selfworkId,tv_selftel,tv_selfemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);
        tv_selfname=(TextView) findViewById(R.id.tv_selfname);
        tv_selfworkId=(TextView) findViewById(R.id.tv_selfworkId);
        tv_selftel=(TextView) findViewById(R.id.tv_selftel);
        tv_selfemail=(TextView) findViewById(R.id.tv_selfemail);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = HttpUtilsHttpURLConnection.BASE_URL+"/UserInfo";
                Map<String, String> params = new HashMap<String, String>();
                //取出当前用户账号
                SharedPreferences sp=getSharedPreferences("userInfo", MODE_PRIVATE);
                String workId=sp.getString("WorkId","");
                params.put("workId", workId);

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
                        String key = data.getString("result");//得到json返回的json
                        //                                   Toast.makeText(LoginActivity.this,key,Toast.LENGTH_LONG).show();
                        try {
                            JSONObject json = new JSONObject(key);
                            String result = (String) json.get("result");
                            String workId=(String) json.get("workId");
                            String name=(String) json.get("name");
                            String tel=(String) json.get("tel");
                            String email=(String) json.get("email");
                            if ("success".equals(result)) {
                                //显示用户信息
                                tv_selfname.setText(name);
                                tv_selfworkId.setText(workId);
                                tv_selfemail.setText(email);
                                tv_selftel.setText(tel);

                            } else if ("error".equals(result)) {
                                Toast.makeText(SelfInfoActivity.this, "信息更新失败", Toast.LENGTH_LONG).show();
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
