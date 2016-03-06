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

import com.google.gson.Gson;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.DbInsertListener;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
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
    private Chat chat = Chat.getInstance(getContext());
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private static final String TAG = FragmentMessage.class.getName();
    private static final int ON_FRIENDS = 0;

    private TestMessageAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private View rootView;
    private Config config = globalInfos.getConfig();

    private Map<String,ChatGroup> chatGroupMap;

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

            mRecyclerViewAdapter = new TestMessageAdapter(getContext());
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

            loadChatGroups();

        }
        return rootView;
    }

    private void loadChatGroups(){
        mRecyclerView.showLoadingView();
        SQLiteDatabase db = DbHelper.getInstance(getContext()).getDb();
        //Cursor cursor = db.rawQuery("select * from t_chatgroup", null);
        String sql = "select a.* from t_chatrecord a where 10>(select count(*) from t_chatrecord where chatgroup_id=a.chatgroup_id and id>a.id) order by a.id";
        Cursor cursor = db.rawQuery(sql, null);
        int count = cursor.getColumnCount();
        chatGroupMap = new HashMap<>();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String chatgroupId = cursor.getString(1);
            int status = cursor.getInt(2);
            String time = cursor.getString(3);
            int userid = cursor.getInt(4);
            int type = cursor.getInt(5);
            String content = cursor.getString(6);
            int seq = cursor.getInt(7);
            int viewType = cursor.getInt(8);
            ChatGroup chatGroup;
            Log.e(TAG, "id:"+id+", chatgroupid:"+chatgroupId+", userid:"+userid+", time:"+time);
            if(!chatGroupMap.containsKey(chatgroupId)){
                chatGroup = new ChatGroup(chatgroupId);
                chatGroupMap.put(chatgroupId, chatGroup);
            }else {
                chatGroup = chatGroupMap.get(chatgroupId);
            }

            ChatMessage chatMessage = new ChatMessage(userid,chatgroupId,type,content,time,seq,status,viewType);
            chatGroup.appendMsg(chatMessage);
            Log.e(TAG, chatGroup.getChatgroupId()+":"+chatMessage.getContent());
        }


        sql = "select * from t_chatgroup";
        cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            String chatgroupId = cursor.getString(0);
            String name = cursor.getString(1);
            if(chatGroupMap.containsKey(chatgroupId)){
                chatGroupMap.get(chatgroupId).setName(name);
            }else {
                chatGroupMap.put(chatgroupId, new ChatGroup(chatgroupId, name));
            }
        }

        for(Map.Entry entry:chatGroupMap.entrySet()){
            ChatGroup chatGroup = (ChatGroup) entry.getValue();
            Log.e(TAG, "chatgroupid:"+chatGroup.getChatgroupId()+", msg size:"+chatGroup.getMessageList().size());
            mRecyclerViewAdapter.append(chatGroup);
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
        DbHelper.getInstance(getContext()).appendInsertListener("t_chatrecord", new DbInsertListener() {
            @Override
            public void callback(Object obj) {
                ChatMessage msg = (ChatMessage) obj;
                //更新列表项
                int position = mRecyclerViewAdapter.getPositionById(msg.getChatgroupId());
                ChatGroup chatGroup = mRecyclerViewAdapter.getItem(position);
                chatGroup.appendMsg(msg);
                mRecyclerViewAdapter.notifyItemUpdate(position);

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
