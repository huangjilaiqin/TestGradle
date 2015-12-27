package com.lessask.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lessask.OnItemClickListener;
import com.lessask.R;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.model.Lesson;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/24.
 */
public class LessonAdapter extends BaseRecyclerAdapter<Lesson, RecyclerView.ViewHolder> {

    private static final String TAG=LessonAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;

    private Context context;

    public LessonAdapter(Context context){
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_menu, null);
        mybg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new MyViewHolder(context, mybg, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder)RecyclerViewDragHolder.getHolder(holder);
        Lesson data = getItem(position);
        myHolder.name.setText(data.getName()+"分钟");
        myHolder.address.setText(data.getAddress());
        myHolder.getTopView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myHolder.isOpen()){
                    myHolder.close();
                }else {
                    Toast.makeText(context, "real click", Toast.LENGTH_SHORT).show();
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            }
        });
        myHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stringArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
        myHolder.distributeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "分配", Toast.LENGTH_SHORT).show();
            }
        });


        myHolder.time.setText("" + data.getCostTime());
    }

    public static class MyViewHolder extends RecyclerViewDragHolder{
        TextView name;
        TextView time;
        TextView address;

        //左滑菜单
        TextView deleteItem;
        TextView distributeItem;

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
            deleteItem = (TextView) itemView.findViewById(R.id.delete);
            distributeItem = (TextView) itemView.findViewById(R.id.distribute);
        }
    }
}
