package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class ShowImageActivity extends FragmentActivity {
    private final String TAG = ShowImageActivity.class.getName();
    private View mRootView;
    private ViewPager mViewPager;
    private ArrayList<String> viewList;
    private List<String> titleList;
    private TitlePageIndicator mTitlePageIndicator;
    private LayoutInflater mInflate;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        setContentView(R.layout.activity_show_image);
        viewList = mIntent.getStringArrayListExtra("images");
        int index = mIntent.getIntExtra("index", 0);

        mInflate = getLayoutInflater().from(this);

        //mTitlePageIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.title);

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
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("images", viewList);
            bundle.putInt("position", position);
            fragmentImageShow.setArguments(bundle);
            Log.e(TAG, fragmentImageShow.toString());
            return fragmentImageShow;
        }


        /*
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setImageResource(mListViews.get(position));

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }
        */

        @Override
        public int getIconResId(int index) {
            return 0;
        }
        @Override
        public int getCount() {
            return viewList.size();
        }
    }

}
