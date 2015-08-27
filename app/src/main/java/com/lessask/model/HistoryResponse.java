package com.lessask.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by JHuang on 2015/8/27.
 */
public class HistoryResponse extends ResponseError{
    private int friendid;
    private ArrayList<ChatMessage> messages;

    public HistoryResponse(int friendid, ArrayList<ChatMessage> messages) {
        this.friendid = friendid;
        this.messages = messages;
    }

    public HistoryResponse(int errno, String error, int friendid, ArrayList<ChatMessage> messages) {
        super(errno, error);
        this.friendid = friendid;
        this.messages = messages;
    }

    public int getFriendid() {
        return friendid;
    }

    public void setFriendid(int friendid) {
        this.friendid = friendid;
    }

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ChatMessage> messages) {
        this.messages = messages;
    }
}
