package com.lessask.chat;

import android.util.Log;

import com.lessask.model.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huangji on 2015/8/12.
 */
public class GlobalInfos {

    private final static String TAG = GlobalInfos.class.getName();

    private int userid;
    private User user;
    //聊天信息
    private HashMap<Integer, ArrayList> chatContents;
    //好友哈希表
    private HashMap<Integer, User> friendsinMap;
    //好友列表
    private ArrayList<User> friends;

    private GlobalInfos(){
        chatContents = new HashMap<>();
    }
    public static final GlobalInfos getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final GlobalInfos INSTANCE = new GlobalInfos();
    }

    public void setUser(int userid, User user){
        Log.e(TAG, "globalInfos set user");
        this.user = user;
    }
    public User getUser(){
        return this.user;
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
    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public HashMap<Integer, User> getFriendsinMap() {
        return friendsinMap;
    }

    public void setFriendsinMap(HashMap<Integer, User> friends) {
        this.friendsinMap = friends;
        //将用户自己的信息添加进来
        this.friendsinMap.put(userid, user);
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
