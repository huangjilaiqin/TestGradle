package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketIOActivity extends Activity implements View.OnClickListener{

    private String host = "http://192.168.41.102:5002";
    private String yuHost = "http://123.59.40.113:5002";
    private String onlineHost = "http://ws.qqshidao2.com";
    private String scoreHost = "http://14.215.100.100";
    private String scoreHost2 = "http://wsn.500.com";
    private String myhost = "http://123.59.40.113";
    final private static String TAG = "SocketIOActivity";
    private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
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
            Log.d(TAG, "onError"+msg);
		}
	};
    private Socket mSocket;
    {
        try {
            IO.Options options = new IO.Options();
            //options.transports = new String[]{"websocket", "flashsocket", "htmlfile", "xhr-multipart", "polling-xhr", "jsonp-polling"};
            options.transports = new String[]{"websocket", "polling"};
            //options.reconnection = false;
            options.forceNew = true;
            options.reconnectionAttempts=2;
            //options.upgrade = true;
            mSocket = IO.socket(myhost, options);
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
    private EditText etContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_io);
        Button bConnect = (Button)findViewById(R.id.connect);
        Button bDisconnect = (Button)findViewById(R.id.disconnect);
        Button bSend = (Button)findViewById(R.id.send);
        bConnect.setOnClickListener(this);
        bDisconnect.setOnClickListener(this);
        bSend.setOnClickListener(this);
        etContent = (EditText) findViewById(R.id.content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect:
                mSocket.connect();
                Log.d(TAG, "connect");
                break;
            case R.id.disconnect:
                mSocket.disconnect();
                Log.d(TAG, "disconnect");
                break;
            case R.id.send:
                String content = etContent.getText().toString().trim();
                if(content.length()==0){
                    Toast.makeText(getApplicationContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                }
                mSocket.emit("data", content);
                etContent.setText("");
                break;
            default:
                break;
        }
    }
}
