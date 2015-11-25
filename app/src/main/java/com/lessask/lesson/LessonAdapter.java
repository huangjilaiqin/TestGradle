package com.lessask.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;
import com.github.captain_miao.recyclerviewutils.listener.OnRecyclerItemClickListener;
import com.lessask.MainActivity;
import com.lessask.OnItemClickListener;
import com.lessask.R;
import com.lessask.RecyclerViewDragHolder;
import com.lessask.model.LessonItem;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/24.
 */
public class LessonAdapter extends BaseLoadMoreRecyclerAdapter<LessonItem, RecyclerView.ViewHolder> implements OnRecyclerItemClickListener,AdapterView.OnItemLongClickListener{

    private static final String TAG=LessonAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;

    private Context context;

    public LessonAdapter(Context context){
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.bg_menu, null);
        mybg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new MyViewHolder(context, mybg, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder)RecyclerViewDragHolder.getHolder(holder);
        LessonItem data = getItem(position);
        myHolder.name.setText(data.getName()+"分钟");
        myHolder.address.setText(data.getAddress());
        /*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        */
        myHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stringArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
        myHolder.closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "关闭菜单", Toast.LENGTH_SHORT).show();
                myHolder.close();
            }
        });

        ArrayList<String> tags = data.getTags();

        StringBuilder builder = new StringBuilder();
        for(int i=0;i<tags.size();i++){
            builder.append(tags.get(i));
            builder.append("  ");
        }
        myHolder.tags.setText(builder.toString());
        myHolder.time.setText(""+data.getTime());
    }

    @Override
    public void onClick(View v, int position) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public static class MyViewHolder extends RecyclerViewDragHolder{
        TextView name;
        TextView time;
        TextView address;
        TextView tags;

        //左滑菜单
        TextView deleteItem;
        TextView closeApp;

        public MyViewHolder(Context context, View bgView, View topView) {
            super(context, bgView, topView);
        }

        public MyViewHolder(Context context, View bgView, View topView, int mTrackingEdges) {
            super(context, bgView, topView, mTrackingEdges);
        }

        @Override
        public void initView(View itemView) {
            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            address = (TextView)itemView.findViewById(R.id.address);
            tags = (TextView)itemView.findViewById(R.id.tags);
            deleteItem = (TextView) itemView.findViewById(R.id.delete);
            closeApp = (TextView) itemView.findViewById(R.id.closeMenu);
        }
    }
}
