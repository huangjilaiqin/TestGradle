package com.lessask.action;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.video.PlayVideoActiviy;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/24.
 */
public class ActionAdapter extends BaseRecyclerAdapter<ActionItem, ActionAdapter.MyViewHolder> {

    private static final String TAG=ActionAdapter.class.getSimpleName();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
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

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
        this.onItemMenuClickListener = onItemMenuClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myHolder, final int position) {

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

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(myHolder.video,R.drawable.man, R.drawable.women);
        Log.e(TAG, "load video img:" + config.getImgUrl() + data.getActionImage());
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + data.getActionImage(), listener);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        myHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemLongClickListener!= null) {
                    Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(10);
                    onItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView video;
        TextView name;
        TextView tags;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            video = (ImageView)itemView.findViewById(R.id.video);
            name = (TextView)itemView.findViewById(R.id.name);
            tags = (TextView)itemView.findViewById(R.id.tags);
        }
    }
}
