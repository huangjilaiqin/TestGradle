package com.lessask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class HandlerActivity extends Activity {

    public static final int UPDATE_TEXT = 1;

    private static TextView tv;

    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_TEXT:
                    Content content = (Content)msg.obj;
                    tv.setText(content.title);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);

        tv = (TextView) findViewById(R.id.show_text);
        Button bChange = (Button) findViewById(R.id.change_text);
        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("HandlerActivity", "线程启动...");
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        Content content = new Content();
                        content.title = "在线程中改变显示内容"+new Date();
                        message.obj = content;
                        handler.sendMessage(message);
                        Log.d("HandlerActivity", "sendMessage...");
                    }
                }).start();
            }
        });
    }

    public class Content{
        public String title;
    }

}
