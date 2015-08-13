package com.model;
/**
 * Created by JHuang on 2015/8/1.
 */
public class ChatMessage {
    public static final int TYPE_RECEIVED_TEXT = 0;
    public static final int TYPE_RECEIVED_SOUND = 1;
    public static final int TYPE_RECEIVED_IMAGE = 2;
    public static final int TYPE_SEND_TEXT = 3;
    public static final int TYPE_SEND_SOUND = 4;
    public static final int TYPE_SEND_IMAGE = 5;

    public static final int MSG_TYPE_TEXT = 0;
    public static final int MSG_TYPE_IMG = 1;
    public static final int MSG_TYPE_FILE = 2;
    public static final int MSG_TYPE_VOICE = 3;
    public static final int MSG_TYPE_VIDEO = 4;

    private int userid;
    private int friendid;
    private int type;
    private String content;
    private String time;
    //每条消息的序号
    private int seq;

    public ChatMessage(int userid, int friendid, int type, String content, String time, int seq) {
        this.friendid = friendid;
        this.type = type;
        this.userid = userid;
        this.content = content;
        this.time = time;
        this.seq = seq;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
