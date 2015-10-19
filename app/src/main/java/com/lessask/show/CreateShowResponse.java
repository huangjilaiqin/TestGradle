package com.lessask.show;


import java.util.Date;

/**
 * Created by huangji on 2015/10/19.
 */
public class CreateShowResponse {
    int showid;
    String time;

    public CreateShowResponse(int showid, String time) {
        this.showid = showid;
        this.time = time;
    }

    public int getShowid() {
        return showid;
    }

    public void setShowid(int showid) {
        this.showid = showid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
