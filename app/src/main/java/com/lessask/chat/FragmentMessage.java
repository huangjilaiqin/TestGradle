package com.lessask.chat;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.DbInsertListener;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    private MessageAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private View rootView;
    private Config config = globalInfos.getConfig();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_chat, container, false);
            mRecyclerView = (RecyclerViewStatusSupport) rootView.findViewById(R.id.list);
            mRecyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

            mRecyclerViewAdapter = new MessageAdapter(getContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    ChatGroup chatGroup = mRecyclerViewAdapter.getItem(position);
                    intent.putExtra("chatGroup", chatGroup);
                    startActivity(intent);
                }
            });

            globalInfos.setMessageAdapter(mRecyclerViewAdapter);
            loadChatGroups();

        }
        return rootView;
    }

    private void loadChatGroups(){
        mRecyclerView.showLoadingView();
        SQLiteDatabase db = DbHelper.getInstance(getContext()).getDb();
        Cursor cursor = db.rawQuery("select * from t_chatgroup", null);
        int count = cursor.getColumnCount();
        while (cursor.moveToNext()){
            mRecyclerViewAdapter.append(new ChatGroup(cursor.getString(0), cursor.getString(1)));
        }
        Log.e(TAG, "query db, chatgroup size:"+count);
        if(count==0){
            mRecyclerView.showEmptyView();
        }else {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }

        //设置数据库监听
        DbHelper.getInstance(getContext()).appendInsertListener("t_chatgroup", new DbInsertListener() {
            @Override
            public void callback(Object obj) {
                ChatGroup chatGroup = (ChatGroup)obj;
                mRecyclerViewAdapter.append(chatGroup);
                Log.e(TAG, "insert callback");
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        //低效率的刷新,只要再次显示这个界面都重新刷新一遍
        mRecyclerViewAdapter.notifyDataSetChanged();
        if(globalInfos.getChatGroups()!=null)
            Log.e(TAG, "size:"+globalInfos.getChatGroups().size());
    }
}
