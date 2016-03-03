package com.lessask.chat;

import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by JHuang on 2016/2/27.
 * 聊天群信息
 */
public class ChatGroup implements Parcelable {
    private String chatgroupId;
    private String name;
    private String img;
    private List<Message> messageList;
    //to do 一个高效的缓存结构


    public ChatGroup(String chatgroupId, String name) {
        this.chatgroupId = chatgroupId;
        this.name = name;
        //聊天列表缓存一定数量的消息，保证进入聊天界面不会因为查数据库而产生卡顿
        this.messageList = new LinkedList<>();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatgroupId);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<ChatGroup> CREATOR
             = new Parcelable.Creator<ChatGroup>() {
         public ChatGroup createFromParcel(Parcel in) {
             String chatgroupId = in.readString();
             String name = in.readString();
             return new ChatGroup(chatgroupId, name);
         }

         public ChatGroup[] newArray(int size) {
             return new ChatGroup[size];
         }
    };

    public String getChatgroupId() {
        return chatgroupId;
    }

    public void setChatgroupId(String chatgroupId) {
        this.chatgroupId = chatgroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
