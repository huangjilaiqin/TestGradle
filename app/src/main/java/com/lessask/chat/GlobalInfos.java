package com.lessask.chat;

import android.content.Intent;
import android.util.Log;

import com.lessask.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

/**
 * Created by huangji on 2015/8/12.
 */
public class GlobalInfos {

    private final static String TAG = GlobalInfos.class.getName();

    private int userid;
    private User user;
    //聊天信息
    private HashMap<Integer, ArrayList> chatContents;
    //历史记录id
    private HashMap<Integer, Integer> historyIds;
    //好友哈希表
    private HashMap<Integer, User> friendsinMap;
    //好友列表
    private ArrayList<User> friends;

    private File headImgDir;
    private String headImgHost;

    private int screenWidth;
    private int screenHeight;

    private GlobalInfos(){
        historyIds = new HashMap<>();
        chatContents = new HashMap<>();
    }
    public static final GlobalInfos getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final GlobalInfos INSTANCE = new GlobalInfos();
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getHistoryIds(int userId) {
        if(historyIds.get(userId)==null){
            historyIds.put(userId, -1);
        }
        return historyIds.get(userId);
    }

    public void setHistoryIds(int userId, int historyId) {
        historyIds.put(userId, historyId);
    }

    public String getHeadImgHost() {
        return headImgHost;
    }

    public void setHeadImgHost(String headImgHost) {
        this.headImgHost = headImgHost;
    }

    public File getHeadImgDir() {
        return headImgDir;
    }

    public void setHeadImgDir(File headImgDir) {
        this.headImgDir = headImgDir;
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
