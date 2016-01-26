package com.lessask.lesson;

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
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.model.Lesson;
import com.lessask.util.ArrayUtil;

/**
 * Created by JHuang on 2015/11/24.
 */
public class LessonAdapter extends BaseRecyclerAdapter<Lesson, LessonAdapter.MyViewHolder> {

    private static final String TAG=LessonAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private Context context;

    public LessonAdapter(Context context){
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        return new MyViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myHolder, final int position) {
        Lesson data = getItem(position);
        myHolder.name.setText(data.getName());
        myHolder.address.setText(data.getAddress());
        myHolder.costtime.setText(data.getCostTime()+"分钟");
        myHolder.purpose.setText(data.getPurpose());
        myHolder.bodies.setText(ArrayUtil.join(data.getBodies(), " "));
        myHolder.fatBar.setStar(data.getFatEffect());
        myHolder.fatBar.setmClickable(false);
        myHolder.muscleBar.setStar(data.getMuscleEffect());
        myHolder.muscleBar.setmClickable(false);

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(myHolder.cover,R.drawable.man, R.drawable.women);
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + data.getCover(), listener);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView cover;
        TextView name;
        TextView costtime;
        TextView address;
        TextView purpose;
        TextView bodies;
        RatingBar fatBar;
        RatingBar muscleBar;

        //左滑菜单
        TextView deleteItem;
        TextView distributeItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cover = (ImageView)itemView.findViewById(R.id.cover);
            name = (TextView)itemView.findViewById(R.id.name);
            costtime = (TextView)itemView.findViewById(R.id.costtime);
            address = (TextView)itemView.findViewById(R.id.address);
            purpose = (TextView)itemView.findViewById(R.id.purpose);
            bodies = (TextView)itemView.findViewById(R.id.bodies);
            fatBar = (RatingBar)itemView.findViewById(R.id.fat_star);
            muscleBar = (RatingBar)itemView.findViewById(R.id.muscle_star);


            deleteItem = (TextView) itemView.findViewById(R.id.delete);
            distributeItem = (TextView) itemView.findViewById(R.id.distribute);
        }
    }
}
