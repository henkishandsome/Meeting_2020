package com.example.meeting2020.Meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.HttpConnect.HttpUtil;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.R;
//import com.example.meeting2020.MeetingListActivity.MeetingAdapter.ViewHolder;

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

public class MeetingListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btn;
    private MeetingAdapter meetingAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = findViewById(R.id.meetingList);
        btn = findViewById(R.id.insertMeeting);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MeetingListActivity.this, MeetingManageActivity.class));
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MeetingListThread().start();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new MeetingListThread().start();
    }

    public class MeetingListThread extends Thread {
        @Override
        public void run() {
            super.run();
            final Data app = (Data) getApplication();
            String url = HttpUtil.BASE_URL + "meeting/selectAllMeetingByPublisherId.do";
            Map<String, String> params = new HashMap<String, String>();
            params.put("workid", app.getUserInfo().getWorkid());
            HttpUtil.sendOkHttpRequestText(url, params, new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String result = response.body().string();
                    JSONArray jsonArray = JSONArray.parseArray(result);
                    final List<Meeting> meetings = new ArrayList<Meeting>();
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
                                Toast.makeText(MeetingListActivity.this, "暂无数据", Toast.LENGTH_LONG).show();
                            }
                            if (meetingAdapter == null) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MeetingListActivity.this);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                meetingAdapter = new MeetingAdapter(MeetingListActivity.this, meetings);
                                recyclerView.setAdapter(meetingAdapter);
                            } else {
                                meetingAdapter.setList(meetings);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MeetingListActivity.this, "请求失败，请重试!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        }
    }

    /*private static class MeetingListAsyncTask extends AsyncTask<Void, Void, List<Meeting>> {
        private final WeakReference<MeetingListActivity> weakActivity;
        List<Meeting> meetings = new ArrayList<Meeting>();

        private MeetingListAsyncTask(WeakReference<MeetingListActivity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        @Override
        protected List<Meeting> doInBackground(Void... voids) {
            MeetingListActivity activity = weakActivity.get();
            final Data app = (Data) activity.getApplication();
            String url = HttpUtilsHttpURLConnection.BASE_URL + "meeting/selectAllMeetingByPublisherId.do";
            Map<String, String> params = new HashMap<String, String>();
            params.put("workid", app.getUserInfo().getWorkid());
            String result = HttpUtilsHttpURLConnection.getContextByHttp1(url, params);
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
            return meetings;
        }

        @Override
        protected void onPostExecute(List<Meeting> meetings) {
            super.onPostExecute(meetings);
            MeetingListActivity activity = weakActivity.get();
            if (meetings != null && meetings.size() != 0) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                activity.recyclerView.setLayoutManager(linearLayoutManager);
                MeetingAdapter meetingAdapter = new MeetingAdapter(activity, meetings);
                activity.recyclerView.setAdapter(meetingAdapter);
            } else {
                Toast.makeText(activity, "暂无数据", Toast.LENGTH_LONG).show();
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                // activity死亡了，不再做任何的事情
                return;
            }
        }
    }*/

    public static class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

        private Activity activity;
        private List<Meeting> meetings;

        public MeetingAdapter(Activity activity, List<Meeting> meetings) {
            this.activity = activity;
            this.meetings = meetings;
        }

        public void setList(List<Meeting> meetings){
            this.meetings = meetings;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final Meeting meeting = meetings.get(position);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MeetingManageActivity.class);
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
