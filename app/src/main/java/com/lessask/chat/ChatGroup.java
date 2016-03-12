package com.lessask.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.lessask.model.ChatMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JHuang on 2016/2/27.
 * 聊天群信息
 */
public class ChatGroup implements Parcelable,Comparable<ChatGroup> {
    private String chatgroupId;
    private int status;     //1:置顶,0:非置顶
    private String name;
    private String img;
    private int unreadCout;     //未读消息条数
    private ArrayList<ChatMessage> messageList;
    private String TAG = ChatGroup.class.getSimpleName();
    //to do 一个高效的缓存结构


    @Override
    public boolean equals(Object o) {
        ChatGroup chatGroup = (ChatGroup)o;
        return this.chatgroupId.equals(chatGroup.getChatgroupId());
    }

    @Override
    public int compareTo(ChatGroup another) {
        //排序字段 消息时间,name
        //a>b 正序返回1,逆序返回-1
        ArrayList<ChatMessage> anotherMsg = (ArrayList<ChatMessage>)another.getMessageList();
        Date chatTime;
        if(messageList.size()!=0)
            chatTime=messageList.get(messageList.size()-1).getTime();
        else
            chatTime = new Date(0);
        Date anotherChatTime;
        if(anotherMsg.size()!=0)
            anotherChatTime=anotherMsg.get(anotherMsg.size()-1).getTime();
        else
            anotherChatTime = new Date(0);

        if(status>another.getStatus()){
            return -1;
        }else if(status<another.getStatus()){
            return 1;
        }else {
            //比较时间
            Log.e(TAG, name+" time:"+chatTime+", "+another.getName()+" time:"+anotherChatTime);
            long delta = chatTime.getTime() - anotherChatTime.getTime();
            if (delta>0) {
                Log.e(TAG, "-1");
                return -1;
            } else if (delta<0) {
                Log.e(TAG, "1");
                return 1;
            } else {
                //比较名字
                if(another.getName()==null)
                    return -1;
                return name.compareTo(another.getName());
            }
        }
    }

    public ChatGroup(String chatgroupId) {
        this.chatgroupId = chatgroupId;
        //聊天列表缓存一定数量的消息，保证进入聊天界面不会因为查数据库而产生卡顿
        this.messageList = new ArrayList<ChatMessage>();
    }
    public ChatGroup(String chatgroupId,String name) {
        this.chatgroupId = chatgroupId;
        this.name = name;
        //聊天列表缓存一定数量的消息，保证进入聊天界面不会因为查数据库而产生卡顿
        this.messageList = new ArrayList<ChatMessage>();
    }
    public ChatGroup(String chatgroupId, String name,int status) {
        this.chatgroupId = chatgroupId;
        this.name = name;
        this.status = status;
        //聊天列表缓存一定数量的消息，保证进入聊天界面不会因为查数据库而产生卡顿
        this.messageList = new ArrayList<ChatMessage>();
    }

    public ChatGroup(String chatgroupId, String name,int status,String img,int unreadCout, ArrayList<ChatMessage> messageList) {
        this.chatgroupId = chatgroupId;
        this.name = name;
        this.img = img;
        this.status = status;
        this.unreadCout = unreadCout;
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

    public ArrayList<ChatMessage> getMessageList(){
        return messageList;
    }

    public void appendTopMsg(ChatMessage msg){
        messageList.add(0,msg);
    }
    public void appendMsg(ChatMessage msg){
        messageList.add(msg);
    }

    public void appendList(List<ChatMessage> list){
        if(messageList==null)
            messageList = new ArrayList<>();
        messageList.addAll(list);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatgroupId);
        dest.writeString(name);
        dest.writeInt(status);
        dest.writeString(img);
        dest.writeInt(unreadCout);
        dest.writeTypedList(messageList);
    }

    public static final Parcelable.Creator<ChatGroup> CREATOR
             = new Parcelable.Creator<ChatGroup>() {
         public ChatGroup createFromParcel(Parcel in) {
             String chatgroupId = in.readString();
             String name = in.readString();
             int status = in.readInt();
             String img = in.readString();
             int unreadCount = in.readInt();
             ArrayList list = new ArrayList();
             in.readTypedList(list,ChatMessage.CREATOR);
             return new ChatGroup(chatgroupId,name,status,img,unreadCount,list);
         }
         public ChatGroup[] newArray(int size) {
             return new ChatGroup[size];
         }
    };

    public void setMessageList(ArrayList<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    public int getUnreadCout() {
        return unreadCout;
    }

    public void setUnreadCout(int unreadCout) {
        this.unreadCout = unreadCout;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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
