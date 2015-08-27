package com.lessask.model;

/**
 * Created by JHuang on 2015/8/12.
 */
public class ChatMessageResponse extends ResponseError{
    private int id;
    private int friendid;
    private int seq;

    public ChatMessageResponse(int errno, String error, int id, int friendid, int seq) {
        super(errno, error);
        this.id = id;
        this.friendid = friendid;
        this.seq = seq;
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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
