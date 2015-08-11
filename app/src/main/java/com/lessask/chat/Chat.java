package com.lessask.chat;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by huangji on 2015/8/11.
 */

public class Chat {
    final private static String TAG = Chat.class.getName();
    private String chathost = "http://ws.o-topcy.com";
    private Socket mSocket;

    private Chat(){
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket", "polling"};
            mSocket = IO.socket(chathost, options);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeout);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on(Socket.EVENT_ERROR, onError);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
    public void emit(String event, Object... args){
        mSocket.emit(event, args);
    }
    public void emit(String event, Object[] args, Ack ask){
        mSocket.emit(event, args, ask);
    }
}

