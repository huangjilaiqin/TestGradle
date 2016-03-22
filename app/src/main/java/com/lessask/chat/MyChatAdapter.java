package com.lessask.chat;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.util.DbUtil;

import java.util.List;


/**
 * Created by JHuang on 2015/8/1.
 */

public class MyChatAdapter extends BaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> {
    private static String TAG = "ChatAdapter";
    private Context context;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();

    public MyChatAdapter(Context context){
        this.context = context;
    }

    private OnItemMenuClickListener onItemMenuClickListener;

    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
        this.onItemMenuClickListener=onItemMenuClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case ChatMessage.MSG_TYPE_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_me, parent, false);
                viewHolder = new MeViewHolder(view);
                break;
            case ChatMessage.MSG_TYPE_TEXT+1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_other, parent, false);
                viewHolder = new OtherViewHolder(view);
                break;
            case ChatMessage.MSG_TYPE_TIME:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_time, parent, false);
                viewHolder = new TimeViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ChatMessage itemData = getItem(position);
        ImageView resend = null;
        ProgressBar flower = null;
        int status=0;
        if(holder instanceof OtherViewHolder){
            OtherViewHolder otherViewHolder = (OtherViewHolder)holder;
            otherViewHolder.content.setText(itemData.getContent());
            //itemData.getFriendid() 根据好友id在本地存储图片
            //设置静态资源
            String headImgUrl = imageUrlPrefix+itemData.getUserid()+".jpg";
            //Log.e(TAG, "other :"+headImgUrl);
            ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(otherViewHolder.headImg, 0, 0);
            VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);
            //otherViewHolder.headImg.setImageResource(R.mipmap.ic_launcher);
            status=itemData.getStatus();
            flower = otherViewHolder.flower;
            resend = otherViewHolder.resend;
        }else if(holder instanceof MeViewHolder){
            MeViewHolder meViewHolder = (MeViewHolder) holder;
            meViewHolder.content.setText(itemData.getContent());
            //itemData.getFriendid() 根据好友id在本地存储图片
            //设置静态资源
            //meViewHolder.headImg.setImageResource(R.mipmap.ic_launcher);
            String headImgUrl = imageUrlPrefix+itemData.getUserid()+".jpg";
            Log.e(TAG, "me:"+headImgUrl);
            ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(meViewHolder.headImg, 0, 0);
            VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);
            status=itemData.getStatus();
            flower = meViewHolder.flower;
            resend = meViewHolder.resend;
        }
        if(status==ChatMessage.MSG_SENDING){
            flower.setVisibility(View.VISIBLE);
        }else if(status==ChatMessage.MSG_SEND){
            flower.setVisibility(View.INVISIBLE);
        }else if(status==ChatMessage.MSG_SEND_FAILED){
            flower.setVisibility(View.INVISIBLE);
            resend.setVisibility(View.VISIBLE);
        }else if(status==ChatMessage.MSG_RECEIVC){
            flower.setVisibility(View.INVISIBLE);
            resend.setVisibility(View.INVISIBLE);
        }else {
            Log.e(TAG, "error status:"+status);
        }
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemData.setStatus(ChatMessage.MSG_SENDING);
                //update db
                v.setVisibility(View.INVISIBLE);

                String[] whereValues = new String[]{itemData.getId()+"", itemData.getChatgroupId()};
                ContentValues values = new ContentValues();
                values.put("status", ChatMessage.MSG_SENDING);
                DbHelper.getInstance(context).getDb().update("t_chatrecord",values,"id=? and chatgroup_id=?",whereValues);
                //通知界面
                if(onItemMenuClickListener!=null)
                    onItemMenuClickListener.onItemMenuClick(v,itemData);
            }
        });



    }

    public int getPositionById(int id){
        int positon=-1;
        List list = getList();
        for(int i=list.size()-1;i>0;i--){
            ChatMessage msg = (ChatMessage) list.get(i);
            if(msg.getId()==id){
                positon=i;
                break;
            }
        }
        return positon;
    }

    public void updateItemStatusById(long id,int status){
        int positon=-1;
        List list = getList();
        for(int i=list.size()-1;i>0;i--){
            ChatMessage msg = (ChatMessage) list.get(i);
            if(msg.getId()==id){
                positon=i;
                break;
            }
        }
        if(positon!=-1){
            ChatMessage msg = (ChatMessage) list.get(positon);
            msg.setStatus(status);
            notifyItemChanged(positon);
        }
    }

    /*
            * 该方法的返回值范围: [0, getViewTypeCount()-1]
            * */
    @Override
    public int getItemViewType(int position) {
        int type=0;
        ChatMessage msg = getItem(position);
        if(msg.getUserid()==globalInfos.getUserId())
            type = msg.getType();
        else
            type = msg.getType()+1;
        return type;
    }


    public static class OtherViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView headImg;
        TextView content;
        ProgressBar flower;
        ImageView resend;
        public OtherViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            headImg = (ImageView) itemView.findViewById(R.id.head_img);
            content =(TextView)itemView.findViewById(R.id.content);
            flower = (ProgressBar)itemView.findViewById(R.id.flower);
            resend = (ImageView) itemView.findViewById(R.id.resend);
        }
    }
    public static class MeViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView headImg;
        TextView content;
        ProgressBar flower;
        ImageView resend;
        public MeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            headImg = (ImageView) itemView.findViewById(R.id.head_img);
            content =(TextView)itemView.findViewById(R.id.content);
            flower = (ProgressBar)itemView.findViewById(R.id.flower);
            resend = (ImageView) itemView.findViewById(R.id.resend);
        }
    }
    public static class TimeViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView time;

        public TimeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            time =(TextView)itemView.findViewById(R.id.time);
        }
    }


}
