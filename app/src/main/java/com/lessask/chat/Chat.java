package com.lessask.chat;

import android.util.Log;

import io.socket.emitter.Emitter;
import io.socket.client.Ack;
import io.socket.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.ChatMessageResponse;
import com.lessask.model.HistoryResponse;
import com.lessask.model.ResponseError;
import com.lessask.model.RunDataResponse;
import com.lessask.model.User;
import com.lessask.model.Utils;
import com.lessask.net.LASocketIO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by huangji on 2015/8/11.
 */

public class Chat {
    final private static String TAG = Chat.class.getName();
    //private String chathost = "http://ws.otopcy.com";
    //private String chathost = "http://123.59.40.113:5002";
    //private String chathost = "http://ws.qqshidao2.com";
    private Socket mSocket;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private String chathost = config.getWsUrl();
    private Gson gson = new Gson();
    private HashMap<Integer, User> friendsMap;
    //更新不一样的activity应该有多个listener
    private DataChangeListener dataChangeListener;
    private LoginListener loginListener;
    private RegisterListener registerListener;
    private FriendsListener friendsListener;
    private ChangeUserInfoListener changeUserInfoListener;
    private HistoryListener historyListener;
    private UploadRunListener uploadRunListener;

    private Chat(){
        mSocket = LASocketIO.getSocket();
        //注册回调函数
        mSocket.on("message", onMessage);
        mSocket.on("messageResp", onMessageResp);
        mSocket.on("login", onLogin);
        mSocket.on("register", onRegister);
        mSocket.on("friendsInfo", onFriends);
        mSocket.on("changeUserInfo", onChangeUserInfo);
        mSocket.on("history", onHistory);
        mSocket.on("uploadrun", onUploadRun);

        friendsMap = globalInfos.getFriendsinMap();
    }

    public static final Chat getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final Chat INSTANCE = new Chat();
    }
    private Emitter.Listener onMessage = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onMessage:" + args[0].toString());
            //to do 响应message要有两套机制,一个是message回应, 一个是好友发过来的message
            //修改用户的信息列表
            //Type type = new TypeToken<Map<String, String>>(){}.getType();
            //Map<String, String> map = gson.fromJson(args[0].toString(), type);
            //String id = map.get("id");
            ChatMessage message = gson.fromJson(args[0].toString(), ChatMessage.class);
            message.setViewType(ChatMessage.VIEW_TYPE_RECEIVED);
            int friendId = message.getUserid();
            ArrayList mList = globalInfos.getChatContent(message.getFriendid());
            mList.add(message);
            if(globalInfos.getHistoryIds(friendId)==-1){
                globalInfos.setHistoryIds(friendId, message.getId());
            }
            /*
            Iterator ite = mList.iterator();
            while (ite.hasNext()){
                ChatMessage msg = (ChatMessage)ite.next();
                Log.e(TAG, msg.getContent());
            }
            */

            //通知当前聊天activity
            dataChangeListener.message(message.getFriendid(), message.getType());
            //通知消息列表更新
        }
    };
    private Emitter.Listener onMessageResp = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onMessageResp:" + args[0].toString());
            ChatMessageResponse response = gson.fromJson(args[0].toString(), ChatMessageResponse.class);
            dataChangeListener.messageResponse(response);
            if(globalInfos.getHistoryIds(response.getFriendid())==-1){
                globalInfos.setHistoryIds(response.getFriendid(), response.getId());
                Log.e(TAG, "historyId:" + globalInfos.getHistoryIds(response.getFriendid()));
            }
        }
    };
    private Emitter.Listener onLogin = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            loginListener.login(args[0].toString());
        }
    };
    private Emitter.Listener onRegister = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            registerListener.register(args[0].toString());
        }
    };

    private Emitter.Listener onChangeUserInfo = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            changeUserInfoListener.changeUserInfo(args[0].toString());
        }
    };
    private Emitter.Listener onHistory = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onHistory"+args[0].toString());
            HistoryResponse historyResponse= gson.fromJson(args[0].toString(), HistoryResponse.class);
            if (historyResponse.getErrno() != 0 || historyResponse.getError()!=null && historyResponse.getError().length() != 0) {
                historyListener.history(historyResponse,historyResponse.getFriendid(),0);
            }else {
                ArrayList<ChatMessage> messages = historyResponse.getMessages();
                if(messages.size()>0){
                    int myId = globalInfos.getUserid();
                    int friendId = 0;
                    //获取好友id
                    Iterator ite = messages.iterator();
                    ChatMessage msg = null;
                    while(friendId == 0 && ite.hasNext()){
                        msg = (ChatMessage)ite.next();
                        if(msg.getUserid() == myId)
                            friendId = msg.getFriendid();
                    }

                    ArrayList mList = globalInfos.getChatContent(friendId);
                    ite = messages.iterator();
                    while(ite.hasNext()){
                        msg = (ChatMessage)ite.next();
                        if(msg.getUserid()==myId){
                            msg.setViewType(ChatMessage.VIEW_TYPE_SEND);
                        }else {
                            msg.setViewType(ChatMessage.VIEW_TYPE_RECEIVED);
                        }
                        msg.setTime(Utils.formatTime4Chat(msg.getTime()));
                        mList.add(0, msg);
                        Log.e(TAG, msg.getId()+", userid:"+msg.getUserid()+", "+msg.getFriendid()+", history:"+msg.getContent());
                    }
                    ChatMessage message = (ChatMessage)mList.get(0);
                    globalInfos.setHistoryIds(friendId, message.getId());
                    Log.e(TAG,"historyId:"+globalInfos.getHistoryIds(friendId));
                }
                historyListener.history(null, historyResponse.getFriendid(), messages.size());
            }
        }
    };
    private Emitter.Listener onUploadRun = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onUploadRun" + args[0].toString());
            RunDataResponse runDataResponse = gson.fromJson(args[0].toString(), RunDataResponse.class);
            if (runDataResponse.getErrno() != 0 || runDataResponse.getError() != null && runDataResponse.getError().length() != 0) {
                uploadRunListener.uploadRun(runDataResponse, runDataResponse.getUserid());
            } else {
                uploadRunListener.uploadRun(null, runDataResponse.getUserid());
            }
        }
    };
    private Emitter.Listener onFriends = new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            //这里应该处理数据,应为可能出现界面还没加载，协议已经返回的情况
            String data = args[0].toString();
            Log.e(TAG, "onFriends"+data);

            Type type = new TypeToken<ArrayList<User>>(){}.getType();
            ArrayList<User> originFriends = gson.fromJson(data, type);
            HashMap<Integer, User> friends = new HashMap<>();
            Iterator ite = originFriends.iterator();
            while (ite.hasNext()){
                User user = (User)ite.next();
                friends.put(user.getUserid(), user);
                Log.e(TAG, ""+user);
            }
            globalInfos.setFriends(originFriends);
            globalInfos.setFriendsinMap(friends);
            if(friendsListener!=null) {
                friendsListener.friendsInfo(args[0].toString());
            }
        }
    };


    public void emit(String event, Object... args){
        Log.e(TAG, event + ":" + args[0].toString());
        mSocket.emit(event, args);
    }
    public void emit(String event, Object[] args, Ack ask){
        mSocket.emit(event, args, ask);
    }


    public void setDataChangeListener(DataChangeListener dataChangeListener){
        this.dataChangeListener = dataChangeListener;
    }
    public interface DataChangeListener{
        void message(int friendId, int type);
        void messageResponse(ChatMessageResponse response);
    }
    public interface LoginListener{
        void login(String data);
    }
    public void setLoginListener(LoginListener listener){
        loginListener = listener;
    }
    public interface RegisterListener{
        void register(String data);
    }
    public void setRegisterListener(RegisterListener listener){
        this.registerListener = listener;
    }
    public interface FriendsListener{
        void friendsInfo(String data);
    }

    public void setFriendsListener(FriendsListener friendsListener) {
        this.friendsListener = friendsListener;
    }
    public interface ChangeUserInfoListener{
        void changeUserInfo(String data);
    }
    public void setChangeUserInfoListener(ChangeUserInfoListener listener){
        this.changeUserInfoListener = listener;
    }
    public interface HistoryListener{
        void history(ResponseError error, int friendid, int messageSize);
    }
    public void setHistoryListener(HistoryListener listener){
        this.historyListener = listener;
    }
    public interface UploadRunListener{
        void uploadRun(ResponseError error, int userId);
    }
    public void setUploadRunListener(UploadRunListener listener){
        this.uploadRunListener = listener;
    }
}

