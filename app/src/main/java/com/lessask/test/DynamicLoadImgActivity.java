package com.lessask.test;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lessask.R;
import com.lessask.util.ScreenUtil;

public class DynamicLoadImgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_load_img);

        LinearLayout imgs = (LinearLayout)findViewById(R.id.imgs);
        imgs.setOrientation(LinearLayout.VERTICAL);
        ImageView imageView = new ImageView(this);
        imageView.setVisibility(View.VISIBLE);
        imageView.setBackgroundColor(getResources().getColor(R.color.main_color));
        //设置图片宽高
        int maxWidth = (int)(ScreenUtil.getScreenWidth(this)*0.555);
        ViewGroup.LayoutParams params = new ActionBar.LayoutParams(maxWidth,maxWidth);
        imgs.addView(imageView,params);

        TextView textView = (TextView)findViewById(R.id.content);
        //内容为空时设置高度为0
        //textView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }
}
