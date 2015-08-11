package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lessask.chat.Chat;

import java.net.URISyntaxException;

public class SocketIOActivity extends Activity implements View.OnClickListener{

    final private static String TAG = "SocketIOActivity";

    private EditText etContent;
    private Chat chat;
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
        chat = Chat.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                String content = etContent.getText().toString().trim();
                if(content.length()==0){
                    Toast.makeText(getApplicationContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                }
                chat.emit("data", content);
                etContent.setText("");
                break;
            default:
                break;
        }
    }
}
