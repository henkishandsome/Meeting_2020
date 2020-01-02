package com.example.meeting2020.Bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Signin implements Serializable {
    private Integer meetingid;
    private String userid;
    private String name;
    private LocalDateTime time;

    public Integer getMeetingid() {
        return meetingid;
    }

    public void setMeetingid(int meetingid) {
        this.meetingid = meetingid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
