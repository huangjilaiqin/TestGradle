package com.lessask.me;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;
import com.github.captain_miao.recyclerviewutils.listener.OnRecyclerItemClickListener;
import com.lessask.R;
import com.lessask.crud.AdapterAction;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.VolleyHelper;
import com.lessask.show.ShowImageActivity;
import com.lessask.show.ShowTime;
import com.lessask.util.TimeHelper;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by huangji on 2015/11/16.
 */
public class ShowTimeAdapter extends BaseLoadMoreRecyclerAdapter<ShowTime, ShowTimeAdapter.ViewHolder> implements OnRecyclerItemClickListener,AdapterAction<ShowTime> {
    private static final String TAG = ShowTimeAdapter.class.getName();

    PhotoViewAttacher mAttacher;

    private Context context;
    private FragmentActivity activity;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();

    private final LayoutInflater inflater;

    public ShowTimeAdapter(Context context){
        //数据直接传递给Base...Adapter
        //获取item通过getItem
        //appendToList(data);
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_item, parent, false);
        return new ShowTimeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final ViewHolder holder, int position) {
        final int myPosition = position;
        ShowTime showItem = getItem(myPosition);

        //头像
        String headImgUrl = imageUrlPrefix+showItem.getHeadimg();
        //Log.e(TAG, headImgUrl);
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(holder.ivHead ,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        //imageLoader.get(headImgUrl, headImgListener);
        //VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener);
        //VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener,holder.ivHead.getWidth(),holder.ivHead.getHeight());
        VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener,200,200);

        showItem.getUserid();
        holder.tvName.setText(showItem.getNickname());
        holder.tvTime.setText(TimeHelper.date2Show(TimeHelper.utcStr2Date(showItem.getTime())));
        holder.tvAddress.setText(showItem.getAddress());
        holder.tvContent.setText(showItem.getContent());
        int likeSize = 0;
        if(showItem.getLiker()!=null){
            likeSize = showItem.getLiker().size();
        }
        holder.tvUpSize.setText("" + likeSize);
        int commentSize = 0;
        if(showItem.getComments()!=null){
            commentSize = showItem.getComments().size();
        }
        holder.tvCommentSize.setText("" + commentSize);

        //点赞
        if(showItem.getLikeStatus()==1){
            holder.ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up_selected));
        }else {
            holder.ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up));
        }

        //评论

        //设置图片
        GridLayout imageLayout;
        ImageView showImage,showImage1,showImage2,showImage3,showImage4;
        ArrayList<String> pictures = showItem.getPictures();
        switch (pictures.size()){
            case 1:
                //加载图片布局xml文件, 获取布局对象
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.show_iamge_1, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)layout.findViewById(R.id.show_image1);
                registerImageEvent(showImage1, showItem, 0);

                String imgUrl1 = imageUrlPrefix+pictures.get(0);
                Log.e(TAG, "img1 w:"+showImage1.getWidth()+" h:"+showImage1.getHeight());
                ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(showImage1,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                //imageLoader.get(imgUrl1, listener1);
                //VolleyHelper.getInstance().getImageLoader().get(imgUrl1, listener1,showImage1.getWidth(),showImage1.getHeight());
                VolleyHelper.getInstance().getImageLoader().get(imgUrl1, listener1,200,200);

                holder.showImageLayout.removeAllViews();
                holder.showImageLayout.addView(layout);
                break;
            case 2:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (GridLayout) inflater.inflate(R.layout.show_iamge_2, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                ImageView[] imageViews2 = {showImage1,showImage2};

                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(i);
                    showImage = imageViews2[i];
                    Log.e(TAG, "img2 w:"+showImage.getWidth()+" h:"+showImage.getHeight());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(showImage,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    //imageLoader.get(imgUrl, listener);
                    //VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,showImage.getWidth(),showImage.getHeight());
                    VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,200,200);
                    registerImageEvent(imageViews2[i], showItem, i);
                }

                holder.showImageLayout.removeAllViews();
                holder.showImageLayout.addView(imageLayout);
                break;
            case 3:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (GridLayout) inflater.inflate(R.layout.show_iamge_3, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                showImage3 = (ImageView)imageLayout.findViewById(R.id.show_image3);
                ImageView[] imageViews3 = {showImage1,showImage2,showImage3};

                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(i);
                    showImage = imageViews3[i];
                    Log.e(TAG, "img3 w:"+showImage.getWidth()+" h:"+showImage.getHeight());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(showImage,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    //imageLoader.get(imgUrl, listener);
                    //VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,showImage.getWidth(),showImage.getHeight());
                    VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,200,200);
                    registerImageEvent(imageViews3[i], showItem, i);
                }

                holder.showImageLayout.removeAllViews();
                holder.showImageLayout.addView(imageLayout);
                break;
            case 4:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (GridLayout) inflater.inflate(R.layout.show_iamge_4, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                showImage3 = (ImageView)imageLayout.findViewById(R.id.show_image3);
                showImage4 = (ImageView)imageLayout.findViewById(R.id.show_image4);
                ImageView[] imageViews4 = {showImage1,showImage2,showImage3,showImage4};

                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(i);
                    showImage = imageViews4[i];
                    Log.e(TAG, "img4 w:"+showImage.getWidth()+" h:"+showImage.getHeight());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(showImage,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    //imageLoader.get(imgUrl, listener);
                    //VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,showImage.getWidth(),showImage.getHeight());
                    VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,200,200);
                    registerImageEvent(imageViews4[i], showItem, i);
                }

                holder.showImageLayout.removeAllViews();
                holder.showImageLayout.addView(imageLayout);
                break;
        }
    }

    @Override
    public void onClick(View view, int i) {

    }

    @Override
    public void update(int position, ShowTime obj) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivHead;
        TextView tvName;
        TextView tvTime;
        TextView tvAddress;
        TextView tvContent;
        TextView tvUpSize;
        ImageView ivUp;
        TextView tvCommentSize;
        ImageView ivComment;
        RelativeLayout showImageLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            ivHead = (ImageView)itemView.findViewById(R.id.head_img);
            tvName = (TextView)itemView.findViewById(R.id.name);
            tvTime = (TextView)itemView.findViewById(R.id.time);
            tvAddress = (TextView)itemView.findViewById(R.id.address);
            tvContent = (TextView)itemView.findViewById(R.id.content);
            tvUpSize = (TextView)itemView.findViewById(R.id.up_size);
            ivUp = (ImageView)itemView.findViewById(R.id.up);
            tvCommentSize = (TextView)itemView.findViewById(R.id.comment_size);
            ivComment = (ImageView)itemView.findViewById(R.id.comment);
            //图片容器布局
            showImageLayout = (RelativeLayout)itemView.findViewById(R.id.show_image_layout);
        }
    }

    private void registerImageEvent(ImageView image, final ShowTime item, final int index){
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "long click image", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                //Intent intent = new Intent(context, TmpActivity.class);
                intent.putExtra("index", index);
                intent.putStringArrayListExtra("images", item.getPictures());
                activity.startActivity(intent);
            }
        });
    }
}
