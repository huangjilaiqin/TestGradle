package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketIOActivity extends Activity implements View.OnClickListener{

    private String host = "http://192.168.41.102:5002";
    private String yuHost = "http://123.59.40.113:5003";
    final private static String TAG = "SocketIOActivity";
    private Socket mSocket;
    {
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket", "flashsocket", "htmlfile", "xhr-multipart", "polling-xhr", "jsonp-polling"};
            mSocket = IO.socket(yuHost, options);
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
