package com.lessask;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lessask.model.ChatMessage;

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


    /*
    * 该方法的返回值范围: [0, getViewTypeCount()-1]
    * */
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    //返回消息的类型数量
    @Override
    public int getViewTypeCount() {
        return 3;
    }

    //listview只创建一屏+1的ItemView,可以通过convertView来复用这些对象
    //这里不能简单的缓冲,因为好几种消息类型用不一样的布局的
    //通过View的setTag来缓冲对象
    public View getView(int position, View convertView, ViewGroup parent){
        Log.d(TAG, "getView position:"+position);
        //获取数据对象
        ChatMessage itemData = getItem(position);
        //获取数据类型对象
        int type = getItemViewType(position);
        MeViewHolder meViewHolder = null;
        OtherViewHolder otherViewHolder = null;
        TimeViewHolder timeViewHolder = null;

        //获取数据对应的视图对象
        if(convertView!=null){
            switch (type){
                case ChatMessage.VIEW_TYPE_RECEIVED:
                    switch (itemData.getType()){
                        case ChatMessage.MSG_TYPE_TEXT:
                            otherViewHolder = (OtherViewHolder)convertView.getTag();
                            break;
                    }
                case ChatMessage.VIEW_TYPE_SEND:
                    switch (itemData.getType()) {
                        case ChatMessage.MSG_TYPE_TEXT:
                            meViewHolder = (MeViewHolder) convertView.getTag();
                            break;
                    }
                case ChatMessage.VIEW_TYPE_TIME:
                    timeViewHolder = (TimeViewHolder)convertView.getTag();
                    break;
                default:
                    break;
            }
        }else{
            switch (type){
                case ChatMessage.VIEW_TYPE_RECEIVED:
                    switch (itemData.getType()){
                        case ChatMessage.MSG_TYPE_TEXT:
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_other, null);
                            otherViewHolder = new OtherViewHolder();
                            convertView.setTag(otherViewHolder);
                            otherViewHolder.headImg = (ImageView)convertView.findViewById(R.id.head_img);
                            otherViewHolder.msg = (TextView)convertView.findViewById(R.id.content);
                            break;
                    }
                case ChatMessage.VIEW_TYPE_SEND:
                    switch (itemData.getType()) {
                        case ChatMessage.MSG_TYPE_TEXT:
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_me, null);
                            meViewHolder = new MeViewHolder();
                            convertView.setTag(meViewHolder);
                            meViewHolder.headImg = (ImageView) convertView.findViewById(R.id.head_img);
                            meViewHolder.msg = (TextView) convertView.findViewById(R.id.content);
                            break;
                    }
                case ChatMessage.VIEW_TYPE_TIME:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_time, null);
                    timeViewHolder = new TimeViewHolder();
                    convertView.setTag(timeViewHolder);
                    timeViewHolder.time = (TextView)convertView.findViewById(R.id.time);
                    break;
                default:
                    break;
            }
        }
        //将数据设置到视图中
        switch (type){
            case ChatMessage.VIEW_TYPE_RECEIVED:
                switch (itemData.getType()){
                    case ChatMessage.MSG_TYPE_TEXT:
                        otherViewHolder.msg.setText(itemData.getContent());
                        //itemData.getFriendid() 根据好友id在本地存储图片
                        //设置静态资源
                        otherViewHolder.headImg.setImageResource(R.mipmap.ic_launcher);
                        //设置动态加载的资源
                        //otherViewHolder.headImg.setImageDrawable();
                        break;
                }
            case ChatMessage.VIEW_TYPE_SEND:
                switch (itemData.getType()) {
                    case ChatMessage.MSG_TYPE_TEXT:
                        meViewHolder.msg.setText(itemData.getContent());
                        meViewHolder.headImg.setImageResource(R.mipmap.ic_launcher);
                        break;
                }
            case ChatMessage.VIEW_TYPE_TIME:
                break;
            default:
                break;
        }
        return convertView;
    }

    static class OtherViewHolder{
        LinearLayout layout;
        ImageView headImg;
        TextView msg;
    }
    static class MeViewHolder{
        LinearLayout layout;
        ImageView headImg;
        TextView msg;
    }
    static class TimeViewHolder{
        LinearLayout layout;
        TextView time;
    }
}
