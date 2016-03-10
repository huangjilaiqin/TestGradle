package com.lessask.chat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.global.DbHelper;
import com.lessask.global.DbInsertListener;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.ChatMessageResponse;
import com.lessask.model.ResponseError;
import com.lessask.recyclerview.ImprovedSwipeLayout;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.show.ShowListAdapter;
import com.lessask.util.ScreenUtil;
import com.lessask.util.TimeHelper;
import com.lessask.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyChatActivity extends Activity{

    private static final int HANDLER_MESSAGE_RECEIVE = 0;
    private static final int HANDLER_MESSAGE_RESP = 1;
    private static final int HANDLER_HISTORY_SUCCESS = 2;
    private static final int HANDLER_HISTORY_ERROR = 3;

    private final static String TAG = "ChatActivity";
    private ListView chatListView;
    private MyChatAdapter mRecyclerViewAdapter;

    private SwipeRefreshLayout swipeView;
    private RecyclerViewStatusSupport mRecyclerView;
    private ImprovedSwipeLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;

    private EditText etContent;

    private Chat chat = Chat.getInstance(getBaseContext());
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private int userId;
    private int friendId=0;
    private String chatgroupId;
    private int oldestSeq=0;
    private int newestSeq=0;

    private List<ChatMessage> messageList;
    private ChatGroup chatGroup;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case HANDLER_MESSAGE_RECEIVE:
                    mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount()-1);
                    mRecyclerView.scrollToPosition(mRecyclerViewAdapter.getItemCount()-1);
                    Log.e(TAG, "current chat insert callback");
                    //通知friendActivity更新

                    break;
                case HANDLER_MESSAGE_RESP:
                    //根据消息响应的状态改变界面
                    ChatMessageResponse response = (ChatMessageResponse)msg.obj;

                    break;
                case HANDLER_HISTORY_SUCCESS:
                    int msgSize = msg.arg1;
                    Log.e(TAG, "HANDLER_HISTORY_SUCCESS:"+msgSize+", friendId:"+friendId);
                    if(msgSize>0){
                        //加载数据时保持当前数据不动, 当adapter中有数据时setSelection不起作用
                        //chatListView.setAdapter(chatAdapter);
                        chatListView.setSelection(msgSize);
                        if(msgSize>1) {
                            msgSize--;
                            chatListView.scrollTo(msgSize,0);
                        }
                        //chatListView.setSelection(0);
                    }else {
                        Toast.makeText(MyChatActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onHistory notifyDataSetChanged");
                    swipeView.setRefreshing(false);
                    break;
                case HANDLER_HISTORY_ERROR:
                    ResponseError error = (ResponseError)msg.obj;
                    Toast.makeText(MyChatActivity.this, "errno:"+error.getErrno()+",msg:"+error.getError(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onHistory error");
                    swipeView.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);
        //获取传递的数据
        final Intent intent = getIntent();
        userId = globalInfos.getUserId();
        chatGroup = intent.getParcelableExtra("chatGroup");
        chatgroupId = chatGroup.getChatgroupId();
        if(chatgroupId.contains("_")){
            String[] ids = chatgroupId.split("_");
            int id1 = Integer.parseInt(ids[0]);
            if(id1==userId)
                friendId = Integer.parseInt(ids[1]);
            else
                friendId = id1;
        }


        //初始化控件
        //swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

        mRecyclerView = (RecyclerViewStatusSupport) findViewById(R.id.list);
        mRecyclerView.setStatusViews(findViewById(R.id.loading_view), findViewById(R.id.empty_view), findViewById(R.id.error_view));
        mLinearLayoutManager = new LinearLayoutManager(this);
        //键盘弹起时 记录滚动到最后一条
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mSwipeRefreshLayout = (ImprovedSwipeLayout) findViewById(R.id.swiperefresh);

        mRecyclerViewAdapter = new MyChatAdapter(this);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        List<ChatMessage> messageList = chatGroup.getMessageList();
        if(messageList.size()!=0){
            oldestSeq = messageList.get(0).getSeq();
            newestSeq = messageList.get(messageList.size()-1).getSeq();
        }
        mRecyclerViewAdapter.appendToList(messageList);
        mRecyclerView.scrollToPosition(mRecyclerViewAdapter.getItemCount()-1);
        mRecyclerViewAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.line_color_run_speed_13);

        //下拉刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 mSwipeRefreshLayout.setRefreshing(true);
                 SQLiteDatabase db = globalInfos.getDb(getBaseContext());
                 int expectSeq = oldestSeq-15;
                 Log.e(TAG, "load history:"+expectSeq+","+oldestSeq);
                 Cursor cursor = db.rawQuery("select * from t_chatrecord where chatgroup_id=? and seq>? and seq<? order by seq desc", new String[]{chatgroupId, ""+expectSeq,""+oldestSeq});
                 Log.e(TAG, "load history size:"+cursor.getCount());
                 while (cursor.moveToNext()){
                     int id = cursor.getInt(0);
                     String chatgroupId = cursor.getString(1);
                     int status = cursor.getInt(2);
                     Date time = TimeHelper.dateParse(cursor.getString(3));
                     int userid = cursor.getInt(4);
                     int type = cursor.getInt(5);
                     String content = cursor.getString(6);
                     int seq = cursor.getInt(7);
                     Log.e(TAG, "seq:"+seq);
                     oldestSeq = seq;
                     mRecyclerViewAdapter.appendToTop(new ChatMessage(id, userid, chatgroupId, type, content, time, status, seq));
                 }
                 Log.e(TAG, "oldestSeq:"+oldestSeq);
                 cursor.close();
                 mRecyclerViewAdapter.notifyItemRangeInserted(0,cursor.getCount());
                 mSwipeRefreshLayout.setRefreshing(false);
             }
         });

        //监听聊天信息
        DbHelper.getInstance(getBaseContext()).appendInsertListener("t_chatrecord", new DbInsertListener() {
            @Override
            public void callback(Object obj) {
                //这个回调方法 发送跟接收到都会调用
                ChatMessage message = (ChatMessage) obj;
                if(message.getChatgroupId().equals(chatgroupId)){
                    if (message.getUserid()!=globalInfos.getUserId()) {
                        //接受信息
                        newestSeq++;
                        mRecyclerViewAdapter.append(message);
                        Message msg = new Message();
                        msg.what = HANDLER_MESSAGE_RECEIVE;
                        handler.sendMessage(msg);
                    }else {
                        //发送信息
                    }
                }
            }
        });

        chat.setOnMessageResponseListener(new Chat.OnMessageResponseListener() {
            @Override
            public void messageResponse(ChatMessageResponse response) {
                //todo 显示信息状态
            }
        });

        //消息类型
        final ImageView ivContentType = (ImageView) findViewById(R.id.content_type);
        //输入框
        etContent = (EditText) findViewById(R.id.content);
        //发送
        final ImageView ivMore = (ImageView) findViewById(R.id.more);
        final Button send = (Button) findViewById(R.id.send);
        int screenWidth = ScreenUtil.getScreenWidth(getBaseContext());

        ViewGroup.LayoutParams params = etContent.getLayoutParams();
        Log.e(TAG, "width:"+params.width);
        params.width = screenWidth-ivMore.getLayoutParams().width-ivContentType.getLayoutParams().width;
        Log.e(TAG, "width:"+params.width);
        etContent.setLayoutParams(params);

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    ivMore.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);
                } else {
                    ivMore.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivContentType.setTag(R.drawable.tn);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                if (content.length()==0) {
                    Toast.makeText(getApplicationContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                newestSeq++;
                ChatMessage msg = new ChatMessage(userId,friendId,chatgroupId, ChatMessage.MSG_TYPE_TEXT,content,new Date(),newestSeq,ChatMessage.MSG_SENDING);
                mRecyclerViewAdapter.append(msg);
                mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount());
                mRecyclerView.smoothScrollToPosition(mRecyclerViewAdapter.getItemCount());

                etContent.setText("");

                //to do对发送的消息进行转圈圈, 由messageResponse取消圈圈
                Log.d(TAG, "gson:" + gson.toJson(msg));
                chat.emit("message", gson.toJson(msg));

                //本地主动发送的消息入库

                //该聊天记录不在聊天列表里
                if(intent.getBooleanExtra("notInContacts", false)) {
                    ContentValues values = new ContentValues();
                    values.put("chatgroup_id", chatGroup.getChatgroupId());
                    values.put("name", chatGroup.getName());
                    DbHelper.getInstance(getBaseContext()).insert("t_chatgroup", null, values);
                }

                //聊天消息入库
                ContentValues values = new ContentValues();
                values.put("chatgroup_id", chatGroup.getChatgroupId());
                values.put("userid",""+userId);
                values.put("type", ""+msg.getType());
                values.put("content", msg.getContent());
                values.put("status", msg.getStatus());
                values.put("time", TimeHelper.date2Chat(msg.getTime()));
                values.put("seq", msg.getSeq());
                DbHelper.getInstance(getBaseContext()).insert("t_chatrecord", null, values);
            }
        });
        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        ivContentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView)v;
                int resourceId = (int)iv.getTag();
                //Log.d("ChatActivity", ""+resourceId+","+R.drawable.tn+","+R.drawable.tl);
                //tn 为语音图片
                InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(resourceId == R.drawable.tn) {
                    Log.d("ChatActivity", "change to tn");
                    //ivContentType.setImageDrawable(getResources().getDrawable(R.drawable.tl));
                    //ivContentType.invalidate();;
                    Log.d(TAG, "change to tl");
                    //ivContentType.setImageResource(R.drawable.tl);
                    iv.setImageResource(R.drawable.tl);
                    iv.setTag(R.drawable.tl);
                    if(imm != null){
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                    }
                }else{
                    //ivContentType.setImageDrawable(getResources().getDrawable(R.drawable.tn));
                    //ivContentType.setImageResource(R.drawable.tn);
                    Log.d(TAG, "change to tl");
                    iv.setImageResource(R.drawable.tn);
                    iv.setTag(R.drawable.tn);
                    if(imm != null){
                        imm.showSoftInput(getWindow().getDecorView(), InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        });
        /*
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                     chatAdapter.notifyDataSetChanged();
                }
            }
        });
        */
        /*
        swipeView.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                //请求历史数据
                History history = new History(userId, friendId, globalInfos.getHistoryIds(friendId));
                Log.e(TAG, "history:"+gson.toJson(history));
                chat.emit("history", gson.toJson(history));
            }
        });
        */
    }
}
