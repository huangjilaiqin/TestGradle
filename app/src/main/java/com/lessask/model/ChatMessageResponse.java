package com.lessask.model;

/**
 * Created by JHuang on 2015/8/12.
 */
public class ChatMessageResponse extends ResponseError{
    private long id;
    private String chatgroupId;
    private int seq;
    private int status;

    public ChatMessageResponse(int errno, String error,long id, String chatgroupId, int seq) {
        super(errno, error);
        this.id=id;
        this.chatgroupId = chatgroupId;
        this.seq = seq;
    }
    public ChatMessageResponse(long id, String chatgroupId, int status) {
        this.id=id;
        this.chatgroupId = chatgroupId;
        this.status= status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
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
