package com.lessask.lesson;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lessask.R;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.ItemTouchHelperAdapter;
import com.lessask.recyclerview.ItemTouchHelperViewHolder;
import com.lessask.recyclerview.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by huangji on 2015/12/22.
 */
public class LessonActionsAdapter extends BaseRecyclerAdapter<LessonActionInfo, LessonActionsAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    private String TAG = LessonActionsAdapter.class.getSimpleName();
    private Context context;
    private final OnStartDragListener mDragStartListener;

    LessonActionsAdapter(Context context, OnStartDragListener onStartDragListener){
        this.context = context;
        this.mDragStartListener = onStartDragListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //获取item布局
        //item布局要有两层layout在第二层layout里面放控件,背景色设置为不透明，否则bg_menu会显示出来
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_lesson_action_item, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //生成返回RecyclerView.ViewHolder
        Log.e(TAG, "createViewHolder");
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        LessonActionInfo info = getItem(position);
        holder.actionName.setText(info.getActionName());
        /*
        holder.actionPic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
        */
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        List list = getList();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        getList().remove(position);
        notifyItemRemoved(position);
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView actionName;
        public final ImageView actionPic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            actionName = (TextView) itemView.findViewById(R.id.name);
            actionPic = (ImageView) itemView.findViewById(R.id.action_pic);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
