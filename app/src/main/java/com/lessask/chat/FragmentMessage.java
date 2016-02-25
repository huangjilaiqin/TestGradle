package com.lessask.chat;

//import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.friends.FriendsAdapter;
import com.lessask.global.GlobalInfos;
import com.lessask.model.User;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/8/23.
 * 聊天列表
 */
public class FragmentMessage extends Fragment{
    private Chat chat = Chat.getInstance();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private static final String TAG = FragmentMessage.class.getName();
    private static final int ON_FRIENDS = 0;

    private ListView lvFriends;
    private FriendsAdapter mFriendsAdapter;
    private View rootView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ON_FRIENDS:
                    ArrayList friends = globalInfos.getFriends();
                    if(getActivity()!=null){
                        mFriendsAdapter = new FriendsAdapter(getActivity(), friends);
                        lvFriends.setAdapter(mFriendsAdapter);
                        lvFriends.deferNotifyDataSetChanged();
                        Log.e(TAG, "onfriend notifyDataChange");
                    }

                    break;
                default:
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_chat, container, false);
            lvFriends = (ListView)rootView.findViewById(R.id.friends);

            ArrayList friends = globalInfos.getFriends();
            if(friends==null){
                Log.e(TAG, "friends is null");
            }
            mFriendsAdapter = new FriendsAdapter(getActivity().getApplicationContext(), friends);
            lvFriends.setAdapter(mFriendsAdapter);

            lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    User user = (User)parent.getAdapter().getItem(position);
                    intent.putExtra("friendId", user.getUserid());
                    Log.e(TAG, "friend_item click, userid:" + user.getUserid());
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
        }else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        }

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        //低效率的刷新,只要再次显示这个界面都重新刷新一遍
        mFriendsAdapter.notifyDataSetChanged();
    }
}
