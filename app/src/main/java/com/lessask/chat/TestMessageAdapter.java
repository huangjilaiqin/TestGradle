package com.lessask.chat;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lessask.R;
import com.lessask.crud.AdapterAction;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;
import com.lessask.util.TimeHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by huangji on 2016/2/19.
 */
public class TestMessageAdapter extends RecyclerView.Adapter<TestMessageAdapter.ViewHolder> implements AdapterAction<ChatGroup> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private Context context;
    public TestMessageAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ChatGroup chatGroup = getItem(position);
        holder.name.setText(chatGroup.getName());
        Log.e("MessageAdapter", new Date().toString());
        ChatMessage message = chatGroup.getLastMessage();
        if(message!=null) {
            holder.time.setText(message.getTime());
            holder.content.setText(message.getContent());
        }else {
            holder.time.setText(TimeHelper.date2Chat(new Date()));
            holder.content.setText("");
        }

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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView headImg;
        TextView name;
        TextView content;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            headImg = (ImageView) itemView.findViewById(R.id.head_img);
            name =(TextView)itemView.findViewById(R.id.name);
            content=(TextView)itemView.findViewById(R.id.content);
            time=(TextView)itemView.findViewById(R.id.time);
        }
    }

    //数据部分处理
    private final List<ChatGroup> mList = new LinkedList<>();
    private final Map<String,Integer> mapDatas = new HashMap<>();

    public List<ChatGroup> getList() {
        return mList;
    }

    //子类操作列表必须使用getList, 操作原来的数据无效，因为这是复制过去的
    public void appendToList(List<ChatGroup> list) {
        if (list == null) {
            return;
        }
        for (int i=0;i<list.size();i++)
            append(list.get(i));
    }

    public void append(ChatGroup t) {
        if (t == null) {
            return;
        }
        mList.add(t);
        mapDatas.put(t.getChatgroupId(), mList.size()-1);
    }

    private void remap(){
        mapDatas.clear();
        for(int i=0;i<mList.size();i++){
            mapDatas.put(mList.get(i).getChatgroupId(),i);
        }
    }

    public void appendToTop(ChatGroup item) {
        if (item == null) {
            return;
        }
        mList.add(0, item);
        remap();
    }

    public void appendToTopList(List<ChatGroup> list) {
        if (list == null) {
            return;
        }
        mList.addAll(0, list);
        remap();
    }


    public void remove(int position) {
        if (position < mList.size() && position >= 0) {
            mList.remove(position);
        }
        remap();
    }

    public void update(int position,ChatGroup obj){
        remove(position);
        notifyItemRemoved(position);
        mList.add(position,obj);
        notifyItemInserted(position);
    }
    public void notifyItemUpdate(int position){
        if (position < mList.size() && position >= 0)
            notifyItemInserted(position);
    }

    public void clear() {
        mList.clear();
        mapDatas.clear();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int getPositionById(String chatGroupId){
        List<ChatGroup> chatGroups = getList();
        for(int i=0;i<chatGroups.size();i++){
            if(chatGroups.get(i).getChatgroupId().equals(chatGroupId))
                return i;
        }
        return -1;
    }
    /*
    public int getPositionById(String id){
        return mapDatas.get(id);
    }
    */

    public ChatGroup getItem(int position) {
        if (position > mList.size() - 1) {
            return null;
        }
        return mList.get(position);
    }
}
