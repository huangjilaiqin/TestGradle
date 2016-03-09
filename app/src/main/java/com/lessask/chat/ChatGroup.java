package com.lessask.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.lessask.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHuang on 2016/2/27.
 * 聊天群信息
 */
public class ChatGroup implements Parcelable {
    private String chatgroupId;
    private String name;
    private String img;
    private ArrayList<ChatMessage> messageList;
    //to do 一个高效的缓存结构


    @Override
    public boolean equals(Object o) {
        ChatGroup chatGroup = (ChatGroup)o;
        return this.chatgroupId.equals(chatGroup.getChatgroupId());
    }

    public ChatGroup(String chatgroupId) {
        this.chatgroupId = chatgroupId;
        //聊天列表缓存一定数量的消息，保证进入聊天界面不会因为查数据库而产生卡顿
        this.messageList = new ArrayList<ChatMessage>();
    }
    public ChatGroup(String chatgroupId, String name) {
        this.chatgroupId = chatgroupId;
        this.name = name;
        //聊天列表缓存一定数量的消息，保证进入聊天界面不会因为查数据库而产生卡顿
        this.messageList = new ArrayList<ChatMessage>();
    }

    public ChatGroup(String chatgroupId, String name, String img, ArrayList<ChatMessage> messageList) {
        this.chatgroupId = chatgroupId;
        this.name = name;
        this.img = img;
        this.messageList = messageList;
    }

    public String getMsg(){
        String content="";
        if(messageList.size()>0)
            content=messageList.get(0).getContent();
        return content;
    }

    public ChatMessage getLastMessage(){
        ChatMessage message = null;
        int size = messageList.size();
        if(size>0)
            message=messageList.get(size-1);
        return message;
    }

    public List getMessageList(){
        return messageList;
    }

    public void appendTopMsg(ChatMessage msg){
        messageList.add(0,msg);
    }
    public void appendMsg(ChatMessage msg){
        messageList.add(msg);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatgroupId);
        dest.writeString(name);
        dest.writeString(img);
        dest.writeList(messageList);
    }

    public static final Parcelable.Creator<ChatGroup> CREATOR
             = new Parcelable.Creator<ChatGroup>() {
         public ChatGroup createFromParcel(Parcel in) {
             String chatgroupId = in.readString();
             String name = in.readString();
             String img = in.readString();
             ArrayList<ChatMessage> messageList = in.readArrayList(ChatMessage.class.getClassLoader());
             return new ChatGroup(chatgroupId, name, img, messageList);
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
