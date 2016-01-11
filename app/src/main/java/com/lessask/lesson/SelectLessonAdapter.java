package com.lessask.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Lesson;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.util.ArrayUtil;

/**
 * Created by JHuang on 2015/11/24.
 */
public class SelectLessonAdapter extends BaseRecyclerAdapter<Lesson, SelectLessonAdapter.MyViewHolder> {

    private static final String TAG=SelectLessonAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private OnItemMenuClickListener onItemMenuClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private Context context;

    public SelectLessonAdapter(Context context){
        this.context = context;
    }
    @Override
    public SelectLessonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_lesson_item, parent, false);
        return new MyViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener){
        this.onItemMenuClickListener = onItemMenuClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myHolder, final int position) {
        Lesson data = getItem(position);
        myHolder.name.setText(data.getName());
        myHolder.address.setText(data.getAddress());
        myHolder.time.setText(data.getCostTime() + "分钟");
        myHolder.purpose.setText(data.getPurpose());
        myHolder.bodies.setText(ArrayUtil.join(data.getBodies(), " "));
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(myHolder.cover,R.drawable.man, R.drawable.women);
        Log.e(TAG, "load img:" + config.getImgUrl() + data.getCover());
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + data.getCover(), listener);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView name;
        TextView time;
        TextView address;
        TextView purpose;
        TextView bodies;
        RadioButton select;


        public MyViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView)itemView.findViewById(R.id.cover);
            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            address = (TextView)itemView.findViewById(R.id.address);
            purpose = (TextView)itemView.findViewById(R.id.purpose);
            bodies = (TextView)itemView.findViewById(R.id.bodies);
            select = (RadioButton)itemView.findViewById(R.id.select);
        }
    }
}
