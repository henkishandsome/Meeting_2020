package com.example.meeting2020.Meeting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.meeting2020.Bean.Meeting;
import com.example.meeting2020.Comment.SpeakerListActivity;
import com.example.meeting2020.R;

import java.time.format.DateTimeFormatter;

public class MeetingInfoActivity extends AppCompatActivity {

    private TextView theme, time, place, number, introduce;
    private Button comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);
        theme = findViewById(R.id.theme);
        time = findViewById(R.id.time);
        place = findViewById(R.id.place);
        number = findViewById(R.id.number);
        introduce = findViewById(R.id.introduce);
        comment = findViewById(R.id.comment);
        final Bundle bundle = getIntent().getExtras();
        Meeting meeting = null;
        if (bundle != null) {
            meeting = (Meeting) bundle.getSerializable("meeting");
            theme.setText(meeting.getTheme());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            time.setText(meeting.getTime().format(dtf));
            place.setText(meeting.getPlace());
            number.setText(String.valueOf(meeting.getSigninnumber()));
            introduce.setText(meeting.getIntroduce());
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MeetingInfoActivity.this, SpeakerListActivity.class).putExtras(bundle));
                }
            });
        }
    }
}
