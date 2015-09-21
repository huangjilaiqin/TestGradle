package com.lessask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

public class ShowSelectedImageActivity extends FragmentActivity {
    private final String TAG = ShowSelectedImageActivity.class.getName();
    private ViewPager mViewPager;
    private CirclePageIndicator indicator;
    private ArrayList<String> photos;
    private ImageView mDelete;
    private Intent mIntent;
    private int mCurrentPosition;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private static final int REQUEST_DELETE_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        setContentView(R.layout.activity_show_selected_image);
        photos = mIntent.getStringArrayListExtra("images");
        //这个数据是对的,但是删除错位
        mCurrentPosition = mIntent.getIntExtra("index", 0);
        setResult(RESULT_OK, mIntent);

        Log.e(TAG, "onCreate index:"+mCurrentPosition +", photos:"+photos);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mViewPager.setCurrentItem(0); //设置默认当前页

        indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setCurrentItem(mCurrentPosition);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e(TAG, "onPageScrolled:"+position);
                mCurrentPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDelete = (ImageView) findViewById(R.id.delete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, ""+photos.get(mCurrentPosition));
                photos.remove(mCurrentPosition);
                Log.e(TAG, "delete:"+mCurrentPosition);
                if(photos.size()==mCurrentPosition){
                    mCurrentPosition--;
                }
                if(mCurrentPosition==-1){
                    finish();
                }
                Log.e(TAG, "current:" + mCurrentPosition);
                //mViewPager.setAdapter(adapter);
                mViewPager.setCurrentItem(mCurrentPosition); //设置默认当前页
                myFragmentPagerAdapter.notifyDataSetChanged();
                //FragmentImageShow fragmentImageShow = (FragmentImageShow)adapter.getItem(mCurrentPosition);
                //fragmentImageShow.update(photos.get(mCurrentPosition));
                //fragmentImageShow.update(R.drawable.runnging+"");
                indicator.notifyDataSetChanged();

            }
        });
    }
    class MyFragmentPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //mCurrentPosition = position;
            FragmentImageShow fragmentImageShow = new FragmentImageShow();
            Log.e(TAG, "getItem:"+fragmentImageShow.toString());
            return fragmentImageShow;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FragmentImageShow fragmentImageShow = (FragmentImageShow)super.instantiateItem(container, position);
            Log.e(TAG, "instantiateItem:"+fragmentImageShow.toString());
            Log.e(TAG, "instantiateItem:"+photos.get(position));
            Log.e(TAG, "instantiateItem:"+photos);
            /*
            Bundle bundle = new Bundle();
            bundle.putString("image", photos.get(position));
            bundle.putInt("position", position);
            fragmentImageShow.setArguments(bundle);
            Log.e(TAG, fragmentImageShow.toString());
            */
            fragmentImageShow.setPosition(position);
            fragmentImageShow.setmImage(photos.get(position));
            return fragmentImageShow;
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        //IconPagerAdapter 接口
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
