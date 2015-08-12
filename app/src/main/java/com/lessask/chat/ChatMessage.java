package com.lessask.chat;

/**
 * Created by huangji on 2015/8/12.
 */
public class ChatMessage {

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
    private String time;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getType() {
        return type;
    }

    public int getFriendid() {
        return friendid;
    }

    public void setFriendid(int friendid) {
        this.friendid = friendid;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
