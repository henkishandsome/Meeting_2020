package com.example.meeting2020.Meeting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.Comment.SpeakerListActivity;
import com.example.meeting2020.HttpConnect.HttpUtilsHttpURLConnection;
import com.example.meeting2020.JavaClass.Data;
import com.example.meeting2020.MainActivity;
import com.example.meeting2020.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class MeetingManageActivity extends AppCompatActivity {
    private Button button1, button2, submit, delete, remark, signinList;
    private Integer myear, mmonth, mday, mhour, mminute;
    private LocalDateTime localDateTime = null;
    private EditText theme, place, introduce;
    private TextView date, time, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manage);
        button1 = findViewById(R.id.btn1);
        button2 = findViewById(R.id.btn2);
        submit = findViewById(R.id.submit);
        delete = findViewById(R.id.delete);
        remark = findViewById(R.id.remark);
        signinList = findViewById(R.id.signinList);
        theme = findViewById(R.id.theme);
        place = findViewById(R.id.place);
        introduce = findViewById(R.id.introduce);
        date = findViewById(R.id.text2);
        time = findViewById(R.id.text3);
        number = findViewById(R.id.number);
        Integer meetingId = null;
        final Bundle bundle = getIntent().getExtras();
        Meeting meeting = null;
        if (bundle != null) {
            meeting = (Meeting) bundle.getSerializable("meeting");
        }
        if (meeting != null) {
            theme.setText(meeting.getTheme());
            place.setText(meeting.getPlace());
            introduce.setText(meeting.getIntroduce());
            DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss");
            meeting.getTime().format(dft);
            date.setText(meeting.getTime().toLocalDate().toString());
            time.setText(" " + meeting.getTime().toLocalTime().toString());
            number.setText(String.valueOf(meeting.getSigninnumber()));
            localDateTime = meeting.getTime();
            meetingId = meeting.getMeetingid();
        } else {
            delete.setVisibility(View.GONE);
        }
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(MeetingManageActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {
                            String month = null, day = null;

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                myear = year;
                                mmonth = monthOfYear + 1;
                                mday = dayOfMonth;
                                if (mmonth < 10) {
                                    month = "0" + mmonth;
                                } else {
                                    month = mmonth.toString();
                                }
                                if (mday < 10) {
                                    day = "0" + mday;
                                } else {
                                    day = mday.toString();
                                }
                                date.setText(year + "-" + month
                                        + "-" + day);
                            }
                        }
                        // 设置初始日期
                        , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new TimePickerDialog(MeetingManageActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            String hour = null, tminute = null;

                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                mhour = hourOfDay;
                                mminute = minute;
                                if (mhour < 10) {
                                    hour = "0" + mhour;
                                } else {
                                    hour = mhour.toString();
                                }
                                if (mminute < 10) {
                                    tminute = "0" + mminute;
                                } else {
                                    tminute = mminute.toString();
                                }
                                time.setText(" " + hour + ":" + tminute);
                            }
                        }
                        // 设置初始时间
                        , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                        // true表示采用24小时制
                        true).show();
            }
        });
        final Integer finalMeetingId = meetingId;
        remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalMeetingId == null) {
                    new AlertDialog.Builder(MeetingManageActivity.this).setTitle("提示").setMessage("请先提交会议").setPositiveButton("确认", null).show();
                } else {
                    startActivity(new Intent(MeetingManageActivity.this, SpeakerListActivity.class).putExtras(bundle));
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            private final Data app = (Data) getApplication();
            String mtheme, mplace, mintroduce;
            Integer meetingid = finalMeetingId;

            @Override
            public void onClick(View v) {
                final Meeting meeting = new Meeting();
                mtheme = theme.getText().toString();
                mplace = place.getText().toString();
                mintroduce = introduce.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(MeetingManageActivity.this).setTitle("提示").setMessage("是否确认提交?").setPositiveButton("确认", null).setNegativeButton("取消", null);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String hour = null, minute = null, month = null, day = null;

                    @Override
                    public void onClick(View v) {
                        if (localDateTime == null && (myear != null && mmonth != null && mday != null && mday != null && mhour != null && mminute != null && theme.getText().toString() != null
                                && theme.getText().toString() != "" && place.getText().toString() != null && place.getText().toString() != "")) {
                            if (mmonth < 10) {
                                month = "0" + mmonth;
                            } else {
                                month = mmonth.toString();
                            }
                            if (mday < 10) {
                                day = "0" + mday;
                            } else {
                                day = mday.toString();
                            }
                            if (mhour < 10 || mminute < 10) {
                                if (mhour < 10 && mminute < 10) {
                                    hour = "0" + mhour;
                                    minute = "0" + mminute;
                                    localDateTime = LocalDateTime.parse(myear + "-" + month + "-" + day + " " + hour + ":" + minute + ":00", df);
                                }
                                if (mhour < 10 && mminute >= 10) {
                                    hour = "0" + mhour;
                                    localDateTime = LocalDateTime.parse(myear + "-" + month + "-" + day + " " + hour + ":" + mminute + ":00", df);
                                }
                                if (mminute < 10 && mhour >= 10) {
                                    minute = "0" + mminute;
                                    localDateTime = LocalDateTime.parse(myear + "-" + month + "-" + day + " " + mhour + ":" + minute + ":00", df);
                                }
                            } else {
                                localDateTime = LocalDateTime.parse(myear + "-" + month + "-" + day + " " + mhour + ":" + mminute + ":00", df);
                            }
                            meeting.setPublisherid(app.getUserInfo().getWorkid());
                            meeting.setPublisher(app.getUserInfo().getName());
                            meeting.setTheme(mtheme);
                            meeting.setPlace(mplace);
                            meeting.setIntroduce(mintroduce);
                            meeting.setSigninnumber(0);
                            meeting.setTime(localDateTime);
                            MeetingManageAsyncTask meetingManageAsyncTask = new MeetingManageAsyncTask(new WeakReference<MeetingManageActivity>(MeetingManageActivity.this));
                            meetingManageAsyncTask.execute(meeting);
                        } else if (localDateTime != null) {
                            meeting.setMeetingid(meetingid);
                            meeting.setPublisherid(app.getUserInfo().getWorkid());
                            meeting.setPublisher(app.getUserInfo().getName());
                            meeting.setTheme(mtheme);
                            meeting.setPlace(mplace);
                            meeting.setIntroduce(mintroduce);
                            meeting.setTime(localDateTime);
                            MeetingManageAsyncTask meetingManageAsyncTask = new MeetingManageAsyncTask(new WeakReference<MeetingManageActivity>(MeetingManageActivity.this));
                            meetingManageAsyncTask.execute(meeting);
                        } else {
                            new AlertDialog.Builder(MeetingManageActivity.this).setTitle("提示").setMessage("请将信息补充完整").setPositiveButton("确认", null).show();
                        }
                    }
                });
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            Integer meetingid = finalMeetingId;

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MeetingManageActivity.this).setTitle("提示").setMessage("是否确认取消会议?").setPositiveButton("确认", null).setNegativeButton("取消", null);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MeetingDeleteAsyncTask meetingDeleteAsyncTask = new MeetingDeleteAsyncTask(new WeakReference<MeetingManageActivity>(MeetingManageActivity.this));
                        meetingDeleteAsyncTask.execute(meetingid);
                    }
                });
            }
        });
        signinList.setOnClickListener(new View.OnClickListener() {
            Integer meetingid = finalMeetingId;

            @Override
            public void onClick(View v) {
                if (meetingid == null) {
                    Toast.makeText(MeetingManageActivity.this, "暂无名单!", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(MeetingManageActivity.this, SigninedUserListActivity.class).putExtras(bundle));
                }
            }
        });
    }

    private static class MeetingManageAsyncTask extends AsyncTask<Meeting, Void, String> {

        private final WeakReference<MeetingManageActivity> weakActivity;

        private MeetingManageAsyncTask(WeakReference<MeetingManageActivity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        @Override
        protected String doInBackground(Meeting... meetings) {
            MeetingManageActivity activity = weakActivity.get();
            String url = HttpUtilsHttpURLConnection.BASE_URL + "meeting/manageMeeting.do";
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(meetings[0]);
            String result = HttpUtilsHttpURLConnection.getContextByHttp(url, jsonObject);
            JSONObject json = JSONObject.parseObject(result);
            if (json.getString("result").equals("success")) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                return "success";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MeetingManageActivity activity = weakActivity.get();
            if (s == null) {
                Toast.makeText(activity, "操作失败,请重试!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "操作成功", Toast.LENGTH_LONG).show();
                activity.finish();
            }
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                // activity死亡了，不再做任何的事情
                return;
            }
        }
    }

    private static class MeetingDeleteAsyncTask extends AsyncTask<Integer, Void, String> {

        private final WeakReference<MeetingManageActivity> weakActivity;

        MeetingDeleteAsyncTask(WeakReference<MeetingManageActivity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            MeetingManageActivity activity = weakActivity.get();
            String url = HttpUtilsHttpURLConnection.BASE_URL + "meeting/deleteMeeting.do";
            Map<String, String> map = new HashMap<String, String>();
            map.put("meetingid", integers[0].toString());
            String result = HttpUtilsHttpURLConnection.getContextByHttp1(url, map);
            JSONObject json = JSONObject.parseObject(result);
            if (json.getString("result").equals("success")) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                return "success";
            }
            return "failed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MeetingManageActivity activity = weakActivity.get();
            if (s.equals("success")) {
                Toast.makeText(activity, "取消会议成功", Toast.LENGTH_LONG).show();
                activity.finish();
            } else {
                Toast.makeText(activity, "取消会议失败,请重试!", Toast.LENGTH_LONG).show();
            }
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                // activity死亡了，不再做任何的事情
                return;
            }
            // The activity is still valid, do main-thread stuff here
        }
    }
}
