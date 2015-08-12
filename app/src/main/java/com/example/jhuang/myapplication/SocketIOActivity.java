package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lessask.chat.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            case R.id.connect:
                Runtime runtime = Runtime.getRuntime();
                Process process = null;
                String line = null;
                InputStream is = null;
                InputStreamReader isr = null;
                BufferedReader br = null;
                String ip = "www.baidu.com";
                boolean res = false;
                try {
                    process = runtime.exec("ping " + ip); // PING
                    is = process.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    while ((line = br.readLine()) != null) {
                        Log.d(TAG,line);
                        if (line.contains("TTL") || line.contains("ttl")) {
                            res = true;
                            process.destroy();
                            break;
                        }
                    }
                    is.close();
                    isr.close();
                    br.close();
                    if (res) {
                        Log.d(TAG,"ping ok...");
                    } else {
                        Log.d(TAG,"ping not ok...");
                    }
                } catch (IOException e) {
                    System.out.println(e);
                    runtime.exit(1);
                }
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
