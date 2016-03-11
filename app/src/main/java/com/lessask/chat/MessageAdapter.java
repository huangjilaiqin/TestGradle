package com.lessask.chat;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.dialog.LoadingDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.User;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;
import com.lessask.util.TimeHelper;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by huangji on 2016/2/19.
 */
public class MessageAdapter extends BaseRecyclerAdapter<ChatGroup,MessageAdapter.ViewHolder>{
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private static Context context;
    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void sort(){
        Collections.sort(getList());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ChatGroup chatGroup = getItem(position);
        holder.name.setText(chatGroup.getName());
        Log.e("MessageAdapter", new Date().toString());
        ChatMessage message = chatGroup.getLastMessage();
        if(message!=null) {
            holder.time.setText(TimeHelper.date2Chat(message.getTime()));
            holder.content.setText(message.getContent());
        }else {
            holder.time.setText(TimeHelper.date2Chat(new Date()));
            holder.content.setText("");
        }

        //holder.badge.setText(""+chatGroup.getMessageList().size());
        //holder.badge.show();



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemLongClickListener != null) {
                    Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(10);
                    onItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });
    }

    public int getPositionById(String chatGroupId){
        List<ChatGroup> chatGroups = getList();
        for(int i=0;i<chatGroups.size();i++){
            if(chatGroups.get(i).getChatgroupId().equals(chatGroupId))
                return i;
        }
        return -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView headImg;
        TextView name;
        TextView content;
        TextView time;
        BadgeView badge;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            headImg = (ImageView) itemView.findViewById(R.id.head_img);
            name =(TextView)itemView.findViewById(R.id.name);
            content=(TextView)itemView.findViewById(R.id.content);
            time=(TextView)itemView.findViewById(R.id.time);
            /*
            badge = new BadgeView(MessageAdapter.context, headImg);
            //badge.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
            badge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
                    anim.setInterpolator(new BounceInterpolator());
                    anim.setDuration(1000);
                    badge.toggle(anim, null);
                }
            });
            */
        }
    }
}
