package com.lessask.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.User;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;

/**
 * Created by huangji on 2016/2/19.
 */
public class MessageAdapter extends BaseRecyclerAdapter<User,MessageAdapter.ViewHolder>{
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    public MessageAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = getItem(position);
        holder.name.setText(user.getNickname());
        String headImgUrl = imageUrlPrefix+user.getHeadImg();
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(holder.headImg, 0, 0);
        VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView headImg;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            headImg = (ImageView) itemView.findViewById(R.id.head_img);
            name =(TextView)itemView.findViewById(R.id.name);
        }
    }
}
