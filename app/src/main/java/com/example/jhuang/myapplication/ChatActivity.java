package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends Activity {

    private List<Message> messageList = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initChatItemList();
        ChatAdapter chatAdapter = new ChatAdapter(ChatActivity.this, R.layout.chat_item, messageList);
        ListView chatListView = (ListView) findViewById(R.id.chat_view);
        chatListView.setAdapter(chatAdapter);
    }

    private void initChatItemList(){
        for(int i=0;i<1000;i++){
            Message item;
            if(i%2 == 0) {
                item = new Message(Message.TYPE_RECEIVED_TEXT, "聊点什么" + i, R.mipmap.ic_launcher);
            }else{
                item = new Message(Message.TYPE_SEND_TEXT, "随便聊点什么都可以啊" + i, R.mipmap.ic_launcher);
            }
            messageList.add(item);
        }
    }
}
