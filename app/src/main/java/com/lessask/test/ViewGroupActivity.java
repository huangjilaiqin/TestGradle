package com.lessask.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lessask.R;
import com.lessask.util.ScreenUtil;

public class ViewGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = new ViewGroup(this) {
            @Override
            protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

            }
        };
        RelativeLayout relativeLayout = new RelativeLayout(this);
        ImageView imageView = new ImageView(this);
        //imageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.getScreenWidth(this),ScreenUtil.getScreenHeight(this)/2));
        imageView.setBackgroundColor(getResources().getColor(R.color.main_color));
        //viewGroup.addView(imageView);
        //setContentView(R.layout.activity_view_group);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this)/2);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.addView(imageView,lp);
        setContentView(relativeLayout);
    }
}
