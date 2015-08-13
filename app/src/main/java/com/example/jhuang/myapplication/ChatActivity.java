package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import android.os.Handler;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.chat.ChatContext;
import com.model.ChatMessage;
import com.model.ChatMessageResponse;


public class ChatActivity extends Activity {

    private static final int HANDLER_MESSAGE = 0;
    private static final int HANDLER_MESSAGE_RESP = 1;

    private final static String TAG = "ChatActivity";
    private ListView chatListView;
    private static ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> messageArrayList;

    private ListView lvChatView;
    private EditText etContent;

    private Chat chat = Chat.getInstance();
    private ChatContext chatContext = ChatContext.getInstance();
    private Gson gson = new Gson();
    private MyApplication app;
    private int userId;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "onMessage handler");
            switch (msg.what){
                case ChatMessage.VIEW_TYPE_RECEIVED_TEXT:
                    ChatMessage chatMessage = (ChatMessage)msg.obj;
                    ArrayList mList = chatContext.getChatContent(2);
                    mList.add(chatMessage);

                    chatAdapter.notifyDataSetChanged();
                    //lvChatView.setSelection(chatAdapter.getCount()-1);
                    Log.d(TAG, "onMessage notifyDataSetChanged");
                    break;
                case HANDLER_MESSAGE_RESP:
                    //根据消息响应的状态改变界面
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        app  = (MyApplication)getApplication();
        userId = app.getUserid();

        chat.setDataChangeListener(new Chat.DataChangeListener() {
            @Override
            public void message(ChatMessage chatMessage) {
                Message msg = new Message();
                msg.what = ChatMessage.VIEW_TYPE_RECEIVED_TEXT;
                msg.obj = chatMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void messageResponse(ChatMessageResponse response) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE_RESP;
                msg.obj = response;
                handler.sendMessage(msg);
            }
        });
        messageArrayList = ChatContext.getInstance().getChatContent(2);
        chatAdapter = new ChatAdapter(ChatActivity.this, R.layout.chat_other, messageArrayList);
        chatListView = (ListView) findViewById(R.id.chat_view);
        chatListView.setAdapter(chatAdapter);
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        lvChatView = (ListView) findViewById(R.id.chat_view);
        //消息类型
        final ImageView ivContentType = (ImageView) findViewById(R.id.content_type);
        //输入框
        etContent = (EditText) findViewById(R.id.content);
        //发送
        ImageView ivSend = (ImageView) findViewById(R.id.send);

        ivContentType.setTag(R.drawable.tn);

        //一进来就显示最新的聊天消息
        chatAdapter.notifyDataSetChanged();
        //lvChatView.setSelection(chatAdapter.getCount()-1);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                if (content.length()==0) {
                    Toast.makeText(getApplicationContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*
                View itemView = layoutInflater.inflate(R.layout.chat_other, null);
                LinearLayout leftLayout = (LinearLayout) itemView.findViewById(R.id.layout_other_msg);
                LinearLayout rightLayout = (LinearLayout) itemView.findViewById(R.id.layout_my_msg);
                //ImageView leftHeadImg = (ImageView) itemView.findViewById(R.id.other_head_img);
                ImageView rightHeadImg = (ImageView) itemView.findViewById(R.id.my_head_img);
                //TextView leftMsg = (TextView) itemView.findViewById(R.id.chat_item_other_content);
                TextView rightMsg = (TextView) itemView.findViewById(R.id.chat_item_my_content);

                rightLayout.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.GONE);
                rightHeadImg.setImageResource(R.mipmap.ic_launcher);
                rightMsg.setText(content);

                lvChatView.addFooterView(itemView);
                */
                ArrayList mList = chatContext.getChatContent(2);
                ChatMessage msg = new ChatMessage(userId, 2, ChatMessage.MSG_TYPE_TEXT, content, null, 112, ChatMessage.VIEW_TYPE_SEND_TEXT);
                mList.add(msg);

                etContent.setText("");
                chatAdapter.notifyDataSetChanged();
                //lvChatView.setSelection(chatAdapter.getCount());

                //to do对发送的消息进行转圈圈, 由messageResponse取消圈圈
                Log.d(TAG, "userid:"+userId);
                Log.d(TAG, "gson:"+gson.toJson(msg));
                chat.emit("message", gson.toJson(msg));

            }
        });
        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.notifyDataSetChanged();
                //lvChatView.setSelection(chatAdapter.getCount());
            }
        });
        ivContentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView)v;
                int resourceId = (int)iv.getTag();
                Log.d("ChatActivity", ""+resourceId+","+R.drawable.tn+","+R.drawable.tl);
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
                     lvChatView.setSelection(chatAdapter.getCount());
                }
            }
        });
        */

    }

}
