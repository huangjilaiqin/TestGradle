package com.lessask.show;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lessask.friends.FriendsAdapter;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ShowItem;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;
import com.lessask.util.TimeHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by JHuang on 2015/9/16.
 */
public class ShowListAdapter extends BaseAdapter {
    private static final String TAG = FriendsAdapter.class.getName();
    private RequestQueue requestQueue;
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
    private ImageLoader.ImageCache imageCache;
    private ImageLoader imageLoader;

    PhotoViewAttacher mAttacher;

    private Context context;
    private FragmentActivity activity;
    private ArrayList<ShowItem> mShowListData;
    private File headImgDir;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private final int HANDLER_LIKE_START = 0;
    private final int HANDLER_LIKE_DONE = 1;
    private final int HANDLER_UNLIKE_START = 2;
    private final int HANDLER_UNLIKE_DONE = 3;

    private final LayoutInflater inflater;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_LIKE_START:
                    //Toast.makeText(activity, "like", Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_LIKE_DONE:
                    //Toast.makeText(activity, "like success", Toast.LENGTH_SHORT).show();

                    break;
                case HANDLER_UNLIKE_START:
                    //Toast.makeText(activity, "unlike", Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_UNLIKE_DONE:
                    //Toast.makeText(activity, "unlike success", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private PostSingleEvent likePostSingleEvent = new PostSingleEvent() {
        @Override
        public void onStart() {
            Message msg = new Message();
            msg.what = HANDLER_LIKE_START;
            handler.sendMessage(msg);
        }

        @Override
        public void onDone(boolean success, PostResponse response) {
            Message msg = new Message();
            msg.what = HANDLER_LIKE_DONE;
            msg.arg1 = response.getCode();
            //json
            Log.e(TAG, "like done:"+response.getBody());
            handler.sendMessage(msg);
        }
    };
    private PostSingleEvent unlikePostSingleEvent = new PostSingleEvent() {
        @Override
        public void onStart() {
            Message msg = new Message();
            msg.what = HANDLER_UNLIKE_START;
            handler.sendMessage(msg);
        }

        @Override
        public void onDone(boolean success, PostResponse response) {
            Message msg = new Message();
            msg.what = HANDLER_UNLIKE_DONE;
            msg.arg1 = response.getCode();
            Log.e(TAG, "unlike done:"+response.getBody());
            //json
            handler.sendMessage(msg);
        }
    };
    private PostSingle postSingle;

    public ShowListAdapter(FragmentActivity activity, ArrayList data){
        this.activity = activity;
        this.context = activity.getApplicationContext();
        inflater = LayoutInflater.from(context);

        mShowListData = data;
        //to do 这里有时出现NullException
        headImgDir = context.getExternalFilesDir("headImg");
        requestQueue = Volley.newRequestQueue(context);
        imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap bitmap) {
                lruCache.put(key, bitmap);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        imageLoader = new ImageLoader(requestQueue, imageCache);
    }
    @Override
    public int getCount() {
        if(mShowListData==null){
            return 0;
        }
        return mShowListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mShowListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private Bitmap decodeUriAsBitmap(Uri uri){
      Bitmap bitmap = null;
      try {
          //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
          bitmap = BitmapFactory.decodeFile(uri.getPath());
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
      return bitmap;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ShowItem showItem = (ShowItem)getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.show_item, null);
        ImageView ivHead = (ImageView)convertView.findViewById(R.id.head_img);
        TextView tvName = (TextView)convertView.findViewById(R.id.name);
        TextView tvTime = (TextView)convertView.findViewById(R.id.time);
        TextView tvAddress = (TextView)convertView.findViewById(R.id.address);
        TextView tvContent = (TextView)convertView.findViewById(R.id.content);
        final TextView tvUpSize = (TextView)convertView.findViewById(R.id.up_size);
        final ImageView ivUp = (ImageView)convertView.findViewById(R.id.up);
        setUp(showItem, ivUp, tvUpSize);
        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUp(showItem, (ImageView) v, (TextView) tvUpSize);
                Log.e(TAG, "click up");
            }
        });
        TextView tvCommentSize = (TextView)convertView.findViewById(R.id.comment_size);
        ImageView ivComment = (ImageView)convertView.findViewById(R.id.comment);

        String headImgUrl = imageUrlPrefix+showItem.getHeadimg();
        Log.e(TAG, headImgUrl);
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(ivHead ,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        imageLoader.get(headImgUrl, headImgListener);

        showItem.getUserid();
        tvName.setText(showItem.getNickname());
        tvTime.setText(TimeHelper.date2Show(TimeHelper.utcStr2Date(showItem.getTime())));
        tvAddress.setText(showItem.getAddress());
        tvContent.setText(showItem.getContent());
        tvUpSize.setText(""+showItem.getLiker().size());
        tvCommentSize.setText(""+showItem.getComments().size());
        //获取被动态填充的布局控件
        RelativeLayout showImageLayout = (RelativeLayout)convertView.findViewById(R.id.show_image_layout);

        RelativeLayout imageLayout;
        ImageView showImage1,showImage2,showImage3,showImage4;
        ArrayList<String> pictures = showItem.getPictures();

        switch (pictures.size()){
            case 1:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (RelativeLayout) inflater.inflate(R.layout.show_iamge_1, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                registerImageEvent(showImage1, showItem, 0);

                String imgUrl1 = imageUrlPrefix+pictures.get(0);
                ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(showImage1,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                imageLoader.get(imgUrl1, listener1);

                showImageLayout.removeAllViews();
                showImageLayout.addView(imageLayout);
                break;
            case 2:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (RelativeLayout) inflater.inflate(R.layout.show_iamge_2, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                ImageView[] imageViews2 = {showImage1,showImage2};

                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(0);
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageViews2[i],R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    imageLoader.get(imgUrl, listener);
                    registerImageEvent(imageViews2[i], showItem, 0);
                }

                showImageLayout.removeAllViews();
                showImageLayout.addView(imageLayout);
                break;
            case 3:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (RelativeLayout) inflater.inflate(R.layout.show_iamge_3, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                showImage3 = (ImageView)imageLayout.findViewById(R.id.show_image3);
                ImageView[] imageViews3 = {showImage1,showImage2,showImage3};

                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(0);
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageViews3[i],R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    imageLoader.get(imgUrl, listener);
                    registerImageEvent(imageViews3[i], showItem, 0);
                }

                showImageLayout.removeAllViews();
                showImageLayout.addView(imageLayout);
                break;
            case 4:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (RelativeLayout) inflater.inflate(R.layout.show_iamge_4, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                showImage3 = (ImageView)imageLayout.findViewById(R.id.show_image3);
                showImage4 = (ImageView)imageLayout.findViewById(R.id.show_image4);
                ImageView[] imageViews4 = {showImage1,showImage2,showImage3,showImage4};

                for(int i=0;i<pictures.size();i++){
                    String imgUrl = imageUrlPrefix+pictures.get(0);
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageViews4[i],R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    imageLoader.get(imgUrl, listener);
                    registerImageEvent(imageViews4[i], showItem, 0);
                }

                showImageLayout.removeAllViews();
                showImageLayout.addView(imageLayout);
                break;
        }

        return convertView;
    }

    private void setUp(ShowItem showItem,ImageView ivUp,TextView tvUpSize){
        if(showItem.getLikeStatus()==1){
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up_selected));
        }else {
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up));
        }
        tvUpSize.setText(""+showItem.getLiker().size());
    }

    private void changeUp(ShowItem showItem,ImageView ivUp, TextView tvUpSize){
        if(showItem.getLikeStatus()==1){
            //获取数据状态数据
            postSingle = new PostSingle(config.getUnlikeUrl(), unlikePostSingleEvent);
            HashMap<String, String> requestArgs = new HashMap<>();
            requestArgs.put("userid", ""+globalInfos.getUserid());
            requestArgs.put("showid", ""+showItem.getId());
            postSingle.setHeaders(requestArgs);
            postSingle.start();

            showItem.setLikeStatus(0);
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up));
            showItem.unlike(globalInfos.getUserid());
            int upSize = showItem.getLiker().size();
            tvUpSize.setText("" + upSize);
        }else {
            postSingle = new PostSingle(config.getLikeUrl(), likePostSingleEvent);
            HashMap<String, String> requestArgs = new HashMap<>();
            requestArgs.put("userid", ""+globalInfos.getUserid());
            requestArgs.put("showid", ""+showItem.getId());
            postSingle.setHeaders(requestArgs);
            postSingle.start();

            showItem.setLikeStatus(1);
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up_selected));
            showItem.like(globalInfos.getUserid());
            int upSize = showItem.getLiker().size();
            tvUpSize.setText("" + upSize);
        }
    }
    private void registerImageEvent(ImageView image, final ShowItem item, final int index){
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
