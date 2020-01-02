package com.example.meeting2020.Meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.Bean.Signin;
import com.example.meeting2020.HttpConnect.HttpUtil;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.MainActivity;
import com.example.meeting2020.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SigninedMeetingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signined_meeting_list);
        recyclerView = findViewById(R.id.recyclerView);
        new Thread(new Runnable() {
            final Data app = (Data) getApplication();

            @Override
            public void run() {
                String url = HttpUtil.BASE_URL + "signin/signinedMeetingListByUserid.do";
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", app.getUserInfo().getWorkid());
                HttpUtil.sendOkHttpRequestText(url, map, new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SigninedMeetingListActivity.this, "请求失败，请重试!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SigninedMeetingListActivity.this, MainActivity.class));
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String result = response.body().string();
                        if (result.equals("{}\r\n")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SigninedMeetingListActivity.this, "暂无会议历史!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        } else {
                            JSONArray jsonArray = JSONArray.parseArray(result);
                            List<Signin> signins = new ArrayList<Signin>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                Integer meetingid = json.getInteger("meetingid");
                                String userid = json.getString("userid");
                                String name = json.getString("name");
                                LocalDateTime time = json.getObject("time", LocalDateTime.class);
                                Signin signin = new Signin();
                                signin.setMeetingid(meetingid);
                                signin.setUserid(userid);
                                signin.setName(name);
                                signin.setTime(time);
                                signins.add(signin);
                            }
                            JSONArray json = (JSONArray) JSONArray.toJSON(signins);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("signins", json.toJSONString());
                            String url = HttpUtil.BASE_URL + "meeting/selectAllMeetingByMeetingid.do";
                            HttpUtil.sendOkHttpRequestText(url, map, new okhttp3.Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SigninedMeetingListActivity.this, "请求失败，请重试!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String result = response.body().string();
                                    final List<Meeting> meetings = new ArrayList<>();
                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JSONObject json = jsonArray.getJSONObject(i);
                                        Integer meetingId = json.getInteger("meetingid");
                                        String publisherId = json.getString("publisherid");
                                        String publisher = json.getString("publisher");
                                        String theme = json.getString("theme");
                                        String place = json.getString("place");
                                        Integer signinnumber = json.getInteger("signinnumber");
                                        String introduce = json.getString("introduce");
                                        LocalDateTime time = json.getObject("time", LocalDateTime.class);
                                        Meeting meeting = new Meeting();
                                        meeting.setMeetingid(meetingId);
                                        meeting.setPublisherid(publisherId);
                                        meeting.setPublisher(publisher);
                                        meeting.setTheme(theme);
                                        meeting.setPlace(place);
                                        meeting.setSigninnumber(signinnumber);
                                        meeting.setTime(time);
                                        meeting.setIntroduce(introduce);
                                        meetings.add(meeting);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (meetings == null || meetings.size() == 0) {
                                                Toast.makeText(SigninedMeetingListActivity.this, "暂无数据", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(SigninedMeetingListActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SigninedMeetingListActivity.this);
                                                recyclerView.setLayoutManager(linearLayoutManager);
                                                SigninedMeetingListAdater signinedMeetingListAdater = new SigninedMeetingListAdater(SigninedMeetingListActivity.this, meetings);
                                                recyclerView.setAdapter(signinedMeetingListAdater);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    public static class SigninedMeetingListAdater extends RecyclerView.Adapter<SigninedMeetingListAdater.ViewHolder> {

        private Activity activity;
        private List<Meeting> meetings;

        public SigninedMeetingListAdater(Activity activity, List<Meeting> meetings) {
            super();
            this.activity = activity;
            this.meetings = meetings;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.signined_meeting_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Meeting meeting = meetings.get(position);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MeetingInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("meeting", meeting);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            });
            holder.meetingid.setText(String.valueOf(meeting.getMeetingid()));
            holder.theme.setText(meeting.getTheme());
            holder.time.setText(meeting.getTime().format(dtf));
            holder.introduce.setText(meeting.getIntroduce());
            holder.place.setText(meeting.getPlace());
            holder.num.setText(String.valueOf(meeting.getSigninnumber()));
        }

        @Override
        public int getItemCount() {
            return meetings.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView meetingid;
            private TextView theme;
            private TextView time;
            private TextView introduce;
            private TextView place;
            private TextView num;
            private LinearLayout content;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                meetingid = itemView.findViewById(R.id.meetingId);
                theme = itemView.findViewById(R.id.theme);
                time = itemView.findViewById(R.id.time);
                introduce = itemView.findViewById(R.id.introduce);
                place = itemView.findViewById(R.id.place);
                num = itemView.findViewById(R.id.number);
                content = itemView.findViewById(R.id.content);
            }
        }
    }
}
