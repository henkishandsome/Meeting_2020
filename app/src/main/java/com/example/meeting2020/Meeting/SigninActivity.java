package com.example.meeting2020.Meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.Bean.Signin;
import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.MainActivity;
import com.example.meeting2020.R;
import com.example.meeting2020.zxing.Constants;
import com.example.meeting2020.zxing.activity.CaptureActivity;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;



public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        init();
    }

    public void init() {
        bt_scan = findViewById(R.id.bt_scan);
        bt_scan.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_scan:
                startQrCode();
                break;
        }
    }

    private void startQrCode() {
        if (ContextCompat.checkSelfPermission(SigninActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // android 6.0以上需要动态申请权限
            ActivityCompat.requestPermissions(SigninActivity.this, new String[]{Manifest.permission.CAMERA}, Constants.REQ_PERM_CAMERA);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(SigninActivity.this, CaptureActivity.class);
        startActivityForResult(intent, Constants.REQ_QR_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
//        Toast.makeText(SigninActivity.this, "扫描成功", Toast.LENGTH_SHORT).show();
        if (requestCode == Constants.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constants.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来
            signin(scanResult);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(SigninActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void signin(String scanResult) {
        SigninAsyncTask signinAsyncTask = new SigninAsyncTask(new WeakReference<SigninActivity>(SigninActivity.this));
        signinAsyncTask.execute(scanResult);
    }

    private static class SigninAsyncTask extends AsyncTask<String, Void, String> {
        private final WeakReference<SigninActivity> weakActivity;

        private SigninAsyncTask(WeakReference<SigninActivity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        protected String doInBackground(String... strings) {
            SigninActivity activity = weakActivity.get();
            final Data app = (Data) activity.getApplication();
            String url = HttpUtilsHttpURLConnection.BASE_URL + "signin/insertSignin.do";
            String[] idtime = strings[0].split(",");
            String meetingid = idtime[0];
            String time = idtime[1];
            LocalDateTime start = LocalDateTime.parse(time);
            Duration duration = Duration.between(start, LocalDateTime.now());
            if (duration.toMillis() > 3000) {
                return "overtime";
            }
            Signin signin = new Signin();
            signin.setMeetingid(Integer.parseInt(strings[0]));
            signin.setUserid(app.getUserInfo().getWorkid());
            signin.setName(app.getUserInfo().getName());
            signin.setTime(LocalDateTime.now());
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(signin);
            String result = HttpUtilsHttpURLConnection.getContextByHttp(url, jsonObject);
            JSONObject json = JSONObject.parseObject(result);
            if (json.getString("result").equals("success")) {
                url = HttpUtilsHttpURLConnection.BASE_URL + "meeting/selectMeeting.do";
                Map<String, String> map = new HashMap<String, String>();
                map.put("meetingid", strings[0]);
                result = HttpUtilsHttpURLConnection.getContextByHttp1(url, map);
                json = JSONObject.parseObject(result);
                Meeting meeting = JSONObject.parseObject(json.getString("result"), Meeting.class);
                Integer number = json.getInteger("number");
                Bundle bundle = new Bundle();
                bundle.putSerializable("meeting", meeting);
                bundle.putInt("number", number);
                activity.startActivity(new Intent(activity, MainActivity.class).putExtras(bundle));
                return "success";
            } else if (json.getString("result").equals("repeated")) {
                return "repeated";
            }
            return "failed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SigninActivity activity = weakActivity.get();
            if (s.equals("success")) {
                Toast.makeText(activity, "签到成功", Toast.LENGTH_LONG).show();
                activity.finish();
            } else if (s.equals("repeated")) {
                Toast.makeText(activity, "请不要重复签到", Toast.LENGTH_LONG).show();
            } else if (s.equals("overtime")) {
                Toast.makeText(activity, "二维码已失效，请重试!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "签到失败,请重试", Toast.LENGTH_LONG).show();
            }
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                // activity死亡了，不再做任何的事情
                return;
            }
        }
    }
}
