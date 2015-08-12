package com.lessask.chat;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by huangji on 2015/8/11.
 */

public class Chat {
    final private static String TAG = Chat.class.getName();
    //private String chathost = "http://ws.o-topcy.com";
    private String chathost = "http://123.59.40.113:5002";
    private Socket mSocket;
    private ChatContext chatContext;
    private Gson gson;
    //更新不一样的activity应该有多个listener
    private DataChangeListener dataChangeListener;
    private LoginListener loginListener;

    private Chat(){
        try {
            IO.Options options = new IO.Options();
            //options.transports = new String[]{"websocket", "polling"};
            mSocket = IO.socket(chathost, options);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeout);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on(Socket.EVENT_ERROR, onError);
            mSocket.on("message", onMessage);
            mSocket.on("login", onLogin);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        chatContext = ChatContext.getInstance();
        gson = new Gson();
    }

    public static final Chat getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final Chat INSTANCE = new Chat();
    }
    private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.d(TAG, "onConnect");
		}
	};
    private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            String error= "";
            for(int i=0;i<args.length;i++)
                error+=args[i]+", ";
            Log.d(TAG, "onConnectError:"+error);
		}
	};
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.d(TAG, "onDisconnect");
		}
	};
    private Emitter.Listener onTimeout = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.d(TAG, "onTimeout:"+ args[0]);
		}
	};
    private Emitter.Listener onReconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.d(TAG, "onReconnect");
            Log.d(TAG, "args length:"+args.length);
		}
	};
    private Emitter.Listener onError = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            String error= "";
            for(int i=0;i<args.length;i++)
                error+=args[i]+", ";
            Log.d(TAG, "onError:"+error);
		}
	};
    private Emitter.Listener onMessage = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            //to do 响应message要有两套机制,一个是message回应, 一个是好友发过来的message
            //修改用户的信息列表
            //Type type = new TypeToken<Map<String, String>>(){}.getType();
            //Map<String, String> map = gson.fromJson(args[0].toString(), type);
            //String id = map.get("id");
            ChatMessage message = gson.fromJson(args[0].toString(), ChatMessage.class);
            ArrayList mList = chatContext.getChatContent(message.getFriendid());
            mList.add(message);

            //通知当前聊天activity
            dataChangeListener.message(args.toString());
            //通知消息列表更新
        }
    };
    private Emitter.Listener onLogin = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            loginListener.login(args[0].toString());
        }
    };


    public void emit(String event, Object... args){
        Log.d(TAG, event+":"+args[0].toString());
        mSocket.emit(event, args);
    }
    public void emit(String event, Object[] args, Ack ask){
        mSocket.emit(event, args, ask);
    }


    public void setDataChangeListener(DataChangeListener dataChangeListener){
        this.dataChangeListener = dataChangeListener;
    }
    public interface DataChangeListener{
        void message(String data);
    }
    public interface LoginListener{
        void login(String data);
    }
    public void setLoginListener(LoginListener listener){
        loginListener = listener;
    }
}

