package com.example.jhuang.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lessask.chat.ChatMessage;

import java.util.List;

/**
 * Created by JHuang on 2015/8/1.
 */

public class ChatAdapter extends ArrayAdapter<ChatMessage>{
    private static String TAG = "ChatAdapter";
    private int resourceId;
    public ChatAdapter(Context context, int textViewResourceId, List<ChatMessage> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    //listview只创建一屏+1的ItemView,可以通过convertView来复用这些对象
    //这里不能简单的缓冲,因为好几种消息类型用不一样的布局的
    //通过View的setTag来缓冲对象
    public View getView(int position, View convertView, ViewGroup parent){
        ChatMessage itemData = getItem(position);
        View itemView;
        int msgType = itemData.getType();
        ViewHolder viewHolder;
        if(convertView!=null){
            itemView = convertView;
            viewHolder = (ViewHolder) itemView.getTag();
        }else{
            //指定加载的布局
            itemView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) itemView.findViewById(R.id.layout_other_msg);
            viewHolder.rightLayout = (LinearLayout) itemView.findViewById(R.id.layout_my_msg);
            viewHolder.leftHeadImg = (ImageView) itemView.findViewById(R.id.other_head_img);
            viewHolder.rightHeadImg = (ImageView) itemView.findViewById(R.id.my_head_img);
            viewHolder.leftMsg = (TextView) itemView.findViewById(R.id.chat_item_other_content);
            viewHolder.rightMsg = (TextView) itemView.findViewById(R.id.chat_item_my_content);
            itemView.setTag(viewHolder);
        }

        switch (msgType){
            case ChatMessage.TYPE_RECEIVED_TEXT:
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftHeadImg.setImageResource(R.mipmap.ic_launcher);
                viewHolder.leftMsg.setText(itemData.getContent());
                break;
            case ChatMessage.TYPE_SEND_TEXT:
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightHeadImg.setImageResource(R.mipmap.ic_launcher);
                viewHolder.rightMsg.setText(itemData.getContent());
                break;
            default:
                break;
        }
        return itemView;
    }

    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        ImageView leftHeadImg;
        TextView leftMsg;
        ImageView rightHeadImg;
        TextView rightMsg;
    }
}
