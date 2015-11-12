package com.lessask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;


import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

/*
* 展示show中的图片
* */

public class ShowImageActivity extends FragmentActivity {
    private final String TAG = ShowImageActivity.class.getName();
    private ViewPager mViewPager;
    private ArrayList<String> photos;
    private Intent mIntent;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        setContentView(R.layout.activity_show_image);
        photos = mIntent.getStringArrayListExtra("images");
        int index = mIntent.getIntExtra("index", 0);


        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0); //设置默认当前页

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setCurrentItem(index);


    }
    class MyPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            FragmentImageShow fragmentImageShow = new FragmentImageShow();
            fragmentImageShow.setPosition(position);
            fragmentImageShow.setmImage(imageUrlPrefix + photos.get(position));
            Log.e(TAG, fragmentImageShow.toString());
            return fragmentImageShow;
        }

        @Override
        public int getIconResId(int index) {
            return 0;
        }
        @Override
        public int getCount() {
            return photos.size();
        }
    }

}
