package com.lessask.lesson;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.ItemTouchHelperAdapter;
import com.lessask.recyclerview.OnStartDragListener;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.video.PlayVideoActiviy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by huangji on 2015/12/22.
 */
public class LessonActionsAdapter extends BaseRecyclerAdapter<LessonAction, RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {
    private String TAG = LessonActionsAdapter.class.getSimpleName();
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemMenuClickListener onItemMenuClickListener;

    private final OnStartDragListener mDragStartListener;
    private final CoordinatorLayout coordinatorLayout;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    LessonActionsAdapter(Context context, OnStartDragListener onStartDragListener,CoordinatorLayout coordinatorLayout){
        this.context = context;
        this.mDragStartListener = onStartDragListener;
        this.coordinatorLayout = coordinatorLayout;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener){
        this.onItemMenuClickListener = onItemMenuClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //获取item布局
        View itemMenu = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_action_menu, null);
        itemMenu.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //item布局要有两层layout在第二层layout里面放控件,背景色设置为不透明，否则bg_menu会显示出来
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_lesson_action_item, null);
        //Height要使用WRAP_CONTENT否则内容会被挤在一起显示不出来
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //生成返回RecyclerView.ViewHolder
        return new ItemViewHolder(context, itemMenu, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder myholder = (ItemViewHolder)RecyclerViewDragHolder.getHolder(holder);
        final LessonAction info = getItem(position);
        myholder.actionName.setText(info.getActionName());
        myholder.actionPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayVideoActiviy.class);
                if (info.getVideoName() == null) {
                    Toast.makeText(context, "file is not exist", Toast.LENGTH_SHORT).show();
                    return;
                }
                File videoFile = new File(config.getVideoCachePath(), info.getVideoName());

                intent.putExtra("video_path", videoFile.getAbsolutePath());
                intent.putExtra("video_url", config.getVideoUrl() + info.getVideoName());
                context.startActivity(intent);
            }
        });

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(myholder.actionPic, R.drawable.man, R.drawable.women);
        Log.e(TAG, info.getActionName()+", "+info.getActionImage());
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + info.getActionImage(), listener);

        myholder.getTopView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myholder.isOpen()) {
                    myholder.close();
                } else {
                    // do something
                }
            }
        });
        myholder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
                notifyItemRemoved(position);
            }
        });

        myholder.groups.setOnTouchListener(myholder);
        myholder.groups.setText(info.getGroups() + "组");
        myholder.times.setOnTouchListener(myholder);
        myholder.times.setText(info.getTimes() + "个");
        myholder.resetTime.setOnTouchListener(myholder);
        Log.e(TAG, "resetTime:"+info.getResetTime());
        myholder.resetTime.setText(info.getResetTime() + "秒");
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
        Log.e(TAG, "begin");
        for (int i=0;i<list.size();i++){
            LessonAction info = (LessonAction)list.get(i);
            Log.e(TAG, "info:"+info.getActionId());
        }
        Log.e(TAG, "over");
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        /*
        final LessonAction lessonActionInfo = getList().get(position);
        Snackbar.make(coordinatorLayout, "删除动作", Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "onclick", Toast.LENGTH_SHORT).show();
                getList().add(position, lessonActionInfo);
                notifyItemInserted(position);
            }
        }).show();
        getList().remove(position);
        notifyItemRemoved(position);
        */
    }



    public class ItemViewHolder  extends RecyclerViewDragHolder implements View.OnTouchListener{

        public TextView actionName;
        public ImageView actionPic;
        public EditText groups;
        public EditText times;
        public EditText resetTime;
        //左滑菜单
        TextView deleteItem;

        public ItemViewHolder(Context context, View bgView, View topView) {
            super(context, bgView, topView);
        }

        public ItemViewHolder(Context context, View bgView, View topView, int mTrackingEdges) {
            super(context, bgView, topView, mTrackingEdges);
        }

        @Override
        public void initView(View itemView) {
            actionName = (TextView) itemView.findViewById(R.id.name);
            actionPic = (ImageView) itemView.findViewById(R.id.action_pic);
            groups = (EditText) itemView.findViewById(R.id.groups);
            times = (EditText) itemView.findViewById(R.id.times);
            resetTime = (EditText) itemView.findViewById(R.id.reset_time);

            deleteItem = (TextView) itemView.findViewById(R.id.delete);
            RecyclerView.ViewHolder viewHolder=getDragViewHolder();
        }

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                final EditText et;
                int pos;
                final LessonAction lessonAction = getItem(((RecyclerView.ViewHolder)getDragViewHolder()).getAdapterPosition());
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
                                lessonAction.setGroups(Integer.parseInt(data.replace("组","")));
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
                                lessonAction.setTimes(Integer.parseInt(data.replace("个", "")));
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
                    case R.id.reset_time:
                        et = (EditText) v;
                        ArrayList<String> groupRestTimeValues = new ArrayList<>();
                        for (int i = 1; i < 181; i++)
                            groupRestTimeValues.add(i + "秒");
                        stringPickerDialog = new StringPickerDialog(context, groupRestTimeValues, new StringPickerDialog.OnSelectListener() {
                            @Override
                            public void onSelect(String data) {
                                et.setText(data);
                                lessonAction.setResetTime(Integer.parseInt(data.replace("秒", "")));
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
                }
            }
            return false;
        }

    }
}
