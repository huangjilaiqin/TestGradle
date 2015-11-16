package com.lessask.show;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ShowItem;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by huangji on 2015/11/16.
 */
public class ShowListAdapter extends RecyclerView.Adapter<ShowListAdapter.ViewHolder>{
    private static final String TAG = ShowListAdapter.class.getName();
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
            Log.e(TAG, "like done:" + response.getBody());
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mShowListData.size();
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
        public ViewHolder(View itemView) {
            super(itemView);
            ImageView ivHead = (ImageView)convertView.findViewById(R.id.head_img);
            TextView tvName = (TextView)convertView.findViewById(R.id.name);
            TextView tvTime = (TextView)convertView.findViewById(R.id.time);
            TextView tvAddress = (TextView)convertView.findViewById(R.id.address);
            TextView tvContent = (TextView)convertView.findViewById(R.id.content);
            final TextView tvUpSize = (TextView)convertView.findViewById(R.id.up_size);
            final ImageView ivUp = (ImageView)convertView.findViewById(R.id.up);
        }
    }
}
