package com.example.meeting2020.JavaClass;

import android.app.Application;

import com.example.meeting2020.Bean.UserInfo;


public class Data extends Application {
    private Integer status;
    private UserInfo userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
