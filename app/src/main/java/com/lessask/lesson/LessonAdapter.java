package com.lessask.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.OnItemClickListener;
import com.lessask.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.model.Lesson;
import com.lessask.util.ArrayUtil;

import java.util.Iterator;

/**
 * Created by JHuang on 2015/11/24.
 */
public class LessonAdapter extends BaseRecyclerAdapter<Lesson, RecyclerView.ViewHolder> {

    private static final String TAG=LessonAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private OnItemMenuClickListener onItemMenuClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

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
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener){
        this.onItemMenuClickListener = onItemMenuClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myHolder = (MyViewHolder)RecyclerViewDragHolder.getHolder(holder);
        Lesson data = getItem(position);
        myHolder.name.setText(data.getName());
        myHolder.address.setText(data.getAddress());
        myHolder.time.setText(data.getCostTime()+"分钟");
        myHolder.purpose.setText(data.getPurpose());
        myHolder.bodies.setText(ArrayUtil.join(data.getBodies(), " "));
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(myHolder.cover,R.drawable.man, R.drawable.women);
        Log.e(TAG, "load img:" + config.getImgUrl() + data.getCover());
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + data.getCover(), listener);

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
        myHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemMenuClickListener != null) {
                    onItemMenuClickListener.onItemMenuClick(v, position);
                }
            }
        });
        myHolder.distributeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemMenuClickListener != null) {
                    onItemMenuClickListener.onItemMenuClick(v, position);
                }
            }
        });


    }

    public static class MyViewHolder extends RecyclerViewDragHolder{
        ImageView cover;
        TextView name;
        TextView time;
        TextView address;
        TextView purpose;
        TextView bodies;

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
            cover = (ImageView)itemView.findViewById(R.id.cover);
            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            address = (TextView)itemView.findViewById(R.id.address);
            purpose = (TextView)itemView.findViewById(R.id.purpose);
            bodies = (TextView)itemView.findViewById(R.id.bodies);

            deleteItem = (TextView) itemView.findViewById(R.id.delete);
            distributeItem = (TextView) itemView.findViewById(R.id.distribute);
        }
    }
}
