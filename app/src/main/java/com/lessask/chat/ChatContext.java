package com.lessask.chat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huangji on 2015/8/12.
 */
public class ChatContext {

    private HashMap<Integer, ArrayList> chatContents;

    private ChatContext(){
        chatContents = new HashMap<>();
    }
    public static final ChatContext getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final ChatContext INSTANCE = new ChatContext();
    }

    //好友消息用好友id, 群消息用群id
    public ArrayList getChatContent(int id){
        ArrayList chatContent = chatContents.get(id);
        if(chatContent == null){
            chatContent = new ArrayList();
            chatContents.put(id, chatContent);
        }
        return chatContent;
    }
}
