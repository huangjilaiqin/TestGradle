package com.lessask.lesson;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.video.PlayVideoActiviy;

import java.io.File;

/**
 * Created by huangji on 2015/12/22.
 */
public class ShowLessonActionsAdapter extends BaseRecyclerAdapter<LessonAction, ShowLessonActionsAdapter.ItemViewHolder> {
    private String TAG = ShowLessonActionsAdapter.class.getSimpleName();
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemMenuClickListener onItemMenuClickListener;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    ShowLessonActionsAdapter(Context context){
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener){
        this.onItemMenuClickListener = onItemMenuClickListener;
    }

    @Override
    public ItemViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        //获取item布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_lesson_action_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder myholder, final int position) {
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
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + info.getActionImage(), listener);

        myholder.groups.setText(info.getGroups() + "组");
        myholder.times.setText(info.getTimes() + "个");
        myholder.resetTime.setText(info.getResetTime() + "秒");
    }


    public class ItemViewHolder  extends RecyclerView.ViewHolder{

        public TextView actionName;
        public ImageView actionPic;
        public TextView groups;
        public TextView times;
        public TextView resetTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            actionName = (TextView) itemView.findViewById(R.id.name);
            actionPic = (ImageView) itemView.findViewById(R.id.video);
            groups = (TextView) itemView.findViewById(R.id.groups);
            times = (TextView) itemView.findViewById(R.id.times);
            resetTime = (TextView) itemView.findViewById(R.id.reset_time);

        }
    }
}
