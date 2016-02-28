package com.lessask.model;

/**
 * Created by JHuang on 2015/8/12.
 */
public class ChatMessageResponse extends ResponseError{
    private int id;
    private String chatgroupId;
    private int seq;

    public ChatMessageResponse(int errno, String error,int id, String chatgroupId, int seq) {
        super(errno, error);
        this.id=id;
        this.chatgroupId = chatgroupId;
        this.seq = seq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatgroupId() {
        return chatgroupId;
    }

    public void setChatgroupId(String chatgroupId) {
        this.chatgroupId = chatgroupId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
