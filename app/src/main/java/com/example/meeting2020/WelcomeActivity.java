package com.example.meeting2020;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meeting2020.Bean.UserInfo;
import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.JavaClass.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new WelcomeThread().start();
    }

    private static class WelcomeAsyncTask extends AsyncTask<Void, Void, UserInfo>{

        private final WeakReference<WelcomeActivity> weakActivity;
        String workid;

        private WelcomeAsyncTask(WeakReference<WelcomeActivity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        @Override
        protected UserInfo doInBackground(Void... voids) {
            WelcomeActivity activity = weakActivity.get();
            String url = HttpUtilsHttpURLConnection.BASE_URL + "user/login.do";
            Map<String, String> params = new HashMap<String, String>();
            UserInfo u = new UserInfo();
            if (UserManager.getInstance().hasUserInfo(activity)) {
                SharedPreferences sp = activity.getSharedPreferences("userInfo", MODE_PRIVATE);
                workid = sp.getString("workid", "");
                String password = sp.getString("password", "");
                u.setWorkid(workid);
                u.setPassword(password);
            } else {
                return null;
            }
            com.alibaba.fastjson.JSONObject j = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.toJSON(u);
            String json = HttpUtilsHttpURLConnection.getContextByHttp(url, j);
            try {
                JSONObject jsonObject = new JSONObject(json);
                String result = jsonObject.getString("result");
                if (!"error".equals(result)) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("workid", workid);
                    String user = HttpUtilsHttpURLConnection.getContextByHttp1(HttpUtilsHttpURLConnection.BASE_URL + "user/finduser.do", map);
                    UserInfo userInfo = com.alibaba.fastjson.JSONObject.parseObject(user, UserInfo.class);
                    return userInfo;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserInfo userInfo) {
            super.onPostExecute(userInfo);
            WelcomeActivity activity = weakActivity.get();
            final Data app = (Data) activity.getApplication();
            if (userInfo != null) {
                app.setStatus(userInfo.getStatus());
                app.setUserInfo(userInfo);
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else {
                if (UserManager.getInstance().hasUserInfo(activity)) {
                    Toast.makeText(activity, "账号或密码已修改,请重新登录!", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                // activity死亡了，不再做任何的事情
                return;
            }
        }
    }

    private class WelcomeThread extends Thread {
        @Override
        public void run() {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WelcomeAsyncTask welcomeAsyncTask = new WelcomeAsyncTask(new WeakReference<WelcomeActivity>(WelcomeActivity.this));
            welcomeAsyncTask.execute();
        }
    }

}


