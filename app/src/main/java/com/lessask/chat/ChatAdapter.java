package com.lessask.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.*;
import com.android.volley.toolbox.Volley;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.net.VolleyHelper;

import java.io.File;
import java.util.List;


/**
 * Created by JHuang on 2015/8/1.
 */

public class ChatAdapter extends ArrayAdapter<ChatMessage>{
    private static String TAG = "ChatAdapter";
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private int resourceId;
    private Context context;
    private RequestQueue requestQueue;
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
    private ImageCache imageCache;
    private ImageLoader imageLoader;
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();

    public ChatAdapter(Context context, int textViewResourceId, List<ChatMessage> objects){
        super(context, textViewResourceId, objects);
        this.context = context;
        resourceId = textViewResourceId;
        requestQueue = Volley.newRequestQueue(getContext());
        imageCache = new ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap bitmap) {
                lruCache.put(key, bitmap);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        imageLoader = new ImageLoader(requestQueue, imageCache);
    }

    /*
    * 该方法的返回值范围: [0, getViewTypeCount()-1]
    * */
    @Override
    public int getItemViewType(int position) {
        int type=0;
        ChatMessage msg = getItem(position);
        if(msg.getUserid()!=globalInfos.getUserId())
            type = msg.getType();
        else
            type = msg.getType()+1;
        return type;
    }

    //返回消息的类型数量
    @Override
    public int getViewTypeCount() {
        return ChatMessage.MSG_TYPE_SIZE;
    }

    //listview只创建一屏+1的ItemView,可以通过convertView来复用这些对象
    //这里不能简单的缓冲,因为好几种消息类型用不一样的布局的
    //通过View的setTag来缓冲对象
    public View getView(int position, View convertView, ViewGroup parent){
        //Log.d(TAG, "getView position:"+position);
        //获取数据对象
        ChatMessage itemData = getItem(position);
        //获取数据类型对象
        int viewType = getItemViewType(position);
        int msgType = itemData.getType();
        MeViewHolder meViewHolder = null;
        OtherViewHolder otherViewHolder = null;
        TimeViewHolder timeViewHolder = null;

        //获取数据对应的视图对象
        if(convertView!=null){
            //接收到的消息
            if(globalInfos.getUserId()!=itemData.getUserid()) {
                switch (msgType) {
                    case ChatMessage.MSG_TYPE_TEXT:
                        otherViewHolder = (OtherViewHolder) convertView.getTag();
                        break;
                    case ChatMessage.MSG_TYPE_TIME:
                        timeViewHolder = (TimeViewHolder)convertView.getTag();
                        break;
                    default:
                        break;
                }
            }else {
                switch (msgType) {
                    case ChatMessage.MSG_TYPE_TEXT:
                        meViewHolder = (MeViewHolder) convertView.getTag();
                        break;
                }
            }
        }else{
            if(globalInfos.getUserId()!=itemData.getUserid()) {
                switch (msgType) {
                    case ChatMessage.MSG_TYPE_TEXT:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_other, null);
                        otherViewHolder = new OtherViewHolder();
                        convertView.setTag(otherViewHolder);
                        otherViewHolder.headImg = (ImageView) convertView.findViewById(R.id.head_img);
                        otherViewHolder.msg = (TextView) convertView.findViewById(R.id.content);
                        break;
                    case ChatMessage.MSG_TYPE_TIME:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_time, null);
                        timeViewHolder = new TimeViewHolder();
                        convertView.setTag(timeViewHolder);
                        timeViewHolder.time = (TextView)convertView.findViewById(R.id.time);
                        break;
                    default:
                        break;
                }
            }else {
                switch (msgType) {
                    case ChatMessage.MSG_TYPE_TEXT:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_me, null);
                        meViewHolder = new MeViewHolder();
                        convertView.setTag(meViewHolder);
                        //meViewHolder.headImg = (ImageView) convertView.findViewById(R.id.head_img);
                        meViewHolder.msg = (TextView) convertView.findViewById(R.id.content);
                        break;
                }
            }
        }
        //将数据设置到视图中
        if(globalInfos.getUserId()!=itemData.getUserid()) {
            switch (msgType) {
                case ChatMessage.MSG_TYPE_TEXT:
                    otherViewHolder.msg.setText(itemData.getContent());
                    //itemData.getFriendid() 根据好友id在本地存储图片
                    //设置静态资源
                    otherViewHolder.headImg.setImageResource(R.mipmap.ic_launcher);
                    //设置动态加载的资源
                    String friendHeadImgUrl = imageUrlPrefix + itemData.getUserid() + ".jpg";
                    //File friendHeadImgFile = new File(globalInfos.getHeadImgDir().getAbsolutePath(), itemData.getUserid() + ".jpg");
                    //Utils.getImgFromLocalOrNet(friendHeadImgFile, friendHeadImgUrl, otherViewHolder.headImg);
                    ImageListener listener = ImageLoader.getImageListener(otherViewHolder.headImg, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    Log.e(TAG, "received:"+friendHeadImgUrl);
                    VolleyHelper.getInstance().getImageLoader().get(friendHeadImgUrl, listener, 100, 100);
                    break;
                default:
                    break;
            }
        }else{
            switch (msgType) {
                case ChatMessage.MSG_TYPE_TEXT:
                    meViewHolder.msg.setText(itemData.getContent());

                    /*
                    meViewHolder.headImg.setImageResource(R.mipmap.ic_launcher);
                    String userHeadImgUrl = globalInfos.getHeadImgHost()+itemData.getUserid()+".jpg";
                    File userHeadImgFile = new File(globalInfos.getHeadImgDir().getAbsolutePath(), itemData.getUserid()+".jpg");
                    ImageListener listener = ImageLoader.getImageListener(meViewHolder.headImg,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    //Log.e(TAG, "send:"+userHeadImgUrl);
                    imageLoader.get(userHeadImgUrl, listener);
                    */
                    break;
            }
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
