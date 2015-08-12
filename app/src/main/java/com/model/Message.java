package com.model;
/**
 * Created by JHuang on 2015/8/1.
 */
public class Message {
    public static final int TYPE_RECEIVED_TEXT = 0;
    public static final int TYPE_RECEIVED_SOUND = 1;
    public static final int TYPE_RECEIVED_IMAGE = 2;
    public static final int TYPE_SEND_TEXT = 3;
    public static final int TYPE_SEND_SOUND = 4;
    public static final int TYPE_SEND_IMAGE = 5;

    private int userid;
    private int friendid;
    private int type;
    private String content;
    //每条消息的序号
    private String seq;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
