package com.lessask;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lessask.model.User;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/8/18.
 */
public class FriendsAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<User> originFriends;
    public FriendsAdapter(Context context, ArrayList data){
        this.context = context;
        originFriends = data;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null);
        ImageView ivHead = (ImageView)convertView.findViewById(R.id.head_img);
        /*
        File appDir = getApplicationContext().getExternalFilesDir("headImg");
        File imageFile = new File(appDir, "myheadImg.jpg");
        headImgUri = Uri.fromFile(imageFile);//获取文件的Uri
        bmp = decodeUriAsBitmap(headImgUri, null);
        ivHeadImg.setImageBitmap(bmp);
        */
        ivHead.setImageResource(R.mipmap.ic_launcher);
        TextView tvName = (TextView)convertView.findViewById(R.id.name);
        User user = (User)getItem(position);
        tvName.setText(user.getMail());
        return convertView;
    }
}
