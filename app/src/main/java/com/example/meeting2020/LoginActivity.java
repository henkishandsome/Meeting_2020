package com.example.meeting2020;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.meeting2020.Bean.UserInfo;
import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.JavaClass.UserManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEdit, passwordEdit;
    private Button btn_register, loginButtonHttpURLConnection;
    private String workid, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();
        loginButtonHttpURLConnectionOption();
    }

    private void loginButtonHttpURLConnectionOption() {

        loginButtonHttpURLConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workid = nameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                String[] strings = new String[]{workid, password};
                LoginAsyncTask loginAsyncTask = new LoginAsyncTask(new WeakReference<LoginActivity>(LoginActivity.this));
                loginAsyncTask.execute(strings);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private static class LoginAsyncTask extends AsyncTask<String, Void, UserInfo> {
        private final WeakReference<LoginActivity> weakActivity;
        private UserInfo userInfo;

        private LoginAsyncTask(WeakReference<LoginActivity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        @Override
        protected UserInfo doInBackground(String... strings) {
            String url = HttpUtilsHttpURLConnection.BASE_URL + "user/login.do";
            UserInfo u = new UserInfo();
            u.setWorkid(strings[0]);
            u.setPassword(strings[1]);
            JSONObject j = (JSONObject) JSONObject.toJSON(u);
            //取出当前用户账号
            String json = HttpUtilsHttpURLConnection.getContextByHttp(url, j);
            JSONObject jsonObject = JSONObject.parseObject(json);
            String result = jsonObject.getString("result");
            if (!"error".equals(result)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("workid", strings[0]);
                String user = HttpUtilsHttpURLConnection.getContextByHttp1(HttpUtilsHttpURLConnection.BASE_URL + "user/finduser.do", map);
                UserInfo userInfo = JSONObject.parseObject(user, UserInfo.class);
                return userInfo;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserInfo userInfo) {
            super.onPostExecute(userInfo);
            LoginActivity activity = weakActivity.get();
            final Data app = (Data) activity.getApplication();
            if (userInfo != null) {
                app.setStatus(userInfo.getStatus());
                app.setUserInfo(userInfo);
                UserManager.getInstance().saveUserInfo(activity, userInfo.getWorkid(), userInfo.getPassword());
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Toast.makeText(activity, "账号或密码不正确,请重新登录", Toast.LENGTH_LONG).show();
                activity.nameEdit.setText("");
                activity.passwordEdit.setText("");
            }
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                // activity死亡了，不再做任何的事情
                return;
            }
        }
    }

    private void init() {
        nameEdit = findViewById(R.id.nameEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        btn_register = findViewById(R.id.btn_register);
        loginButtonHttpURLConnection = findViewById(R.id.login1);
    }
}
