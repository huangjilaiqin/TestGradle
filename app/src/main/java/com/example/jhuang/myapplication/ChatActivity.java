package com.example.jhuang.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
                int resourceId = ivContentType.getId();
                Log.d("ChatActivity", ""+resourceId+","+R.drawable.tn);
                if(resourceId == R.drawable.tn) {
                    Log.d("ChatActivity", "change to tn");
                    //ivContentType.setImageDrawable(getResources().getDrawable(R.drawable.tl));
                    ivContentType.invalidate();;
                    ivContentType.setImageResource(R.drawable.tl);
                }else{
                    ivContentType.setImageDrawable(getResources().getDrawable(R.drawable.tn));
                    //ivContentType.setImageResource(R.drawable.tn);
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
