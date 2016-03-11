package com.lessask.chat;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
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
import com.lessask.util.TimeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
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

    private MessageAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private View rootView;
    private Config config = globalInfos.getConfig();
    private String currentChatgroupId = "";


    private static final int HANDLER_MESSAGE_RECEIVE = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case HANDLER_MESSAGE_RECEIVE:
                    int position = msg.arg1;
                    //通知friendActivity更新
                    //mRecyclerViewAdapter.notifyItemChanged(position);
                    mRecyclerViewAdapter.notifyDataSetChanged();

                    break;

                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    Intent intent = new Intent(getActivity(), MyChatActivity.class);
                    ChatGroup chatGroup = mRecyclerViewAdapter.getItem(position);
                    chatGroup.setUnreadCout(0);
                    currentChatgroupId = chatGroup.getChatgroupId();
                    ChatMessage m = chatGroup.getMessageList().get(0);
                    intent.putExtra("test", m);
                    intent.putParcelableArrayListExtra("list", chatGroup.getMessageList());
                    intent.putExtra("chatGroup", chatGroup);
                    Log.e(TAG, "start MyChatActivity");
                    startActivity(intent);
                }
            });

            loadChatGroups();
            //设置数据库监听
            DbHelper.getInstance(getContext()).appendInsertListener("t_chatgroup", new DbInsertListener() {
                @Override
                public void callback(Object obj) {
                    ChatGroup chatGroup = (ChatGroup)obj;
                    mRecyclerViewAdapter.append(chatGroup);
                    mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount()-1);
                    Log.e(TAG, "insert callback");
                }
            });
            DbHelper.getInstance(getContext()).appendInsertListener("t_chatrecord", new DbInsertListener() {
                @Override
                public void callback(Object obj) {
                    ChatMessage msg = (ChatMessage) obj;
                    //更新列表项
                    int position = mRecyclerViewAdapter.getPositionById(msg.getChatgroupId());
                    Log.e(TAG, "insert callback, position:"+position);
                    ChatGroup chatGroup = mRecyclerViewAdapter.getItem(position);
                    chatGroup.appendMsg(msg);
                    if(currentChatgroupId!="" || currentChatgroupId!=chatGroup.getChatgroupId()){
                        chatGroup.setUnreadCout(chatGroup.getUnreadCout()+1);
                    }
                    //越界
                    Message message = new Message();
                    message.arg1 = position;
                    message.what = HANDLER_MESSAGE_RECEIVE;
                    handler.sendMessage(message);
                    mRecyclerViewAdapter.sort();
                }
            });

        }
        return rootView;
    }

    private void loadChatGroups(){
        mRecyclerView.showLoadingView();
        SQLiteDatabase db = DbHelper.getInstance(getContext()).getDb();
        //Cursor cursor = db.rawQuery("select * from t_chatgroup", null);
        //每个聊天列表只查10条记录
        String sql = "select a.* from t_chatrecord a where 10>(select count(*) from t_chatrecord where chatgroup_id=a.chatgroup_id and id>a.id) order by a.id";
        Cursor cursor = db.rawQuery(sql, null);
        int count = cursor.getCount();
        Map<String,ChatGroup> chatGroupMap = new HashMap<>();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            int seq = cursor.getInt(1);
            int userid = cursor.getInt(2);
            String chatgroupId = cursor.getString(3);
            int type = cursor.getInt(4);
            String content = cursor.getString(5);
            Date time = TimeHelper.dateParse(cursor.getString(6));
            int status = cursor.getInt(7);
            ChatGroup chatGroup;
            Log.e(TAG, "load record id:"+id+", chatgroupid:"+chatgroupId+", userid:"+userid+", content:"+content+", time:"+time);
            if(!chatGroupMap.containsKey(chatgroupId)){
                chatGroup = new ChatGroup(chatgroupId);
                chatGroupMap.put(chatgroupId, chatGroup);
            }else {
                chatGroup = chatGroupMap.get(chatgroupId);
            }

            ChatMessage chatMessage = new ChatMessage(id,seq,userid,chatgroupId,type,content,time,status);
            chatGroup.appendMsg(chatMessage);
        }


        //设置每个聊天列表的名字,包括没有聊天记录的项
        sql = "select * from t_chatgroup";
        cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            String chatgroupId = cursor.getString(0);
            String name = cursor.getString(1);
            int status = cursor.getInt(2);
            if(chatGroupMap.containsKey(chatgroupId)){
                chatGroupMap.get(chatgroupId).setName(name);
            }else {
                chatGroupMap.put(chatgroupId, new ChatGroup(chatgroupId,name,status));
            }
        }

        for(Map.Entry entry:chatGroupMap.entrySet()){
            ChatGroup chatGroup = (ChatGroup) entry.getValue();
            Log.e(TAG, "chatgroupid:"+chatGroup.getChatgroupId()+", msg size:"+chatGroup.getMessageList().size());
            mRecyclerViewAdapter.append(chatGroup);
        }
        mRecyclerViewAdapter.sort();
        mRecyclerViewAdapter.getList();
        for(int i=0;i<mRecyclerViewAdapter.getItemCount();i++) {
            ChatGroup chatGroup = mRecyclerViewAdapter.getItem(i);
            ChatMessage msg =chatGroup.getLastMessage();
            if(msg!=null)
                Log.e(TAG, "sort:" + msg.getTime());
            else
                Log.e(TAG, "none");
        }
        Log.e(TAG, "query db, chatgroup size:"+count);
        if(count==0){
            mRecyclerView.showEmptyView();
        }else {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        currentChatgroupId = "";
        //低效率的刷新,只要再次显示这个界面都重新刷新一遍
        //mRecyclerViewAdapter.notifyDataSetChanged();
        //if(globalInfos.getChatGroups()!=null)
        //    Log.e(TAG, "size:"+globalInfos.getChatGroups().size());
    }
}
