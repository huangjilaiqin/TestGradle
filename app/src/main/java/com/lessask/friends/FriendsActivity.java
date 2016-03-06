package com.lessask.friends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lessask.chat.ChatActivity;
import com.lessask.R;
import com.lessask.chat.Chat;
import com.lessask.global.GlobalInfos;
import com.lessask.model.User;

import java.util.ArrayList;

public class FriendsActivity extends Activity {

    private Chat chat = Chat.getInstance(getBaseContext());
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private static final String TAG = FriendsActivity.class.getName();
    private static final int ON_FRIENDS = 0;

    private ListView lvFriends;
    private FriendsAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ON_FRIENDS:
                    ArrayList friends = globalInfos.getFriends();
                    adapter = new FriendsAdapter(FriendsActivity.this, friends);
                    lvFriends.setAdapter(adapter);
                    lvFriends.deferNotifyDataSetChanged();
                    Log.e(TAG, "onfriend notifyDataChange");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Log.e(TAG, "oncreate");

        lvFriends = (ListView)findViewById(R.id.friends);
        ArrayList friends = globalInfos.getFriends();
        if(friends==null){
            Log.e(TAG, "friends is null");
        }
        adapter = new FriendsAdapter(FriendsActivity.this, friends);
        lvFriends.setAdapter(adapter);

        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                User user = (User)parent.getAdapter().getItem(position);
                intent.putExtra("friendId", user.getUserid());
                Log.e(TAG, "contact_item click, userid:" + user.getUserid());
                startActivity(intent);
            }
        });

        chat.setFriendsListener(new Chat.FriendsListener() {
            @Override
            public void friendsInfo(String data) {
                Log.e(TAG, "activity响应friendsInfo");
                //处理 friendsActivity 界面先于onfriends协议返回,导致界面没有数据的情况
                Message msg = new Message();
                msg.what = ON_FRIENDS;
                handler.sendMessage(msg);
            }
        });
    }

}