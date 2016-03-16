package com.lessask.model;

/**
 * Created by laiqin on 16/3/16.
 */
public class ChangeName extends ResponseError{
    private int userid;
    private String name;

    public ChangeName(int userid, String name) {
        this.userid = userid;
        this.name = name;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
