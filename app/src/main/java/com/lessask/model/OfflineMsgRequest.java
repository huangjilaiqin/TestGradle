package com.lessask.model;

import java.util.Map;

/**
 * Created by laiqin on 16/3/12.
 */
public class OfflineMsgRequest {
    private int userid;
    private Map<String, Integer> args;

    public OfflineMsgRequest() {
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Map<String, Integer> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Integer> args) {
        this.args = args;
    }
}
