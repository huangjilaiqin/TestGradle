package com.lessask.lesson;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/10/21.
 */
public class LessonInfo {
    private String name;
    private int time;
    private String address;
    private ArrayList<String> tags;

    public LessonInfo(String name, int time, String address, ArrayList<String> tags) {
        this.name = name;
        this.time = time;
        this.address = address;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
