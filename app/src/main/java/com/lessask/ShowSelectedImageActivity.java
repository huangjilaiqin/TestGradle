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
import android.widget.ImageView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

public class ShowSelectedImageActivity extends FragmentActivity {
    private final String TAG = ShowSelectedImageActivity.class.getName();
    private ViewPager mViewPager;
    private ArrayList<String> photos;
    private ImageView mDelete;
    private Intent mIntent;
    private int mCurrentPosition;
    private MyPagerAdapter adapter;
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

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0); //设置默认当前页

        final CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setCurrentItem(mCurrentPosition);

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
                Log.e(TAG, "current:"+mCurrentPosition);
                adapter.notifyDataSetChanged();
                indicator.notifyDataSetChanged();

            }
        });
    }
    class MyPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCurrentPosition = position;
            FragmentImageShow fragmentImageShow = new FragmentImageShow();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("images", photos);
            bundle.putInt("position", position);
            fragmentImageShow.setArguments(bundle);
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
