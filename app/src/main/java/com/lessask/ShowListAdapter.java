package com.lessask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.lessask.chat.GlobalInfos;
import com.lessask.model.ShowItem;

import java.io.File;
import java.util.ArrayList;

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
    private final LayoutInflater inflater;

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
                showImage1.setImageResource(Integer.parseInt(showItem.getShowImgs().get(0)));
                registerImageEvent(showImage1, showItem, 0);

                showImageLayout.removeAllViews();
                showImageLayout.addView(imageLayout);
                break;
            case 2:
                //加载图片布局xml文件, 获取布局对象
                imageLayout = (RelativeLayout) inflater.inflate(R.layout.show_iamge_2, null).findViewById(R.id.root_layout);
                //设置图片
                showImage1 = (ImageView)imageLayout.findViewById(R.id.show_image1);
                showImage2 = (ImageView)imageLayout.findViewById(R.id.show_image2);
                showImage1.setImageResource(Integer.parseInt(showItem.getShowImgs().get(0)));
                registerImageEvent(showImage1, showItem, 0);
                showImage2.setImageResource(Integer.parseInt(showItem.getShowImgs().get(1)));
                registerImageEvent(showImage2, showItem, 1);
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
                showImage1.setImageResource(Integer.parseInt(showItem.getShowImgs().get(0)));
                registerImageEvent(showImage1, showItem, 0);
                showImage2.setImageResource(Integer.parseInt(showItem.getShowImgs().get(1)));
                registerImageEvent(showImage2, showItem, 1);
                showImage3.setImageResource(Integer.parseInt(showItem.getShowImgs().get(2)));
                registerImageEvent(showImage3, showItem, 2);
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
                showImage1.setImageResource(Integer.parseInt(showItem.getShowImgs().get(0)));
                registerImageEvent(showImage1, showItem, 0);
                showImage2.setImageResource(Integer.parseInt(showItem.getShowImgs().get(1)));
                registerImageEvent(showImage2, showItem, 1);
                showImage3.setImageResource(Integer.parseInt(showItem.getShowImgs().get(2)));
                registerImageEvent(showImage3, showItem, 2);
                showImage4.setImageResource(Integer.parseInt(showItem.getShowImgs().get(3)));
                registerImageEvent(showImage4, showItem, 3);
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

    private void setUp(ShowItem showItem,ImageView ivUp,TextView tvUpSize){
        if(showItem.getUpStatus()==1){
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up_selected));
        }else {
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up));
        }
        tvUpSize.setText(""+showItem.getUpSize());
    }

    private void changeUp(ShowItem showItem,ImageView ivUp, TextView tvUpSize){
        if(showItem.getUpStatus()==1){
            showItem.setUpStatus(0);
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up));
            int upSize = showItem.getUpSize()-1;
            showItem.setUpSize(upSize);
            tvUpSize.setText("" + upSize);
        }else {
            showItem.setUpStatus(1);
            ivUp.setImageDrawable(context.getResources().getDrawable(R.drawable.up_selected));
            int upSize = showItem.getUpSize()+1;
            showItem.setUpSize(upSize);
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
                intent.putExtra("index", index);
                intent.putStringArrayListExtra("images", item.getShowImgs());
                activity.startActivity(intent);
            }
        });
    }
}