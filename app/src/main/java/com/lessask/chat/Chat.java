package com.lessask.chat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import io.socket.emitter.Emitter;
import io.socket.client.Ack;
import io.socket.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.ChatMessage;
import com.lessask.model.ChatMessageResponse;
import com.lessask.model.HistoryResponse;
import com.lessask.model.ResponseError;
import com.lessask.model.RunDataResponse;
import com.lessask.model.User;
import com.lessask.model.VerifyToken;
import com.lessask.util.DbUtil;
import com.lessask.util.TimeHelper;
import com.lessask.util.Utils;
import com.lessask.net.LASocketIO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;

/**
 * Created by huangji on 2015/8/11.
 */

public class Chat {
    final private static String TAG = Chat.class.getName();
    private Socket mSocket;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    //private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:MM:ss").create();
    private Gson gson = TimeHelper.gsonWithDate();
    private HashMap<Integer, User> friendsMap;
    //更新不一样的activity应该有多个listener
    private OnMessageResponseListener onMessageResponseListener;
    private LoginListener loginListener;
    private LoadInitDataListener loadInitDataListener;
    private VerifyTokenListener verifyTokenListener;
    private RegisterListener registerListener;
    private FriendsListener friendsListener;
    private ChangeUserInfoListener changeUserInfoListener;
    private HistoryListener historyListener;
    private UploadRunListener uploadRunListener;
    //to do Chat.getInstance 传入上下文,
    private static Context context;
    private Map<String, Set<Long>> sendingMsgs;
    private ArrayList<ChatResponseListener> verifyTokenListeners;

    private Chat(){
        mSocket = LASocketIO.getSocket();
        //注册回调函数
        mSocket.on("login", onLogin);
        mSocket.on("verifyToken", onVerifyToken);
        mSocket.on("loadInitData", onLoadInitData);
        mSocket.on("register", onRegister);
        mSocket.on("message", onMessage);
        mSocket.on("offlinemessage", onOfflineMessage);
        mSocket.on("messageResp", onMessageResp);
        mSocket.on("friendsInfo", onFriends);
        mSocket.on("changeUserInfo", onChangeUserInfo);
        mSocket.on("history", onHistory);
        mSocket.on("uploadrun", onUploadRun);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_ERROR, onError);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_RECONNECT, onReconnect);

        friendsMap = globalInfos.getFriendsinMap();
        sendingMsgs = new HashMap<>();
        verifyTokenListeners = new ArrayList<>();
    }

    public static final Chat getInstance(Context context){
        Chat.context = context;
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final Chat INSTANCE = new Chat();
    }

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onDisconnect");
            for (Map.Entry<String, Set<Long>> entry : sendingMsgs.entrySet()) {
                String chatgroupId = entry.getKey();
                for(Long id:entry.getValue()){
                    //update db
                    String[] whereValues = new String[]{id+"", chatgroupId};
                    ContentValues values = new ContentValues();
                    values.put("status", ChatMessage.MSG_SEND_FAILED);
                    DbHelper.getInstance(context).getDb().update("t_chatrecord",values,"id=? and chatgroup_id=?",whereValues);
                    //更新界面
                    ChatMessageResponse response = new ChatMessageResponse(id, chatgroupId, ChatMessage.MSG_SEND_FAILED);
                    if(onMessageResponseListener!=null)
                        onMessageResponseListener.messageResponse(response);
                }
                //清除掉没有发送成功,但已通知客户端的信息
                entry.getValue().clear();
            }
        }
    };
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onError");
            for (Map.Entry<String, Set<Long>> entry : sendingMsgs.entrySet()) {
                String chatgroupId = entry.getKey();
                for(Long id:entry.getValue()){
                    //update db
                    String[] whereValues = new String[]{id+"", chatgroupId};
                    ContentValues values = new ContentValues();
                    values.put("status", ChatMessage.MSG_SEND_FAILED);
                    DbHelper.getInstance(context).getDb().update("t_chatrecord",values,"id=? and chatgroup_id=?",whereValues);
                    //更新界面
                    ChatMessageResponse response = new ChatMessageResponse(id, chatgroupId, ChatMessage.MSG_SEND_FAILED);
                    if(onMessageResponseListener!=null)
                        onMessageResponseListener.messageResponse(response);
                }
                //清除掉没有发送成功,但已通知客户端的信息
                entry.getValue().clear();
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onConnectError");
            for (Map.Entry<String, Set<Long>> entry : sendingMsgs.entrySet()) {
                String chatgroupId = entry.getKey();
                for(Long id:entry.getValue()){
                    //update db
                    String[] whereValues = new String[]{id+"", chatgroupId};
                    ContentValues values = new ContentValues();
                    values.put("status", ChatMessage.MSG_SEND_FAILED);
                    DbHelper.getInstance(context).getDb().update("t_chatrecord",values,"id=? and chatgroup_id=?",whereValues);
                    //更新界面
                    ChatMessageResponse response = new ChatMessageResponse(id, chatgroupId, ChatMessage.MSG_SEND_FAILED);
                    if(onMessageResponseListener!=null)
                        onMessageResponseListener.messageResponse(response);
                }
                //清除掉没有发送成功,但已通知客户端的信息
                entry.getValue().clear();
            }
        }
    };
    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onReconnect");
            int userid = globalInfos.getUserId();
            String token = globalInfos.getToken();
            emit("verifyToken",gson.toJson(new VerifyToken(userid,token)));
        }
    };

    //收到信息,发送消息也会调用这里
    private Emitter.Listener onMessage = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onMessage:" + args[0].toString());

            ChatMessage message = gson.fromJson(args[0].toString(), ChatMessage.class);
            Log.e(TAG, "msg time:"+message.getTime()+", "+TimeHelper.dateFormat(message.getTime()));
            if(message.getErrno()!=0 || message.getError()!=null){
                Log.e(TAG, "error:"+message.getError());
                return;
            }

            //时间本地化, 最简单的方法, 接收到消息就用你手机自己的时间
            message.setTime(new Date());
            int userid = message.getUserid();
            int friendId = message.getFriendid();
            String chatGroupId = message.getChatgroupId();

            //区分是接受还是发送 并进行处理
            boolean isReceive = true;
            if(globalInfos.getUserId()==userid)
                isReceive=false;



            //第一次接收到信息 聊天列表 要增加一条记录
            if(!globalInfos.hasChatGroupId(chatGroupId)){
                String friendName;
                if(isReceive) {
                    User user = DbUtil.loadUserFromDb(context,userid);
                    friendName = user.getNickname();
                }else {
                    User user = DbUtil.loadUserFromDb(context,friendId);
                    friendName = user.getNickname();
                }

                ContentValues values = new ContentValues();
                values.put("chatgroup_id", chatGroupId);
                values.put("name", friendName);
                DbHelper.getInstance(context).insert("t_chatgroup", null, values);
            }

            //聊天消息入库
            ContentValues values = new ContentValues();
            values.put("chatgroup_id", chatGroupId);
            values.put("userid",""+message.getUserid());
            values.put("type", ""+message.getType());
            values.put("content", message.getContent());
            values.put("seq", message.getSeq());
            values.put("status", message.getStatus());
            values.put("time", TimeHelper.dateFormat(message.getTime()));
            values.put("friendid",friendId);
            DbHelper.getInstance(context).insert("t_chatrecord", null, values);

            //通知当前聊天activity
            //dataChangeListener.message(message.getUserid(), message.getType());
            //通知消息列表更新
        }
    };


    private Emitter.Listener onOfflineMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onOfflineMessage:" + args[0].toString());
            Type type = new TypeToken<ArrayListResponse<ChatMessage>>() {}.getType();
            //ArrayListResponse response = TimeHelper.gsonWithNodeDate().fromJson(args[0].toString(), type);
            ArrayListResponse response = TimeHelper.gsonWithNodeDate().fromJson(args[0].toString(), type);
            if(response.getErrno()!=0 || response.getError()!=null){
                Log.e(TAG, "error:"+response.getError());
                return;
            }else {
                List<ChatMessage> list = response.getDatas();
                if(list.size()>0) {
                    for (int i = 0; i < list.size(); i++) {
                        ChatMessage message = list.get(i);
                        ContentValues values = new ContentValues();
                        values.put("chatgroup_id", message.getChatgroupId());
                        values.put("userid", "" + message.getUserid());
                        values.put("type", "" + message.getType());
                        values.put("content", message.getContent());
                        values.put("seq", message.getSeq());
                        values.put("status", ChatMessage.MSG_RECEIVC);
                        Log.e(TAG, "time:" + message.getTime());
                        Log.e(TAG, "time:" + TimeHelper.dateFormat(message.getTime()));
                        values.put("time", TimeHelper.dateFormat(message.getTime()));
                        values.put("friendid", "-1");
                        DbHelper.getInstance(context).insert("t_chatrecord", null, values);
                    }
                    ContentValues values = new ContentValues();
                    values.put("unread_count", list.size());
                    String[] whereValues = new String[]{list.get(0).getChatgroupId()};
                    DbHelper.getInstance(context).getDb().update("t_chatgroup", values, "chatgroup_id=?", whereValues);
                }
            }
        }
    };

    private Emitter.Listener onMessageResp = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onMessageResp:" + args[0].toString());
            ChatMessageResponse response = gson.fromJson(args[0].toString(), ChatMessageResponse.class);
            if(response.getErrno()!=0 || response.getError()!=null){
                Log.e(TAG, "error:"+response.getError());
                return;
            }else {
                long id = response.getId();
                String chatgroupId = response.getChatgroupId();
                //已发送成功
                response.setStatus(ChatMessage.MSG_SEND);

                Set idSet = sendingMsgs.get(chatgroupId);
                if(idSet!=null){
                    idSet.remove(id);
                    Log.e(TAG, "remove response id:"+id);
                }else {
                    Log.e(TAG, "response id not record, id:"+id);
                }

                String[] whereValues = new String[]{id+"", chatgroupId};
                ContentValues values = new ContentValues();
                values.put("seq", response.getSeq());
                values.put("status", ChatMessage.MSG_SEND);
                DbHelper.getInstance(context).getDb().update("t_chatrecord",values,"id=? and chatgroup_id=?",whereValues);
                if(onMessageResponseListener!=null)
                    onMessageResponseListener.messageResponse(response);
            }
        }
    };
    //todo 判断发送失败通过调用 onMessageResponseListener 更新界面显示发送失败
    private Emitter.Listener onLogin = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            if(loginListener!=null)
                loginListener.login(args[0].toString());
        }
    };
    private Emitter.Listener onLoadInitData = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            loadInitDataListener.loadInitData(args[0].toString());
        }
    };
    private Emitter.Listener onVerifyToken= new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            for(int i=0;i<verifyTokenListeners.size();i++){
                verifyTokenListeners.get(i).response(args[0].toString());
            }
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

                /*
                if(messages.size()>0){
                    int myId = globalInfos.getUserId();
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
                */
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


    //本地向服务器发送的必经之路
    public void emit(String event, Object... args){
        Log.e(TAG, "emit "+event + ":" + args[0].toString());
        if(event.equals("message")){
            ChatMessage msg = gson.fromJson(args[0].toString(), ChatMessage.class);
            String chatgroupId = msg.getChatgroupId();
            Set idSet = sendingMsgs.get(chatgroupId);
            if(idSet==null){
                idSet = new HashSet<Integer>();
                sendingMsgs.put(chatgroupId, idSet);
            }
            idSet.add(msg.getId());
            Log.e(TAG, "sending id:"+msg.getId());
        }
        mSocket.emit(event, args);
    }
    public void emit(String event, Object[] args, Ack ask){
        mSocket.emit(event, args, ask);
    }


    public void setOnMessageResponseListener(OnMessageResponseListener onMessageResponseListener){
        this.onMessageResponseListener = onMessageResponseListener;
    }
    public interface OnMessageResponseListener{
        void messageResponse(ChatMessageResponse response);
    }
    public interface LoginListener{
        void login(String data);
    }
    public void setLoginListener(LoginListener listener){
        loginListener = listener;
    }

    public interface LoadInitDataListener{
        void loadInitData(String data);
    }
    public void setLoadInitDataListener(LoadInitDataListener listener){
        loadInitDataListener = listener;
    }

    public interface VerifyTokenListener{
        void verify(String data);
    }
    public void setVerifyTokenListener(VerifyTokenListener listener){
        verifyTokenListener = listener;
    }

    public void appendVerifyTokenListener(ChatResponseListener listener){
        verifyTokenListeners.add(listener);
    }
    public void removeVerifyTokenListener(ChatResponseListener listener){
        verifyTokenListeners.remove(listener);
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

