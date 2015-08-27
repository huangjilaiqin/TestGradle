package com.lessask;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lessask.chat.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.DownImageAsync;
import com.lessask.model.User;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JHuang on 2015/8/18.
 */
public class FriendsAdapter extends BaseAdapter{

    private static final String TAG = FriendsAdapter.class.getName();

    private Context context;
    private ArrayList<User> originFriends;
    private File headImgDir;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();

    public FriendsAdapter(Context context, ArrayList data){
        this.context = context;
        originFriends = data;
        //to do 这里有时出现NullException
        headImgDir = context.getExternalFilesDir("headImg");
    }
    @Override
    public int getCount() {
        if(originFriends==null){
            return 0;
        }
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
        TextView tvName = (TextView)convertView.findViewById(R.id.name);
        TextView tvContent = (TextView)convertView.findViewById(R.id.content);
        TextView tvTime = (TextView)convertView.findViewById(R.id.time);
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
                new DownImageAsync("http://123.59.40.113/img/"+user.getUserid()+".jpg",ivHead).execute();
            }
        }else {
            ivHead.setImageBitmap(bmp);
        }

        return convertView;
    }
}
