package com.lessask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.lessask.chat.Chat;

public class FriendsActivity extends Activity {

    private Chat chat = Chat.getInstance();
    private Gson gson = new Gson();
    private MyApplication application;
    private static final String TAG = FriendsActivity.class.getName();

    private ListView lvFriends;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        application = (MyApplication)getApplicationContext();

        lvFriends = (ListView)findViewById(R.id.friends);
        adapter = new SimpleAdapter(FriendsActivity.this, application.getFriends(), android.R.layout.simple_expandable_list_item_1);

        chat.setFriendsListener(new Chat.FriendsListener() {
            @Override
            public void friendsInfo(String data) {

            }
        });
    }

}
