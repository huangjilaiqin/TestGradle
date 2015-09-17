package com.lessask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lessask.chat.GlobalInfos;
import com.lessask.model.ShowItem;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JHuang on 2015/9/16.
 */
public class ShowListAdapter extends BaseAdapter {
    private static final String TAG = FriendsAdapter.class.getName();
    private RequestQueue requestQueue;
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
    private ImageLoader.ImageCache imageCache;
    private ImageLoader imageLoader;

    private Context context;
    private ArrayList<ShowItem> mShowListData;
    private File headImgDir;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private final LayoutInflater inflater;

    public ShowListAdapter(Context context, ArrayList data){
        this.context = context;
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
        ShowItem showItem = (ShowItem)getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.show_item, null);
        ImageView ivHead = (ImageView)convertView.findViewById(R.id.head_img);
        TextView tvName = (TextView)convertView.findViewById(R.id.name);
        TextView tvTime = (TextView)convertView.findViewById(R.id.time);
        TextView tvAddress = (TextView)convertView.findViewById(R.id.address);
        TextView tvContent = (TextView)convertView.findViewById(R.id.content);
        TextView tvUpSize = (TextView)convertView.findViewById(R.id.up_size);
        TextView tvCommentSize = (TextView)convertView.findViewById(R.id.comment_size);

        ivHead.setImageResource(R.drawable.head_default);
        tvName.setText(showItem.getName());
        tvTime.setText(showItem.getTime());
        tvAddress.setText(showItem.getAddress());
        tvContent.setText(showItem.getContent());
        tvUpSize.setText(""+showItem.getUpSize());
        tvCommentSize.setText(""+showItem.getCommentSize());
        //获取被动态填充的布局控件
        RelativeLayout showImageLayout = (RelativeLayout)convertView.findViewById(R.id.show_image_layout);

        RelativeLayout imageLayout;
        ImageView showImage1,showImage2,showImage3,showImage4;

        switch (showItem.getShowImgs().size()){
            case 1:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (RelativeLayout) inflater.inflate(R.layout.show_iamge_1, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage1.setImageResource(R.drawable.runnging);
                showImageLayout.removeAllViews();
                showImageLayout.addView(imageLayout);
                break;
            case 2:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (RelativeLayout) inflater.inflate(R.layout.show_iamge_2, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                showImage1.setImageResource(R.drawable.runnging);
                showImage2.setImageResource(R.drawable.runnging);
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
                showImage1.setImageResource(R.drawable.runnging);
                showImage2.setImageResource(R.drawable.runnging);
                showImage3.setImageResource(R.drawable.runnging);
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
                showImage1.setImageResource(R.drawable.runnging);
                showImage2.setImageResource(R.drawable.runnging);
                showImage3.setImageResource(R.drawable.runnging);
                showImage4.setImageResource(R.drawable.runnging);
                showImageLayout.removeAllViews();
                showImageLayout.addView(imageLayout);
                break;
        }

        /*
        ArrayList chatContent = globalInfos.getChatContent(user.getUserid());
        ChatMessage msg = null;
        if(chatContent.size()>0) {
            msg = (ChatMessage) chatContent.get(chatContent.size()-1);
        }
        tvName.setText(user.getNickname());
        //获取对话内容
        if(msg!=null) {
            tvContent.setText(msg.getContent());
            tvTime.setText(msg.getTime());
        }

        //先从内内存中找, 再从文件中找, 再服务器加载
        //Bitmap bmp = user.getHeadImg();
        Bitmap bmp = null;
        if(bmp == null){
            File imageFile = new File(headImgDir, user.getUserid()+".jpg");
            if(imageFile.exists()) {
                Uri headImgUri = Uri.fromFile(imageFile);//获取文件的Uri
                bmp = decodeUriAsBitmap(headImgUri);
                //user.setHeadImg(bmp);
                ivHead.setImageBitmap(bmp);
            }else {
                //设置默认图像
                ivHead.setImageResource(R.mipmap.ic_launcher);
                //异步加载图像
                String friendHeadImgUrl = globalInfos.getHeadImgHost()+user.getUserid()+".jpg";
                ImageLoader.ImageListener listener = ImageLoader.getImageListener(ivHead,R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                imageLoader.get(friendHeadImgUrl, listener);
            }
        }else {
            ivHead.setImageBitmap(bmp);
        }
        */

        return convertView;
    }
}
