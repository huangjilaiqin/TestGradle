package com.lessask.lesson;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lessask.R;
import com.lessask.dialog.StringPickerDialog;
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
        implements ItemTouchHelperAdapter,View.OnTouchListener {
    private String TAG = LessonActionsAdapter.class.getSimpleName();
    private Context context;
    private final OnStartDragListener mDragStartListener;
    private final CoordinatorLayout coordinatorLayout;

    LessonActionsAdapter(Context context, OnStartDragListener onStartDragListener,CoordinatorLayout coordinatorLayout){
        this.context = context;
        this.mDragStartListener = onStartDragListener;
        this.coordinatorLayout = coordinatorLayout;
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
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        LessonActionInfo info = getItem(position);
        holder.actionName.setText(info.getActionName());
        holder.groups.setOnTouchListener(this);
        holder.times.setOnTouchListener(this);
        holder.groupRestTime.setOnTouchListener(this);
        holder.actionRestTime.setOnTouchListener(this);
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
    public void onItemDismiss(final int position) {

        final LessonActionInfo lessonActionInfo = getList().get(position);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "删除动作", Snackbar.LENGTH_LONG);
        snackbar.setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "onclick", Toast.LENGTH_SHORT).show();
                getList().add(position, lessonActionInfo);
                notifyItemInserted(position);
            }
        });
        snackbar.show();
        getList().remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
            final EditText et;
            int pos;
            StringPickerDialog stringPickerDialog;
            switch (v.getId()){
                case R.id.groups:
                    et = (EditText) v;
                    ArrayList<String> groupValues = new ArrayList<>();
                    for (int i = 1; i < 10; i++)
                        groupValues.add(i + "组");
                    StringPickerDialog costtimeDialog = new StringPickerDialog(context, groupValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            et.setText(data);
                        }
                    });
                    costtimeDialog.setEditable(false);
                    pos = groupValues.indexOf(et.getText().toString().trim());
                    if (pos == -1) {
                        costtimeDialog.setValue(2);
                    } else {
                        costtimeDialog.setValue(pos);
                    }
                    costtimeDialog.show();
                    break;
                case R.id.times:
                    et = (EditText) v;
                    ArrayList<String> costtimeValues = new ArrayList<>();
                    for (int i = 1; i < 201; i++)
                        costtimeValues.add(i + "个");
                    stringPickerDialog = new StringPickerDialog(context, costtimeValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            et.setText(data);
                        }
                    });
                    stringPickerDialog.setEditable(false);
                    pos = costtimeValues.indexOf(et.getText().toString().trim());
                    if (pos == -1) {
                        stringPickerDialog.setValue(9);
                    } else {
                        stringPickerDialog.setValue(pos);
                    }
                    stringPickerDialog.show();
                    break;
                case R.id.group_rest_time:
                    et = (EditText) v;
                    ArrayList<String> groupRestTimeValues = new ArrayList<>();
                    for (int i = 1; i < 181; i++)
                        groupRestTimeValues.add(i + "秒");
                    stringPickerDialog = new StringPickerDialog(context, groupRestTimeValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            et.setText(data);
                        }
                    });
                    stringPickerDialog.setEditable(false);
                    pos = groupRestTimeValues.indexOf(et.getText().toString().trim());
                    if (pos == -1) {
                        stringPickerDialog.setValue(59);
                    } else {
                        stringPickerDialog.setValue(pos);
                    }
                    stringPickerDialog.show();
                    break;
                case R.id.action_rest_time:
                    et = (EditText) v;
                    ArrayList<String> actionRestTimeValues = new ArrayList<>();
                    for (int i = 1; i < 301; i++)
                        actionRestTimeValues.add(i + "秒");
                    stringPickerDialog = new StringPickerDialog(context, actionRestTimeValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            et.setText(data);
                        }
                    });
                    stringPickerDialog.setEditable(false);
                    pos = actionRestTimeValues.indexOf(et.getText().toString().trim());
                    if (pos == -1) {
                        stringPickerDialog.setValue(119);
                    } else {
                        stringPickerDialog.setValue(pos);
                    }
                    stringPickerDialog.show();
                    break;
            }
        }
        return false;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView actionName;
        public final ImageView actionPic;
        public final EditText groups;
        public final EditText times;
        public final EditText groupRestTime;
        public final EditText actionRestTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            actionName = (TextView) itemView.findViewById(R.id.name);
            actionPic = (ImageView) itemView.findViewById(R.id.action_pic);
            groups = (EditText) itemView.findViewById(R.id.groups);
            times = (EditText) itemView.findViewById(R.id.times);
            groupRestTime = (EditText) itemView.findViewById(R.id.group_rest_time);
            actionRestTime = (EditText) itemView.findViewById(R.id.action_rest_time);
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
