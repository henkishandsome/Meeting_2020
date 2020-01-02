package com.example.meeting2020;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_workId,et_password,et_name,et_tel,et_email;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        loginButtonHttpURLConnectionOption();
    }

    private void loginButtonHttpURLConnectionOption() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = HttpUtilsHttpURLConnection.BASE_URL+"/RegisterServlet";
                        Map<String, String> params = new HashMap<String, String>();
                        String workId = et_workId.getText().toString();
                        String password = et_password.getText().toString();
                        String name=et_name.getText().toString();
                        String tel=et_tel.getText().toString();
                        String email=et_email.getText().toString();

                        params.put("workId", workId);
                        params.put("password", password);
                        params.put("name",name);
                        params.put("tel",tel);
                        params.put("email",email);

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
                                    if ("registerYes".equals(result)) {
                                        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else if ("registerNo".equals(result)) {
                                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                }).start();
            }
        });
    }


    private void init() {
        et_workId = (EditText) findViewById(R.id.et_workId);
        et_password = (EditText) findViewById(R.id.et_password);
        et_name=(EditText) findViewById(R.id.et_name);
        et_tel=(EditText) findViewById(R.id.et_tel);
        et_email=(EditText) findViewById(R.id.et_email);
        btn_submit=(Button) findViewById(R.id.btn_submit);
    }
}
