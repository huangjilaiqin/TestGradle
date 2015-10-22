package com.lessask.action;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/10/22.
 */
public class ActionInfo {
    private String name;
    private ArrayList<Integer> tags;
    private ArrayList<String> notices;
    private String vedio;

    public ActionInfo(String name, ArrayList<Integer> tags, ArrayList<String> notices, String vedio) {
        this.name = name;
        this.tags = tags;
        this.notices = notices;
        this.vedio = vedio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Integer> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getNotices() {
        return notices;
    }

    public void setNotices(ArrayList<String> notices) {
        this.notices = notices;
    }

    public String getVedio() {
        return vedio;
    }

    public void setVedio(String vedio) {
        this.vedio = vedio;
    }
}
