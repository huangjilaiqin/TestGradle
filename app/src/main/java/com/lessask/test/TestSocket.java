package com.lessask.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.lessask.R;

import java.net.URISyntaxException;

public class TestSocket extends Activity {
    private static String TAG = TestSocket.class.getName();

    private EditText etHost;
    private Button btConnect;
    private TextView tvResult;
    private ScrollView svScroll;
    //private String mHost = "http://123.59.40.113:5002";
    private String mHost = "http://wsn.500.com";
    private Socket mSocket;
    private StringBuffer mResult;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = mResult.toString();
            if(result.length()>2000){
                result = result.substring(1000);
                mResult = new StringBuffer(result);
            }
            tvResult.setText(result);
            int offset=tvResult.getLineCount()*tvResult.getLineHeight();
            svScroll.scrollTo(0, offset);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);
        mResult = new StringBuffer();
        etHost = (EditText) findViewById(R.id.host);
        btConnect = (Button) findViewById(R.id.connect);
        tvResult = (TextView) findViewById(R.id.result);
        svScroll = (ScrollView) findViewById(R.id.scrollview);
        tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = etHost.getText().toString();
                if(host!=null && host.length()>0)
                    mHost = host;
                Log.e(TAG, "host:" + mHost);
                mResult.append("connect host:" + mHost + "\n");
                handler.sendEmptyMessage(0);
                connect2Die();
            }
        });
    }
    private void connect2Die(){
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.transports = new String[]{"websocket", "polling"};
            mSocket = IO.socket(mHost, options);
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
    private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onConnect");
            mResult.append("onConnect\n");
            handler.sendEmptyMessage(0);
            mSocket.close();
            connect2Die();
		}
	};
    private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            String error= "";
            for(int i=0;i<args.length;i++)
                error+=args[i]+", ";
            Log.e(TAG, "onConnectError:" + error);
            mResult.append("onConnectError:" + error + "\n");
            handler.sendEmptyMessage(0);
		}
	};
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onDisconnect");
            mResult.append("onDisconnect\n");
            handler.sendEmptyMessage(0);
		}
	};
    private Emitter.Listener onTimeout = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onTimeout:" + args[0]);
            mResult.append("onTimeout\n");
            handler.sendEmptyMessage(0);
		}
	};
    private Emitter.Listener onReconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onReconnect");
            mResult.append("onReconnect\n");
            handler.sendEmptyMessage(0);
            Log.e(TAG, "args length:" + args.length);
		}
	};
    private Emitter.Listener onError = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            String error= "";
            for(int i=0;i<args.length;i++)
                error+=args[i]+", ";
            Log.e(TAG, "onError:" + error);
            mResult.append("onError:" + error + "\n");
            handler.sendEmptyMessage(0);
		}
	};
}
