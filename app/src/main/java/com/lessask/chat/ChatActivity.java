package com.lessask.chat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import android.os.Handler;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.global.DbHelper;
import com.lessask.global.DbInsertListener;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.ChatMessageResponse;
import com.lessask.model.ResponseError;
import com.lessask.util.ScreenUtil;
import com.lessask.util.TimeHelper;
import com.lessask.util.Utils;


public class ChatActivity extends Activity implements AbsListView.OnScrollListener{

    private static final int HANDLER_MESSAGE_RECEIVE = 0;
    private static final int HANDLER_MESSAGE_RESP = 1;
    private static final int HANDLER_HISTORY_SUCCESS = 2;
    private static final int HANDLER_HISTORY_ERROR = 3;

    private final static String TAG = "ChatActivity";
    private ListView chatListView;
    private static ChatAdapter chatAdapter;
    private SwipeRefreshLayout swipeView;

    private EditText etContent;

    private Chat chat = Chat.getInstance(getBaseContext());
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private int userId;
    private int friendId=0;
    private String chatgroupId;
    private boolean notInContacts=false;
    private int seq;

    private List<ChatMessage> messageList;
    //private LinkedList<ChatMessage> messageList;
    private ChatGroup chatGroup;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case HANDLER_MESSAGE_RECEIVE:
                    chatAdapter.notifyDataSetChanged();
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
                        chatListView.setAdapter(chatAdapter);
                        chatListView.setSelection(msgSize);
                        if(msgSize>1) {
                            msgSize--;
                            chatListView.scrollTo(msgSize,0);
                        }
                        //chatAdapter.notifyDataSetChanged();
                        //chatListView.setSelection(0);
                    }else {
                        Toast.makeText(ChatActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onHistory notifyDataSetChanged");
                    swipeView.setRefreshing(false);
                    break;
                case HANDLER_HISTORY_ERROR:
                    ResponseError error = (ResponseError)msg.obj;
                    Toast.makeText(ChatActivity.this, "errno:"+error.getErrno()+",msg:"+error.getError(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onHistory error");
                    swipeView.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    private DbInsertListener chatRecorInsertListener = new DbInsertListener() {
        @Override
        public void callback(Object obj) {
            ChatMessage message = (ChatMessage) obj;
            if(message.getChatgroupId()==chatgroupId){
                chatAdapter.add(message);
                //chatAdapter.notifyDataSetChanged();
            }
            Log.e(TAG, "insert callback");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final Intent intent = getIntent();
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

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
        seq = 0;

        if(intent.getBooleanExtra("notInContacts", false))
            notInContacts=true;

        messageList =  chatGroup.getMessageList();
        //messageList = globalInfos.getChatContent(chatGroup.getChatgroupId());

        /*
        chat.setDataChangeListener(new Chat.DataChangeListener() {
            @Override
            public void message(int friendId, int type) {
                Message msg = new Message();

                if(msg.arg1 == friendId) {
                    msg.what = HANDLER_MESSAGE_RECEIVE;
                    msg.arg1 = friendId;
                    msg.arg2 = type;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void messageResponse(ChatMessageResponse response) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE_RESP;
                msg.obj = response;
                handler.sendMessage(msg);
            }
        });
        */
        chat.setHistoryListener(new Chat.HistoryListener() {
            @Override
            public void history(ResponseError error, int mFriendid, int messageSize) {
                Log.e(TAG, "chatActivity friendId:"+mFriendid+", messageSize:"+messageSize);
                if(error!=null){
                    Toast.makeText(getApplicationContext(), "historyError"+error.getError()+", errno:"+error.getErrno(), Toast.LENGTH_SHORT).show();
                }else {
                    //Log.e(TAG, mFriendid+", "+friendId);
                    if(mFriendid == friendId){
                        Message msg = new Message();
                        msg.arg1 = messageSize;
                        msg.what = HANDLER_HISTORY_SUCCESS;
                        handler.sendMessage(msg);
                        //Log.e(TAG, "history send HANDLER_HISTORY_SUCCESS");
                    }
                }
            }
        });

        DbHelper.getInstance(getBaseContext()).appendInsertListener("t_chatrecord", chatRecorInsertListener);

        //获取好友聊天内容
        chatAdapter = new ChatAdapter(ChatActivity.this, R.layout.chat_other, messageList);
        chatListView = (ListView) findViewById(R.id.chat_view);
        chatListView.setItemsCanFocus(false);
        chatListView.setAdapter(chatAdapter);
        chatListView.setOnScrollListener(this);
        //进入界面默认显示最后一条消息
        chatListView.setSelection(messageList.size());
        chatAdapter.notifyDataSetChanged();
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
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
                if(count==0){
                    ivMore.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);
                }else{
                    ivMore.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivContentType.setTag(R.drawable.tn);

        //一进来就显示最新的聊天消息
        chatAdapter.notifyDataSetChanged();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                if (content.length()==0) {
                    Toast.makeText(getApplicationContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatMessage msg = new ChatMessage(userId,friendId,chatgroupId, ChatMessage.MSG_TYPE_TEXT, content,new Date(), seq,ChatMessage.MSG_SENDING);
                messageList.add(msg);

                etContent.setText("");
                chatAdapter.notifyDataSetChanged();

                //to do对发送的消息进行转圈圈, 由messageResponse取消圈圈
                //Log.d(TAG, "userid:"+userId);
                Log.d(TAG, "gson:" + gson.toJson(msg));

                chat.emit("message", gson.toJson(msg));

                //本地主动发送的消息入库

                //该聊天记录不在聊天列表里
                if(notInContacts) {
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
                values.put("time", TimeHelper.dateFormat(msg.getTime()));
                //to do 为每一条消息分配一个seq
                values.put("seq", 0);
                DbHelper.getInstance(getBaseContext()).insert("t_chatrecord", null, values);
            }
        });
        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.notifyDataSetChanged();
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
        swipeView.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*
                swipeView.setRefreshing(true);
                //请求历史数据
                History history = new History(userId, friendId, globalInfos.getHistoryIds(friendId));
                Log.e(TAG, "history:"+gson.toJson(history));
                chat.emit("history", gson.toJson(history));
                */
            }
        });
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.e(TAG, "firstVisibleItem:"+firstVisibleItem+", visibleItemCount:"+visibleItemCount+", totalItemCount:"+totalItemCount);
        if (firstVisibleItem == 0)
            swipeView.setEnabled(true);
        else
            swipeView.setEnabled(false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }
}
