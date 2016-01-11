package com.lessask.me;

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

import com.android.volley.toolbox.ImageLoader;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Lesson;
import com.lessask.model.Workout;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.util.ArrayUtil;

/**
 * Created by JHuang on 2015/11/24.
 */
public class WorkoutAdapter extends BaseRecyclerAdapter<Workout, RecyclerView.ViewHolder> {

    private static final String TAG=WorkoutAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemMenuClickListener onItemMenuClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private Context context;

    public WorkoutAdapter(Context context){
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mybg,view;
        Log.e(TAG, "type: "+viewType);
        if(viewType==1){
            mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_menu, null);
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        }else {
            mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_reset_menu, null);
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_reset_item, parent, false);
        }
        mybg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new MyViewHolder(context, mybg, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener){
        this.onItemMenuClickListener = onItemMenuClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder)RecyclerViewDragHolder.getHolder(holder);
        Workout data = getItem(position);
        Lesson lesson = data.getLesson();
        if(lesson!=null){
            myHolder.name.setText(lesson.getName());
            myHolder.address.setText(lesson.getAddress());
            myHolder.time.setText(lesson.getCostTime()+"分钟");
            myHolder.purpose.setText(lesson.getPurpose());
            myHolder.bodies.setText(ArrayUtil.join(lesson.getBodies(), " "));
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(myHolder.cover,R.drawable.man, R.drawable.women);
            VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + lesson.getCover(), listener);
            myHolder.reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemMenuClickListener != null) {
                        onItemMenuClickListener.onItemMenuClick(v, position);
                    }
                }
            });
            myHolder.change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemMenuClickListener != null) {
                        onItemMenuClickListener.onItemMenuClick(v, position);
                    }
                }
            });
        }else {
            //休息日的item处理
            myHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemMenuClickListener != null) {
                        onItemMenuClickListener.onItemMenuClick(v, position);
                    }
                }
            });

        }


        //处理菜单打开和未打开是的单击事件
        myHolder.getTopView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myHolder.isOpen()) {
                    myHolder.close();
                } else {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            }
        });
        myHolder.getTopView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemLongClickListener != null) {
                    Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(5);
                    onItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getId()>0?1:0;
    }

    public static class MyViewHolder extends RecyclerViewDragHolder{
        ImageView cover;
        TextView name;
        TextView time;
        TextView address;
        TextView purpose;
        TextView bodies;

        //左滑菜单
        TextView reset;
        TextView change;
        TextView add;

        public MyViewHolder(Context context, View bgView, View topView) {
            super(context, bgView, topView);
        }

        public MyViewHolder(Context context, View bgView, View topView, int mTrackingEdges) {
            super(context, bgView, topView, mTrackingEdges);
        }

        @Override
        public void initView(View itemView) {
            cover = (ImageView)itemView.findViewById(R.id.cover);
            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            address = (TextView)itemView.findViewById(R.id.address);
            purpose = (TextView)itemView.findViewById(R.id.purpose);
            bodies = (TextView)itemView.findViewById(R.id.bodies);

            reset = (TextView) itemView.findViewById(R.id.reset);
            change = (TextView) itemView.findViewById(R.id.change);
            add = (TextView) itemView.findViewById(R.id.add);
        }
    }
}
