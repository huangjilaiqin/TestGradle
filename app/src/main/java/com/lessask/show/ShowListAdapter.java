package com.lessask.show;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.captain_miao.recyclerviewutils.listener.OnRecyclerItemClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.LikeResponse;
import com.lessask.model.UnlikeResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.util.ImageUtil;
import com.lessask.util.ScreenUtil;
import com.lessask.util.TimeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;

/**
 * Created by huangji on 2015/11/16.
 */
public class ShowListAdapter extends BaseLoadMoreRecyclerAdapter<ShowTime, ShowListAdapter.ViewHolder> implements OnRecyclerItemClickListener {
    private static final String TAG = ShowListAdapter.class.getName();

    PhotoViewAttacher mAttacher;

    private Context context;
    private FragmentActivity activity;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();

    private final LayoutInflater inflater;

    public ShowListAdapter(FragmentActivity activity){
        //数据直接传递给Base...Adapter
        //获取item通过getItem
        //appendToList(data);
        this.activity = activity;
        this.context = activity.getApplicationContext();
        inflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_item, parent, false);
        return new ShowListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final ViewHolder holder, int position) {
        final int myPosition = position;
        ShowTime showTime = getItem(myPosition);

        //头像
        String headImgUrl = imageUrlPrefix+showTime.getHeadimg();
        //Log.e(TAG, headImgUrl);
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(holder.ivHead,0,0);
        VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);

        showTime.getUserId();
        holder.tvName.setText(showTime.getNickname());
        Log.e(TAG, "showTime:" + showTime.getTime());
        holder.tvTime.setText(TimeHelper.date2Show(TimeHelper.utcStr2Date(showTime.getTime())));
        holder.tvAddress.setText(showTime.getAddress());
        if(showTime.getContent().length()==0){
            holder.tvContent.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }else {
            holder.tvContent.setText(showTime.getContent());
        }
        int likeSize = 0;
        if(showTime.getLiker()!=null){
            likeSize = showTime.getLiker().size();
        }
        holder.tvUpSize.setText("" + likeSize);
        int commentSize = 0;
        if(showTime.getComments()!=null){
            commentSize = showTime.getComments().size();
        }
        holder.tvCommentSize.setText("" + commentSize);

        //点赞
        if(showTime.getLikeStatus()==1){
            holder.ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up_selected));
        }else {
            holder.ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up));
        }
        holder.ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUp(v, myPosition);
            }
        });

        //评论

        //设置图片
        ImageView showImage,showImage1,showImage2,showImage3,showImage4;
        ArrayList<String> pictures = showTime.getPictures();
        ArrayList<ArrayList<Integer>> picsSize = showTime.getPicsSize();
        ArrayList<Integer> picsColor = showTime.getPicsColor();
        if(picsColor==null){
            picsColor=new ArrayList<>();
            for(int i=0;i<pictures.size();i++)
                picsColor.add(0);
        }
        //多个图片间隔
        int imageDeltaDp = 4;
        int imageDelta = ScreenUtil.dp2Px(context,imageDeltaDp);
        //多个图片的显示宽度
        int imageSize = ScreenUtil.getMultiImgWidth(context, imageDeltaDp);
        showTime.setThumbnailWidth(imageSize);
        showTime.setThumbnailHeight(imageSize);

        //单个图片的显示宽度
        int singleImgMaxSize = ScreenUtil.getSingleImgWidth(context);

        RelativeLayout.LayoutParams lp1,lp2,lp3,lp4;
        switch (pictures.size()){
            case 1:
                showImage1 = new ImageView(context);
                showImage1.setId(R.id.show_image1);
                showImage1.setAdjustViewBounds(true);
                showImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                showImage1.setBackgroundColor(picsColor.get(0));
                registerImageEvent(showImage1, showTime, 0);

                ArrayList<Integer> wh = picsSize.get(0);
                Log.e(TAG, "w:" + wh.get(0) + ", h:" + wh.get(1));
                int w = wh.get(0);
                int h = wh.get(1);
                ArrayList<Integer> fitSize = ImageUtil.getRecAFitB(w, h, singleImgMaxSize, singleImgMaxSize);
                w=fitSize.get(0);
                h=fitSize.get(1);
                Log.e(TAG, "fit w:" + w + ", h:" + h);
                lp1 = new RelativeLayout.LayoutParams(w,h);
                showTime.setThumbnailWidth(w);
                showTime.setThumbnailHeight(h);

                String imgUrl1 = imageUrlPrefix+pictures.get(0)+"!"+w+"_"+h;
                ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(showImage1,0,0);
                VolleyHelper.getInstance().getImageLoader().get(imgUrl1, listener1,w,h);

                holder.showImageLayout.removeAllViews();
                holder.showImageLayout.addView(showImage1,lp1);
                break;
            case 2:
                showImage1 = new ImageView(context);
                showImage1.setId(R.id.show_image1);
                showImage1.setAdjustViewBounds(true);
                showImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp1 = new RelativeLayout.LayoutParams(imageSize,imageSize);

                showImage2 = new ImageView(context);
                showImage2.setId(R.id.show_image2);
                showImage2.setAdjustViewBounds(true);
                showImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp2 = new RelativeLayout.LayoutParams(imageSize,imageSize);
                lp2.setMargins(imageDelta,0,0,0);
                lp2.addRule(RelativeLayout.RIGHT_OF, R.id.show_image1);
                ImageView[] imageViews2 = {showImage1,showImage2};
                RelativeLayout.LayoutParams[] lps = {lp1,lp2};

                holder.showImageLayout.removeAllViews();
                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(i)+"!"+imageSize+"_"+imageSize;
                    showImage = imageViews2[i];
                    showImage.setBackgroundColor(picsColor.get(i));
                    Log.e(TAG, "img2 w:"+showImage.getWidth()+" h:"+showImage.getHeight());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(showImage,0,0);
                    VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,imageSize,imageSize);
                    registerImageEvent(showImage, showTime, i);

                    holder.showImageLayout.addView(showImage,lps[i]);
                }

                break;
            case 3:
                showImage1 = new ImageView(context);
                showImage1.setId(R.id.show_image1);
                showImage1.setAdjustViewBounds(true);
                showImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp1 = new RelativeLayout.LayoutParams(imageSize,imageSize);

                showImage2 = new ImageView(context);
                showImage2.setId(R.id.show_image2);
                showImage2.setAdjustViewBounds(true);
                showImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp2 = new RelativeLayout.LayoutParams(imageSize,imageSize);
                lp2.setMargins(imageDelta,0,0,0);
                lp2.addRule(RelativeLayout.RIGHT_OF, R.id.show_image1);

                showImage3 = new ImageView(context);
                showImage3.setId(R.id.show_image3);
                showImage3.setAdjustViewBounds(true);
                showImage3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp3 = new RelativeLayout.LayoutParams(imageSize,imageSize);
                lp3.setMargins(0,imageDelta,0,0);
                lp3.addRule(RelativeLayout.BELOW, R.id.show_image1);


                ImageView[] imageViews3 = {showImage1,showImage2,showImage3};
                RelativeLayout.LayoutParams[] lps3 = {lp1,lp2,lp3};

                holder.showImageLayout.removeAllViews();
                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(i)+"!"+imageSize+"_"+imageSize;
                    showImage = imageViews3[i];
                    //showImage.setLayoutParams(sizeParams);
                    showImage.setBackgroundColor(picsColor.get(i));
                    Log.e(TAG, "img3 w:"+showImage.getWidth()+" h:"+showImage.getHeight());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(showImage,0,0);
                    VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,imageSize,imageSize);
                    registerImageEvent(showImage, showTime, i);
                    holder.showImageLayout.addView(showImage,lps3[i]);
                }

                break;
            case 4:
                showImage1 = new ImageView(context);
                showImage1.setId(R.id.show_image1);
                showImage1.setAdjustViewBounds(true);
                showImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp1 = new RelativeLayout.LayoutParams(imageSize,imageSize);

                showImage2 = new ImageView(context);
                showImage2.setId(R.id.show_image2);
                showImage2.setAdjustViewBounds(true);
                showImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp2 = new RelativeLayout.LayoutParams(imageSize,imageSize);
                lp2.setMargins(imageDelta,0,0,0);
                lp2.addRule(RelativeLayout.RIGHT_OF, R.id.show_image1);

                showImage3 = new ImageView(context);
                showImage3.setId(R.id.show_image3);
                showImage3.setAdjustViewBounds(true);
                showImage3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp3 = new RelativeLayout.LayoutParams(imageSize,imageSize);
                lp3.setMargins(0,imageDelta,0,0);
                lp3.addRule(RelativeLayout.BELOW, R.id.show_image1);

                showImage4 = new ImageView(context);
                showImage4.setId(R.id.show_image4);
                showImage4.setAdjustViewBounds(true);
                showImage4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp4 = new RelativeLayout.LayoutParams(imageSize,imageSize);
                lp4.setMargins(imageDelta,imageDelta,0,0);
                lp4.addRule(RelativeLayout.BELOW, R.id.show_image2);
                lp4.addRule(RelativeLayout.RIGHT_OF,R.id.show_image3);


                ImageView[] imageViews4 = {showImage1,showImage2,showImage3,showImage4};
                RelativeLayout.LayoutParams[] lps4 = {lp1,lp2,lp3,lp4};

                holder.showImageLayout.removeAllViews();
                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(i)+"!"+imageSize+"_"+imageSize;
                    showImage = imageViews4[i];
                    //showImage.setLayoutParams(sizeParams);
                    showImage.setBackgroundColor(picsColor.get(i));
                    Log.e(TAG, "img4 w:"+showImage.getWidth()+" h:"+showImage.getHeight());
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(showImage,0,0);
                    VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,imageSize,imageSize);
                    registerImageEvent(showImage, showTime, i);
                    holder.showImageLayout.addView(showImage,lps4[i]);
                }

                break;
        }
    }

    @Override
    public void onClick(View view, int i) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView ivHead;
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
            ivHead = (CircleImageView)itemView.findViewById(R.id.head_img);
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
    private void changeUp(final View view, final int position){

        final ShowTime showTime = getItem(position);
        final ImageView likeView = (ImageView)view;
        if(showTime.getLikeStatus()==1){
            GsonRequest unlikeRequest = new GsonRequest<>(Request.Method.POST, config.getUnlikeUrl(), UnlikeResponse.class, new GsonRequest.PostGsonRequest<UnlikeResponse>() {
                @Override
                public void onStart() {
                    Toast.makeText(activity, "unlike", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(UnlikeResponse response) {
                    if(response.getError()==null && response.getErrno()==0){
                        Toast.makeText(activity, "unlike success", Toast.LENGTH_SHORT).show();
                        if(showTime.getId()==response.getShowid()){
                            Log.e(TAG, "unlike response");
                            showTime.unlike(globalInfos.getUserId());
                        }else {
                            //遍历查找showid
                        }
                        likeView.setImageDrawable(context.getResources().getDrawable(R.drawable.up));
                        notifyDataSetChanged();
                    }else {
                        Toast.makeText(activity, "unlike fail"+response.getError()+", "+response.getErrno(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, error.getMessage());
                }

                @Override
                public void setPostData(Map datas) {
                    datas.put("userid", "" + globalInfos.getUserId());
                    datas.put("showid", "" + showTime.getId());
                    datas.put("position", "" + position);
                }

                @Override
                public Map getPostData() {
                    Map datas = new HashMap();
                    datas.put("userid", globalInfos.getUserId() + "");
                    datas.put("showid", "" + showTime.getId());
                    datas.put("position", "" + position);
                    return datas;
                }
            });
            VolleyHelper.getInstance().addToRequestQueue(unlikeRequest);
        }else {
            GsonRequest likeRequest = new GsonRequest<>(Request.Method.POST, config.getLikeUrl(), LikeResponse.class, new GsonRequest.PostGsonRequest<LikeResponse>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onResponse(LikeResponse response) {
                    if(response.getError()==null && response.getErrno()==0) {
                        Toast.makeText(activity, "like success", Toast.LENGTH_SHORT).show();
                        if (showTime.getId() == response.getShowid()) {
                            showTime.like(globalInfos.getUserId());
                        } else {
                            //遍历查找showid
                        }
                        likeView.setImageDrawable(context.getResources().getDrawable(R.drawable.up_selected));
                        notifyDataSetChanged();
                    }else {
                        Toast.makeText(activity, "like fail"+response.getError()+", "+response.getErrno(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, "like "+error.getMessage());
                }

                @Override
                public void setPostData(Map datas) {
                    datas.put("userid", "" + globalInfos.getUserId());
                    datas.put("showid", "" + showTime.getId());
                    datas.put("position", "" + position);

                }

                @Override
                public Map getPostData() {
                    Map datas = new HashMap();
                    datas.put("userid", "" + globalInfos.getUserId());
                    datas.put("showid", "" + showTime.getId());
                    datas.put("position", "" + position);
                    return datas;
                }
            });
            VolleyHelper.getInstance().addToRequestQueue(likeRequest);
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
                intent.putExtra("index", index);
                intent.putStringArrayListExtra("images", item.getPictures());
                ArrayList<ArrayList<Integer>> picsSize = item.getPicsSize();
                ArrayList<Integer> newPicsSize = new ArrayList<>();
                for(int i=0;i<picsSize.size();i++){
                    newPicsSize.add(picsSize.get(i).get(0));
                    newPicsSize.add(picsSize.get(i).get(1));
                }
                intent.putIntegerArrayListExtra("picsSize", newPicsSize);
                intent.putIntegerArrayListExtra("picsColor", item.getPicsColor());
                intent.putExtra("thumbnailWidth", item.getThumbnailWidth());
                intent.putExtra("thumbnailHeight", item.getThumbnailHeight());
                activity.startActivity(intent);
            }
        });
    }
}
