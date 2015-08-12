package com.example.jhuang.myapplication;

import android.app.Application;

/**
 * Created by huangji on 2015/8/12.
 */
public class MyApplication extends Application{
    private int userid;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
