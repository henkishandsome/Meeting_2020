package com.example.meeting2020.Meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.Bean.Signin;
import com.example.meeting2020.HttpConnect.HttpUtil;
import com.example.meeting2020.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SigninedUserListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signined_user_list);
        recyclerView = findViewById(R.id.slideRecyclerView);
        final Bundle bundle = getIntent().getExtras();
        Meeting meeting = null;
        if (bundle != null) {
            meeting = (Meeting) bundle.getSerializable("meeting");
        }
        final Meeting finalMeeting = meeting;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = HttpUtil.BASE_URL + "signin/signinedMeetingListByMeetingid.do";
                Map<String, String> map = new HashMap<String, String>();
                map.put("meetingid", String.valueOf(finalMeeting.getMeetingid()));
                HttpUtil.sendOkHttpRequestText(url, map, new okhttp3.Callback() {

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String result = response.body().string();
                        if (result.equals("{}\r\n")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SigninedUserListActivity.this, "暂无签到人员", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        JSONArray jsonArray = JSONArray.parseArray(result);
                        final List<Signin> signins = new ArrayList<Signin>();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (signins == null || signins.size() == 0) {
                                    Toast.makeText(SigninedUserListActivity.this, "暂无签到人员", Toast.LENGTH_LONG).show();
                                } else {
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SigninedUserListActivity.this);
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    UserAdapter userAdapter = new UserAdapter(signins);
                                    recyclerView.setAdapter(userAdapter);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(SigninedUserListActivity.this, "请求失败，请重试!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SigninedUserListActivity.this, MeetingManageActivity.class).putExtras(bundle));
                    }
                });
            }
        }).start();
    }

    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        private List<Signin> signins;

        public UserAdapter(List<Signin> signins) {
            this.signins = signins;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.signined_user_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            Signin signin = signins.get(position);
            holder.workid.setText(signin.getUserid());
            holder.name.setText(signin.getName());
        }

        @Override
        public int getItemCount() {
            return signins.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView workid;
            private TextView name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                workid = itemView.findViewById(R.id.workid);
                name = itemView.findViewById(R.id.name);
            }
        }
    }
}
