package com.lessask.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by JHuang on 2015/8/1.
 */
public class ChatMessage extends ResponseError implements Parcelable {
    /*
    //用于判断显示使用的view类型
    public static final int VIEW_TYPE_RECEIVED= 0;
    public static final int VIEW_TYPE_SEND= 1;
    public static final int VIEW_TYPE_TIME = 2;
    */

    //单数为自己
    public static final int MSG_TYPE_TEXT = 1;
    public static final int MSG_TYPE_TIME = 3;
    public static final int MSG_TYPE_IMG = 5;
    public static final int MSG_TYPE_FILE = 7;
    public static final int MSG_TYPE_VOICE = 9;
    public static final int MSG_TYPE_VIDEO = 11;
    public static final int MSG_TYPE_SIZE = 12;

    public static final int MSG_SENDING = 0;
    public static final int MSG_SEND = 1;
    public static final int MSG_SEND_FAILED= 2;
    public static final int MSG_RECEIVC= 2;

    private int id;
    private String chatgroupId;
    private int type;
    private String content;
    private Date time;
    //0:发送中, 1:已发送, 2:发送失败
    private int status;
    //每条消息的序号
    private int seq;
    //客户端使用
    //private int viewType;

    private int userid;
    private int friendid;

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(chatgroupId);
        dest.writeInt(type);
        dest.writeString(content);
        dest.writeSerializable(time);
        dest.writeInt(status);
        dest.writeInt(seq);
        dest.writeInt(userid);
        dest.writeInt(friendid);
    }

    public static final Parcelable.Creator<ChatMessage> CREATOR
             = new Parcelable.Creator<ChatMessage>() {
         public ChatMessage createFromParcel(Parcel in) {
             int id = in.readInt();
             String chatgroupId = in.readString();
             int type = in.readInt();
             String content = in.readString();
             Date time = (Date)in.readSerializable();
             int status = in.readInt();
             int seq = in.readInt();
             int userid = in.readInt();
             int friendid = in.readInt();
             return new ChatMessage(id,chatgroupId,type,content,time,status,seq,userid,friendid);
         }

         public ChatMessage[] newArray(int size) {
             return new ChatMessage[size];
         }
    };

    //发送消息的构造函数
    public ChatMessage(int userid,int friendid,String chatgroupId, int type, String content, Date time, int seq,int status) {
        this.chatgroupId = chatgroupId;
        this.friendid = friendid;
        this.type = type;
        this.userid = userid;
        this.content = content;
        this.time = time;
        this.seq = seq;
        this.status=status;
    }
    public ChatMessage(int id,String chatgroupId,int userid, int type, String content, Date time,int status,int seq) {
        this.id = id;
        this.userid = userid;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.content = content;
        this.time = time;
        this.status = status;
        this.seq = seq;
    }
    //入库消息构造函数
    public ChatMessage(int userid,String chatgroupId, int type, String content, Date time, int seq,int status) {
        this.chatgroupId = chatgroupId;
        this.friendid = friendid;
        this.type = type;
        this.userid = userid;
        this.content = content;
        this.time = time;
        this.seq = seq;
        this.status=status;
    }
    public ChatMessage(int id, String chatgroupId,int type,String content,Date time,int status,int seq,int userid,int friendid) {
        this.id=id;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.content = content;
        this.time = time;
        this.status = status;
        this.seq = seq;
        this.userid = userid;
        this.friendid=friendid;
    }
    public ChatMessage(int id,int userid, String chatgroupId, int type, String content, Date time) {
        this.id = id;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.userid = userid;
        this.content = content;
        this.time = time;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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
}
