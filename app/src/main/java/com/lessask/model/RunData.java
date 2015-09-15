package com.lessask.model;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by JHuang on 2015/9/15.
 */
public class RunData {
    private int userid;
    private List<LatLng> myload;
    private List<Long> mytime;

    public RunData(int userid, List<LatLng> myload, List<Long> mytime) {
        this.userid = userid;
        this.mytime = mytime;
        this.myload = myload;
    }

    public List<LatLng> getMyload() {
        return myload;
    }

    public List<Long> getMytime() {
        return mytime;
    }

    public void setMyload(List<LatLng> myload) {
        this.myload = myload;
    }

    public void setMytime(List<Long> mytime) {
        this.mytime = mytime;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
