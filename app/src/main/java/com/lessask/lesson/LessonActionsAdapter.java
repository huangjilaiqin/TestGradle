package com.lessask.lesson;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lessask.R;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.ItemTouchHelperAdapter;
import com.lessask.recyclerview.ItemTouchHelperViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by huangji on 2015/12/22.
 */
public class LessonActionsAdapter extends BaseRecyclerAdapter<LessonActionInfo, LessonActionsAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    private Context context;

    LessonActionsAdapter(Context context){
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //获取item布局
        //item布局要有两层layout在第二层layout里面放控件,背景色设置为不透明，否则bg_menu会显示出来
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_lesson_action_item, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //生成返回RecyclerView.ViewHolder
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        LessonActionInfo info = getItem(position);
        holder.actionName.setText(info.getActionName());
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            actionName = (TextView) itemView.findViewById(R.id.name);
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
