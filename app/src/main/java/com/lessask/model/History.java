package com.lessask.model;

/**
 * Created by JHuang on 2015/8/27.
 */
public class History {
    private int userid;
    private int friendid;
    private int id;

    public History(int userid, int friendid, int id) {
        this.userid = userid;
        this.friendid = friendid;
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getFriendid() {
        return friendid;
    }

    public void setFriendid(int friendid) {
        this.friendid = friendid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
