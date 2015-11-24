package com.lessask.lesson;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;
import com.github.captain_miao.recyclerviewutils.listener.OnRecyclerItemClickListener;
import com.lessask.R;
import com.lessask.model.LessonItem;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/24.
 */
public class LessonAdapter extends BaseLoadMoreRecyclerAdapter<LessonItem, LessonAdapter.ViewHolder> implements OnRecyclerItemClickListener,AdapterView.OnItemLongClickListener{

    private static final String TAG=LessonAdapter.class.getSimpleName();
    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(LessonAdapter.ViewHolder holder, int position) {
        LessonItem data = getItem(position);
        holder.name.setText(data.getName()+"分钟");
        holder.address.setText(data.getAddress());

        ArrayList<String> tags = data.getTags();

        StringBuilder builder = new StringBuilder();
        for(int i=0;i<tags.size();i++){
            builder.append(tags.get(i));
            builder.append("  ");
        }
        holder.tags.setText(builder.toString());
        holder.time.setText(""+data.getTime());
    }

    @Override
    public void onClick(View v, int position) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView time;
        TextView address;
        TextView tags;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            address = (TextView)itemView.findViewById(R.id.address);
            tags = (TextView)itemView.findViewById(R.id.tags);
        }
    }
}
