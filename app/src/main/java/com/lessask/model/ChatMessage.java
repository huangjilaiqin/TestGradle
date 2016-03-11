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

    //客户端id,每条消息的序号,发送时本地产生一个唯一的id,返回时用于那条消息，更新对应的状态
    private long id;
    private String chatgroupId;
    private int userid;
    //消息类型, 判断发送还是接受使用userid跟客户端的userid对应即可
    private int type;
    private String content;
    private Date time;
    //服务器统一的seq
    private int seq;
    //让服务器快速找到好友对应的连接
    private int friendid;
    //客户端使用 0:发送中, 1:已发送, 2:发送失败
    private int status;

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
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
             long id = in.readLong();
             String chatgroupId = in.readString();
             int type = in.readInt();
             String content = in.readString();
             Date time = (Date)in.readSerializable();
             int status = in.readInt();
             int seq = in.readInt();
             int userid = in.readInt();
             int friendid = in.readInt();
             return new ChatMessage(id,userid,chatgroupId,type,content,time,status,friendid,seq);
         }

         public ChatMessage[] newArray(int size) {
             return new ChatMessage[size];
         }
    };

    //发送消息的构造函数
    public ChatMessage(long id,int userid,String chatgroupId,int type,String content,Date time,int status,int friendid) {
        this.id = id;
        this.userid = userid;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.content = content;
        this.time = time;
        //客户端用
        this.status=status;
        //服务端用
        this.friendid = friendid;
    }
    //完整版
    public ChatMessage(long id,int userid,String chatgroupId,int type,String content,Date time,int status,int friendid,int seq) {
        this.id = id;
        this.userid = userid;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.content = content;
        this.time = time;
        //客户端用
        this.status=status;
        //服务端用
        this.friendid = friendid;
        this.seq = seq;
    }
    //读取数据库
    public ChatMessage(long id,int seq,int userid,String chatgroupId,int type,String content,Date time,int status) {
        this.id = id;
        this.userid = userid;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.content = content;
        this.time = time;
        //客户端用
        this.status=status;
        //服务端用
        this.seq = seq;
    }
    //写数据库
    public ChatMessage(int userid,String chatgroupId,int type,String content,Date time,int status) {
        this.userid = userid;
        this.chatgroupId = chatgroupId;
        this.type = type;
        this.content = content;
        this.time = time;
        //客户端用
        this.status=status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
