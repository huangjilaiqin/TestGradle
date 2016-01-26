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
import com.lessask.custom.RatingBar;
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
import com.lessask.util.ArrayUtil;

/**
 * Created by JHuang on 2015/11/24.
 */
public class WorkoutAdapter extends BaseRecyclerAdapter<Workout, WorkoutAdapter.MyViewHolder> {

    private static final String TAG=WorkoutAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemMenuClickListener onItemMenuClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private Context context;
    private int [] weekSrc = {R.drawable.mon45,R.drawable.tues45,R.drawable.wed45,R.drawable.thur45,R.drawable.fri45,R.drawable.sat45,R.drawable.sun45};

    public WorkoutAdapter(Context context){
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_reset_item, parent, false);
        }
        return new MyViewHolder(view);
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
    public void onBindViewHolder(MyViewHolder myHolder, final int position) {
        Workout data = getItem(position);
        Lesson lesson = data.getLesson();
        if(lesson!=null){
            myHolder.name.setText(lesson.getName());
            //myHolder.address.setText(lesson.getAddress());
            myHolder.time.setText(lesson.getCostTime()+"分钟");
            myHolder.purpose.setText(lesson.getPurpose());
            myHolder.bodies.setText(ArrayUtil.join(lesson.getBodies(), " "));
            myHolder.fatBar.setStar(lesson.getFatEffect());
            myHolder.fatBar.setmClickable(false);
            myHolder.muscleBar.setStar(lesson.getMuscleEffect());
            myHolder.muscleBar.setmClickable(false);

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(myHolder.cover,R.drawable.man, R.drawable.women);
            VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + lesson.getCover(), listener);
        }
        myHolder.week.setImageResource(weekSrc[position]);

        myHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getId()>0?1:0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView cover;
        TextView name;
        TextView time;
        TextView address;
        TextView purpose;
        TextView bodies;
        RatingBar fatBar;
        RatingBar muscleBar;
        ImageView week;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cover = (ImageView)itemView.findViewById(R.id.cover);
            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            //address = (TextView)itemView.findViewById(R.id.address);
            purpose = (TextView)itemView.findViewById(R.id.purpose);
            bodies = (TextView)itemView.findViewById(R.id.bodies);

            /*
            reset = (TextView) itemView.findViewById(R.id.reset);
            change = (TextView) itemView.findViewById(R.id.change);
            add = (TextView) itemView.findViewById(R.id.add);
            */

            fatBar = (RatingBar) itemView.findViewById(R.id.fat_star);
            muscleBar = (RatingBar) itemView.findViewById(R.id.muscle_star);
            week = (ImageView)itemView.findViewById(R.id.week);
        }
    }

}
