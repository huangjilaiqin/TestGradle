package com.lessask.model;
/**
 * Created by JHuang on 2015/8/1.
 */
public class ChatMessage extends ResponseError {
    //用于判断显示使用的view类型
    public static final int VIEW_TYPE_RECEIVED= 0;
    public static final int VIEW_TYPE_SEND= 1;
    public static final int VIEW_TYPE_TIME = 2;

    public static final int MSG_TYPE_TEXT = 0;
    public static final int MSG_TYPE_IMG = 1;
    public static final int MSG_TYPE_FILE = 2;
    public static final int MSG_TYPE_VOICE = 3;
    public static final int MSG_TYPE_VIDEO = 4;

    private int id;
    private int userid;
    private int friendid;
    private String chatgroupId;
    private int type;
    private String content;
    private String time;
    //每条消息的序号
    private int seq;
    //客户端使用
    private int viewType;

    //发送消息的构造函数
    public ChatMessage(int userid, String chatgroupId, int type, String content, String time, int seq, int viewType) {
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.userid = userid;
        this.content = content;
        this.time = time;
        this.seq = seq;
        this.viewType = viewType;
    }
    public ChatMessage(int userid,int friendid, String chatgroupId, int type, String content, String time, int seq, int viewType) {
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.friendid=friendid;
        this.userid = userid;
        this.content = content;
        this.time = time;
        this.seq = seq;
        this.viewType = viewType;
    }
    public ChatMessage(int id,int userid, String chatgroupId, int type, String content, String time) {
        this.id = id;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.userid = userid;
        this.content = content;
        this.time = time;
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

    public String getChatgroupId() {
        return chatgroupId;
    }

    public void setChatgroupId(String chatgroupId) {
        this.chatgroupId = chatgroupId;
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
    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
