package com.lessask.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;
import com.lessask.OnItemClickListener;
import com.lessask.R;
import com.lessask.model.LessonItem;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/24.
 */
public class LessonAdapter2 extends BaseLoadMoreRecyclerAdapter<LessonItem, LessonAdapter2.MyViewHolder> {

    private static final String TAG=LessonAdapter2.class.getSimpleName();
    private OnItemClickListener onItemClickListener;

    private Context context;

    public LessonAdapter2(Context context){
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        return new MyViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindItemViewHolder(MyViewHolder holder, final int position) {
        LessonItem data = getItem(position);
        holder.name.setText(data.getName() + "分钟");
        holder.address.setText(data.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "real click", Toast.LENGTH_SHORT).show();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        ArrayList<String> tags = data.getTags();

        StringBuilder builder = new StringBuilder();
        for(int i=0;i<tags.size();i++){
            builder.append(tags.get(i));
            builder.append("  ");
        }
        holder.tags.setText(builder.toString());
        holder.time.setText(""+data.getTime());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView address;
        TextView tags;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            address = (TextView)itemView.findViewById(R.id.address);
            tags = (TextView)itemView.findViewById(R.id.tags);
        }
    }
}
