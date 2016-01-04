package com.lessask.action;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lessask.OnItemClickListener;
import com.lessask.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.video.PlayVideoActiviy;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/24.
 */
public class ActionAdapter extends BaseRecyclerAdapter<ActionItem, RecyclerView.ViewHolder> {

    private static final String TAG=ActionAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private OnItemMenuClickListener onItemMenuClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private ActionTagsHolder actionTagsHolder = globalInfos.getActionTagsHolder();

    private Context context;

    public ActionAdapter(Context context){
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
        this.onItemMenuClickListener = onItemMenuClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_menu, null);
        mybg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_item, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new MyViewHolder(context, mybg, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Log.e(TAG, "onBindViewHolder:"+position);
        final MyViewHolder myHolder = (MyViewHolder)RecyclerViewDragHolder.getHolder(holder);
        final ActionItem data = getItem(position);
        myHolder.name.setText(data.getName());
        myHolder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayVideoActiviy.class);
                if(data.getVideoName()==null){
                    Log.e(TAG, "videoFile is null:"+data.getVideoName());
                    Toast.makeText(context, "file is not exist", Toast.LENGTH_SHORT).show();
                    return;
                }
                File videoFile = new File(config.getVideoCachePath(), data.getVideoName());

                intent.putExtra("video_path", videoFile.getAbsolutePath());
                intent.putExtra("video_url", config.getVideoUrl()+data.getVideoName());
                context.startActivity(intent);
            }
        });
        myHolder.getTopView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myHolder.isOpen()){
                    myHolder.close();
                }else {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            }
        });
        myHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemMenuClickListener!=null){
                    onItemMenuClickListener.onItemMenuClick(v, position);
                }
            }
        });

        ArrayList<Integer> tags = data.getTags();

        StringBuilder builder = new StringBuilder();

        for(int i=0;i<tags.size();i++){
            Log.e(TAG, "id:"+tags.get(i));
            Log.e(TAG, "id:"+actionTagsHolder.getActionTagNameById(tags.get(i)));
            builder.append(actionTagsHolder.getActionTagNameById(tags.get(i)));
            builder.append(" ");
        }
        myHolder.tags.setText(builder.toString());

    }

    public static class MyViewHolder extends RecyclerViewDragHolder{
        ImageView video;
        TextView name;
        TextView tags;

        //左滑菜单
        TextView deleteItem;

        public MyViewHolder(Context context, View bgView, View topView) {
            super(context, bgView, topView);
        }

        public MyViewHolder(Context context, View bgView, View topView, int mTrackingEdges) {
            super(context, bgView, topView, mTrackingEdges);
        }

        @Override
        public void initView(View itemView) {
            video = (ImageView)itemView.findViewById(R.id.video);
            name = (TextView)itemView.findViewById(R.id.name);
            tags = (TextView)itemView.findViewById(R.id.tags);

            deleteItem = (TextView) itemView.findViewById(R.id.delete);
        }
    }
}
