package com.lessask.show;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by huangji on 2015/10/19.
 */
public class CreateShowResponse {
    int showid;
    String time;
    ArrayList<String> pictures;

    public CreateShowResponse(int showid, String time, ArrayList<String> pictures) {
        this.showid = showid;
        this.time = time;
        this.pictures = pictures;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
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
