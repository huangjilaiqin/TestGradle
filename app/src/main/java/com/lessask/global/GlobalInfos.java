package com.lessask.global;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lessask.chat.ChatGroup;
import com.lessask.chat.MessageAdapter;
import com.lessask.model.ActionItem;
import com.lessask.model.User;
import com.lessask.action.ActionTagsHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huangji on 2015/8/12.
 */
public class GlobalInfos {

    private final static String TAG = GlobalInfos.class.getName();

    private int userId;
    private String token;
    private User user;
    //聊天信息
    private HashMap<String, ArrayList> chatContents;
    //历史记录id
    private HashMap<Integer, Integer> historyIds;
    //好友哈希表
    private HashMap<Integer, User> friendsinMap;
    //好友列表
    private ArrayList<User> friends;
    private HashMap<Integer, ActionItem> actionsInfo;

    //聊天列表
    private ArrayList<ChatGroup> chatGroups;
    private Set<String> chatGroupIds;

    private File headImgDir;
    private String headImgHost;

    private int screenWidth;
    private int screenHeight;
    private ActionTagsHolder actionTagsHolder;
    private Config config;

    private GlobalInfos(){
        historyIds = new HashMap<>();
        chatContents = new HashMap<>();
        actionsInfo = new HashMap<>();
        actionTagsHolder = new ActionTagsHolder();
        config = new Config();
    }

    public SQLiteDatabase getDb(Context context){
        return DbHelper.getInstance(context).getDb();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<ChatGroup> getChatGroups() {
        return chatGroups;
    }

    public void addChatGroups(ArrayList<ChatGroup> chatGroups) {
        if(this.chatGroups==null)
            this.chatGroups = new ArrayList<>();
        this.chatGroups.addAll(chatGroups);

        if(chatGroupIds==null)
            chatGroupIds=new HashSet<>();
        for(int i=0;i<chatGroups.size();i++)
            chatGroupIds.add(chatGroups.get(i).getChatgroupId());
    }
    public void addChatGroup(ChatGroup chatGroup) {
        if(this.chatGroups==null)
            this.chatGroups = new ArrayList<>();
        this.chatGroups.add(chatGroup);

        if(chatGroupIds==null)
            chatGroupIds=new HashSet<>();
        chatGroupIds.add(chatGroup.getChatgroupId());
    }

    public boolean hasChatGroupId(String chatGroupId){
        if(chatGroupIds==null){
            chatGroups=new ArrayList<>();
            chatGroupIds=new HashSet<>();
        }
        return chatGroupIds.contains(chatGroupId);
    }

    public static final GlobalInfos getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final GlobalInfos INSTANCE = new GlobalInfos();
    }


    public ArrayList<ActionItem> getActions(){
        Collection<ActionItem> collection = actionsInfo.values();
        if(collection instanceof List)
            return (ArrayList) collection;
        else
            return new ArrayList<>(collection);
    }

    public void addActions(ArrayList<ActionItem> actionItems){
        for (int i=0;i<actionItems.size();i++){
            ActionItem action = actionItems.get(i);
            actionsInfo.put(action.getId(), action);
        }
    }
    public void addAction(ActionItem actionItem){
        actionsInfo.put(actionItem.getId(), actionItem);
    }

    public ActionItem getActionById(int id){
        return actionsInfo.get(id);
    }
    public void deleteAction(int id){
        actionsInfo.remove(id);
    }

    public Config getConfig(){
        return config;
    }
    public ActionTagsHolder getActionTagsHolder(){
        return actionTagsHolder;
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

    public void setUser(User user){
        Log.e(TAG, "globalInfos set user");
        this.user = user;
    }
    public User getUser(){
        return this.user;
    }

    //好友消息用好友id, 群消息用群id
    public ArrayList getChatContent(String id){
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

    public User getFriend(int id){
        User user = friends.get(id);
        if(user==null){

        }
        return user;
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
        this.friendsinMap.put(userId, user);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
