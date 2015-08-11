package com.lessask.chat;

import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by huangji on 2015/8/11.
 */

public class Chat {
    final private static String TAG = Chat.class.getName();
    private String chathost = "http://ws.o-topcy.com";

    private Chat(){}

    public static final Chat getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final Chat INSTANCE = new Chat();
    }
    private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Toast.makeText(null, "Connected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onConnect");
		}
	};
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onDisconnect");
		}
	};
    private Emitter.Listener onTimeout = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onTimeout");
            Log.d(TAG, "args length:"+args.length);
            Long l = (Long) args[0];
            Log.d(TAG, ""+l);
		}
	};
    private Emitter.Listener onReconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReconnect");
            Log.d(TAG, "args length:"+args.length);
		}
	};
    private Emitter.Listener onError = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onError");
            Log.d(TAG, "args length:"+args.length);
            String msg = (String)args[0];
            Log.d(TAG, "onError" + msg);
		}
	};
    private Socket mSocket;
    {
        try {
            IO.Options options = new IO.Options();
            //options.transports = new String[]{"websocket", "flashsocket", "htmlfile", "xhr-multipart", "polling-xhr", "jsonp-polling"};
            options.transports = new String[]{"websocket", "polling"};
            //options.reconnection = false;
            //options.forceNew = true;
            //options.reconnectionAttempts=2;
            //options.upgrade = true;
            mSocket = IO.socket(chathost, options);
            //mSocket = IO.socket(yuHost, options);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeout);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on(Socket.EVENT_ERROR, onError);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
