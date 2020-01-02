package com.example.meeting2020.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.Bean.Signin;
import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.Meeting.MeetingInfoActivity;
import com.example.meeting2020.Meeting.SigninedMeetingListActivity;
import com.example.meeting2020.R;
import com.example.meeting2020.zxing.Constants;
import com.example.meeting2020.zxing.activity.CaptureActivity;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;



import static android.app.Activity.RESULT_OK;


public class ThirdFragment extends Fragment {
    private GridView gridView;
    private int[] iconarray = {R.drawable.meetingmanage, R.drawable.signin, R.drawable.draw};
    private String[] name = {"会议列表", "签到", "抽奖"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_third, container, false);
        gridView = view.findViewById(R.id.grid);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Gridadapter gridadapter = new Gridadapter(getActivity(), name, iconarray);
        gridView.setAdapter(gridadapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), SigninedMeetingListActivity.class));
                        break;
                    case 1:
                        startQrCode();
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(), null));
                        break;
                    default:
                }
            }
        });
    }

    private void startQrCode() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // android 6.0以上需要动态申请权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.REQ_PERM_CAMERA);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
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
                    Toast.makeText(getActivity(), "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void signin(String scanResult) {
        SigninAsyncTask signinAsyncTask = new SigninAsyncTask(new WeakReference<ThirdFragment>(ThirdFragment.this));
        signinAsyncTask.execute(scanResult);
    }

    private static class SigninAsyncTask extends AsyncTask<String, Void, String> {
        private final WeakReference<ThirdFragment> weakActivity;

        private SigninAsyncTask(WeakReference<ThirdFragment> weakActivity) {
            this.weakActivity = weakActivity;
        }

        protected String doInBackground(String... strings) {
            ThirdFragment fragment = weakActivity.get();
            final Data app = (Data) fragment.getActivity().getApplication();
            String url = HttpUtilsHttpURLConnection.BASE_URL + "signin/insertSignin.do";
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String[] idtime = strings[0].split(",");
            String meetingid = idtime[0];
            String time = idtime[1];
            LocalDateTime start = LocalDateTime.parse(time, df);
            Duration duration = Duration.between(start, LocalDateTime.now());
            if (duration.toMillis() > 3000) {
                return "overtime";
            }
            Signin signin = new Signin();
            signin.setMeetingid(Integer.parseInt(meetingid));
            signin.setUserid(app.getUserInfo().getWorkid());
            signin.setName(app.getUserInfo().getName());
            signin.setTime(LocalDateTime.now());
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(signin);
            String result = HttpUtilsHttpURLConnection.getContextByHttp(url, jsonObject);
            JSONObject json = JSONObject.parseObject(result);
            if (json.getString("result").equals("success")) {
                url = HttpUtilsHttpURLConnection.BASE_URL + "meeting/selectMeeting.do";
                Map<String, String> map = new HashMap<String, String>();
                map.put("meetingid", meetingid);
                result = HttpUtilsHttpURLConnection.getContextByHttp1(url, map);
                json = JSONObject.parseObject(result);
                Meeting meeting = JSONObject.parseObject(json.getString("result"), Meeting.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("meeting", meeting);
                fragment.getActivity().startActivity(new Intent(fragment.getActivity(), MeetingInfoActivity.class).putExtras(bundle));
                return "success";
            } else if (json.getString("result").equals("repeated")) {
                return "repeated";
            }
            return "failed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ThirdFragment fragment = weakActivity.get();
            if (s.equals("success")) {
                Toast.makeText(fragment.getActivity(), "签到成功", Toast.LENGTH_LONG).show();
                fragment.getActivity().finish();
            } else if (s.equals("repeated")) {
                Toast.makeText(fragment.getActivity(), "请不要重复签到", Toast.LENGTH_LONG).show();
            } else if (s.equals("overtime")) {
                Toast.makeText(fragment.getActivity(), "二维码已失效，请重试!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(fragment.getActivity(), "签到失败,请重试", Toast.LENGTH_LONG).show();
            }
            if (fragment.getActivity() == null || fragment.getActivity().isFinishing() || fragment.getActivity().isDestroyed()) {
                // activity死亡了，不再做任何的事情
                return;
            }
        }
    }

    private class Gridadapter extends BaseAdapter {

        private LayoutInflater inflater;
        private String[] name;
        private int[] iconarray;

        public Gridadapter(Context context, String[] name, int[] iconarray) {
            this.inflater = LayoutInflater.from(context);
            this.name = name;
            this.iconarray = iconarray;
        }

        @Override
        public int getCount() {
            return name.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.function_grid_item, null, false);
                holder.iv = convertView.findViewById(R.id.img);
                holder.iv.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                holder.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.tv = convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.iv.setImageResource(iconarray[position]);
            holder.tv.setText(name[position]);
            return convertView;
        }

        private class ViewHolder {
            ImageView iv;
            TextView tv;
        }
    }
}
