package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
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
import java.util.List;


public class ChatActivity extends Activity {

    private List<Message> messageList = new ArrayList<Message>();
    private final static String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initChatItemList();
        final ChatAdapter chatAdapter = new ChatAdapter(ChatActivity.this, R.layout.chat_item, messageList);
        ListView chatListView = (ListView) findViewById(R.id.chat_view);
        chatListView.setAdapter(chatAdapter);
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final ListView lvChatView = (ListView) findViewById(R.id.chat_view);
        final ImageView ivContentType = (ImageView) findViewById(R.id.content_type);
        final EditText etContent = (EditText) findViewById(R.id.content);
        ImageView ivSend = (ImageView) findViewById(R.id.send);

        ivContentType.setTag(R.drawable.tn);

        //一进来就显示最新的聊天消息
        chatAdapter.notifyDataSetChanged();
        lvChatView.setSelection(chatAdapter.getCount());

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                if (content.length()==0) {
                    Toast.makeText(getApplicationContext(), "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                View itemView = layoutInflater.inflate(R.layout.chat_item, null);
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
                etContent.setText("");
                chatAdapter.notifyDataSetChanged();
                lvChatView.setSelection(chatAdapter.getCount());

            }
        });
        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.notifyDataSetChanged();
                lvChatView.setSelection(chatAdapter.getCount());
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

    private void initChatItemList(){
        for(int i=0;i<1000;i++){
            Message item;
            if(i%2 == 0) {
                item = new Message(Message.TYPE_RECEIVED_TEXT, "聊点什么" + i, R.mipmap.ic_launcher);
            }else{
                item = new Message(Message.TYPE_SEND_TEXT, "随便聊点什么都可以啊" + i, R.mipmap.ic_launcher);
            }
            messageList.add(item);
        }
    }
}
