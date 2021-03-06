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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lessask.DividerItemDecoration;
import com.lessask.MyAppCompatActivity;
import com.lessask.R;
import com.lessask.global.DbHelper;
import com.lessask.global.DbInsertListener;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.ChatMessageResponse;
import com.lessask.model.ResponseError;
import com.lessask.recyclerview.ImprovedSwipeLayout;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.show.ShowListAdapter;
import com.lessask.util.ScreenUtil;
import com.lessask.util.TimeHelper;
import com.lessask.util.Utils;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyChatActivity extends MyAppCompatActivity {

    private static final int HANDLER_MESSAGE_RECEIVE = 0;
    private static final int HANDLER_MESSAGE_RESP = 1;
    private static final int HANDLER_HISTORY_SUCCESS = 2;
    private static final int HANDLER_HISTORY_ERROR = 3;
    private static final int HANDLER_MESSAGE_SEND = 4;
    private static final int HANDLER_MESSAGE_RESEND = 5;


    private final static String TAG = MyChatActivity.class.getSimpleName();
    private ListView chatListView;
    private MyChatAdapter mRecyclerViewAdapter;

    private SwipeRefreshLayout swipeView;
    private RecyclerViewStatusSupport mRecyclerView;
    private ImprovedSwipeLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;

    private EditText etContent;

    private Chat chat = Chat.getInstance(getBaseContext());
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    //private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:MM:ss").create();
    private Gson gson = TimeHelper.gsonWithDate();

    private int userId;
    private int friendId=0;
    private String chatgroupId;
    private long oldestId=0;
    private long newestId=0;

    private List<ChatMessage> messageList;
    private ChatGroup chatGroup;
    private Intent intent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatMessageResponse response;
            ChatMessage chatMessage;
            switch (msg.what){
                case HANDLER_MESSAGE_RECEIVE:
                    mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount()-1);
                    mRecyclerView.scrollToPosition(mRecyclerViewAdapter.getItemCount()-1);
                    Log.e(TAG, "current chat insert callback");
                    //通知friendActivity更新
                    break;
                case HANDLER_MESSAGE_SEND:
                    mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount());
                    mRecyclerView.smoothScrollToPosition(mRecyclerViewAdapter.getItemCount());
                    break;
                case HANDLER_MESSAGE_RESP:
                    //根据消息响应的状态改变界面
                    response = (ChatMessageResponse)msg.obj;
                    mRecyclerViewAdapter.updateItemStatusById(response.getId(),response.getStatus());
                    break;
                case HANDLER_MESSAGE_RESEND:
                    chatMessage = (ChatMessage)msg.obj;
                    mRecyclerViewAdapter.updateItemStatusById(chatMessage.getId(),chatMessage.getStatus());
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


    private DbInsertListener chatrecordInsertListener = new DbInsertListener() {
        @Override
        public void callback(Object obj) {
            //这个回调方法 发送跟接收到都会调用
            ChatMessage message = (ChatMessage) obj;
            if(message.getChatgroupId().equals(chatgroupId)){
                if (message.getUserid()!=globalInfos.getUserId()) {
                    //接受信息
                    newestId=message.getId();
                    mRecyclerViewAdapter.append(message);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE_RECEIVE;
                    handler.sendMessage(msg);
                }else {
                    //发送信息
                    //to do对发送的消息进行转圈圈, 由messageResponse取消圈圈
                    Log.e(TAG, "emit gson:" + gson.toJson(message));
                    chat.emit("message", gson.toJson(message));
                    //先写数据库，再呈现是为了获得自增id
                    newestId=message.getId();
                    mRecyclerViewAdapter.append(message);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE_SEND;
                    handler.sendMessage(msg);

                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);
        //获取传递的数据
        intent = getIntent();
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
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(chatGroup.getName());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,intent);
                finish();
            }
        });


        //swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        //键盘弹起时，信息滚动到最后一条
        final View activityRootView = findViewById(R.id.root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                int count = mRecyclerViewAdapter.getItemCount();
                if (heightDiff > 100 && count>0) {
                    mRecyclerView.smoothScrollToPosition(count - 1);
                }
            }
        });

        mRecyclerView = (RecyclerViewStatusSupport) findViewById(R.id.list);
        mRecyclerView.setStatusViews(findViewById(R.id.loading_view), findViewById(R.id.empty_view), findViewById(R.id.error_view));
        mLinearLayoutManager = new LinearLayoutManager(this);
        //键盘弹起时 记录滚动到最后一条
        //mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mSwipeRefreshLayout = (ImprovedSwipeLayout) findViewById(R.id.swiperefresh);

        mRecyclerViewAdapter = new MyChatAdapter(this);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        List<ChatMessage> messageList = chatGroup.getMessageList();
        if(messageList.size()!=0){
            oldestId = messageList.get(0).getId();
            newestId = messageList.get(messageList.size()-1).getId();
        }
        mRecyclerViewAdapter.appendToList(messageList);
        mRecyclerView.scrollToPosition(mRecyclerViewAdapter.getItemCount()-1);
        mRecyclerViewAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.line_color_run_speed_13);


        //第一次聊天，没有聊天历史记录
        if(intent.getBooleanExtra("notInContacts", false)) {
            mSwipeRefreshLayout.setEnabled(false);
        }

        //下拉刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 mSwipeRefreshLayout.setRefreshing(true);
                 SQLiteDatabase db = globalInfos.getDb(getBaseContext());
                 long expectId = oldestId-15;
                 int loadSize = 15;
                 Log.e(TAG, "load history:"+expectId+","+oldestId);

                 Cursor cursor = db.rawQuery("select * from t_chatrecord where chatgroup_id=? and id<? order by id desc limit ?", new String[]{chatgroupId,""+oldestId,""+loadSize});
                 Log.e(TAG, "load history size:"+cursor.getCount());
                 while (cursor.moveToNext()){
                     int id = cursor.getInt(0);
                     int seq = cursor.getInt(1);
                     int userid = cursor.getInt(2);
                     String chatgroupId = cursor.getString(3);
                     int type = cursor.getInt(4);
                     String content = cursor.getString(5);
                     Date time = TimeHelper.dateParse(cursor.getString(6));
                     int status = cursor.getInt(7);
                     oldestId = id;
                     Log.e(TAG, "id:"+id+", status:"+status+", userid:"+userid+", content:"+content);
                     mRecyclerViewAdapter.appendToTop(new ChatMessage(id,seq,userid,chatgroupId,type,content,time,status));
                 }
                 Log.e(TAG, "oldestId:"+oldestId);
                 cursor.close();
                 mRecyclerViewAdapter.notifyItemRangeInserted(0,cursor.getCount());
                 mSwipeRefreshLayout.setRefreshing(false);
                 if(cursor.getCount()<loadSize)
                     mSwipeRefreshLayout.setEnabled(false);
             }
         });

        //监听聊天信息
        DbHelper.getInstance(getBaseContext()).appendInsertListener("t_chatrecord", chatrecordInsertListener);

        chat.setOnMessageResponseListener(new Chat.OnMessageResponseListener() {
            @Override
            public void messageResponse(ChatMessageResponse response) {
                //todo 显示信息状态
                // todo 更新界面
                Message msg = new Message();
                msg.obj = response;
                msg.what = HANDLER_MESSAGE_RESP;
                handler.sendMessage(msg);
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
        params.width = screenWidth-ivMore.getLayoutParams().width-ivContentType.getLayoutParams().width;
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

                ChatMessage msg = new ChatMessage(userId,chatgroupId,ChatMessage.MSG_TYPE_TEXT,content,new Date(),ChatMessage.MSG_SENDING,friendId);
                etContent.setText("");

                //该聊天记录不在聊天列表里
                if(intent.getBooleanExtra("notInContacts", false)) {
                    ContentValues values = new ContentValues();
                    values.put("chatgroup_id", chatGroup.getChatgroupId());
                    values.put("name", chatGroup.getName());
                    DbHelper.getInstance(getBaseContext()).insert("t_chatgroup", null, values);
                }

                //聊天消息入库
                ContentValues values = new ContentValues();
                values.put("userid",""+userId);
                values.put("chatgroup_id", chatGroup.getChatgroupId());
                values.put("type", ""+msg.getType());
                values.put("content", msg.getContent());
                values.put("time", TimeHelper.dateFormat(msg.getTime()));
                values.put("status", msg.getStatus());
                values.put("friendid", msg.getFriendid());
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

        mRecyclerViewAdapter.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View view, int position) {

            }

            @Override
            public void onItemMenuClick(View view, Object obj) {
                ChatMessage message = (ChatMessage)obj;
                Log.e(TAG, "resend emit gson:" + gson.toJson(message));
                chat.emit("message", gson.toJson(message));
                //先写数据库，再呈现是为了获得自增id
                Message msg = new Message();
                msg.obj = message;
                msg.what = HANDLER_MESSAGE_RESEND;
                handler.sendMessage(msg);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbHelper.getInstance(getBaseContext()).removeInsertListener("t_chatrecord", chatrecordInsertListener);
    }
}
