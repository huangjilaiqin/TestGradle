package com.lessask.contacts;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
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
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;

/**
 * Created by huangji on 2016/2/19.
 */
public class ContactsAdapter extends BaseRecyclerAdapter<User,ContactsAdapter.ViewHolder>{
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private Context context;
    public ContactsAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        User user = getItem(position);
        holder.name.setText(user.getNickname());
        String headImgUrl = imageUrlPrefix+user.getHeadImg();
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(holder.headImg, 0, 0);
        VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
