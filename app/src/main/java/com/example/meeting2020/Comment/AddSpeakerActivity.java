package com.example.meeting2020.Comment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meeting2020.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddSpeakerActivity extends AppCompatActivity {
private TextView tvMeetingTheme;
private EditText etSpeakerName;
private Button btn_addspeaker;
private static final String WEB_SITE="http://192.168.43.188:8082/HttpServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_speaker);
        tvMeetingTheme=findViewById(R.id.tvMeetingTheme);
        etSpeakerName=findViewById(R.id.etSpeakerName);
        btn_addspeaker=findViewById(R.id.btn_addspeaker);
        Bundle bundle=getIntent().getExtras();
        final String meeting_theme=bundle.getString("meeting_theme");
        tvMeetingTheme.setText(meeting_theme);
        btn_addspeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spraker_name=etSpeakerName.getText().toString();
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder().url(WEB_SITE+"/AddMeetingSpeaker"+"?meeting_theme="+meeting_theme
                +"&speaker_name="+spraker_name).build();
                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("请求失败");
             }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            System.out.println("请求成功");
            AddSpeakerActivity.this.finish();
             }
          });
            }
        });
    }
}
