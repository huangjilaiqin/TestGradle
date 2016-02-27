package com.lessask.chat;

/**
 * Created by JHuang on 2016/2/27.
 * 聊天群信息
 */
public class ChatGroup {
    private String chatgroupId;
    private String name;

    public ChatGroup(String chatgroupId, String name) {
        this.chatgroupId = chatgroupId;
        this.name = name;
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
