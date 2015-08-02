package com.example.jhuang.myapplication;
/**
 * Created by JHuang on 2015/8/1.
 */
public class Message {
    public static final int TYPE_RECEIVED_TEXT = 0;
    public static final int TYPE_RECEIVED_SOUND = 1;
    public static final int TYPE_RECEIVED_IMAGE = 2;
    public static final int TYPE_SEND_TEXT = 3;
    public static final int TYPE_SEND_SOUND = 4;
    public static final int TYPE_SEND_IMAGE = 5;

    private int msgType;
    private String content;
    private int headImgId;

    public Message(int msgType, String content, int headImgId){
        this.msgType = msgType;
        this.content= content;
        this.headImgId = headImgId;
    }
    public int getMsgType() {
        return msgType;
    }
    public String getContent(){
        return this.content;
    }
    public int getHeadImgId(){
        return this.headImgId;
    }
}
