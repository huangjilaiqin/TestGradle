package com.lessask;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lessask.model.User;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JHuang on 2015/8/18.
 */
public class FriendsAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<User> originFriends;
    private File headImgDir;
    public FriendsAdapter(Context context, ArrayList data){
        this.context = context;
        originFriends = data;
        headImgDir = context.getExternalFilesDir("headImg");
    }
    @Override
    public int getCount() {
        return originFriends.size();
    }

    @Override
    public Object getItem(int position) {
        return originFriends.get(position);
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
        User user = (User)getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null);
        ImageView ivHead = (ImageView)convertView.findViewById(R.id.head_img);

        //先从内内存中找, 再从文件中找, 再服务器加载
        Bitmap bmp = user.getHeadImg();
        if(bmp == null){
            File imageFile = new File(headImgDir, user.getUserid()+".jpg");
            if(imageFile.exists()) {
                Uri headImgUri = Uri.fromFile(imageFile);//获取文件的Uri
                bmp = decodeUriAsBitmap(headImgUri);
                user.setHeadImg(bmp);
                ivHead.setImageBitmap(bmp);
            }else {
                //设置默认图像
                ivHead.setImageResource(R.mipmap.ic_launcher);
                //异步加载图像
            }
        }else {
            ivHead.setImageBitmap(bmp);
        }

        TextView tvName = (TextView)convertView.findViewById(R.id.name);
        tvName.setText(user.getMail());
        return convertView;
    }
}
