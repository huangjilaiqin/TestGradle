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
import com.lessask.chat.Chat;

import java.net.URISyntaxException;

public class SocketIOActivity extends Activity implements View.OnClickListener{

    final private static String TAG = "SocketIOActivity";

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
