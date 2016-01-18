package com.lessask.show;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lessask.R;
import com.lessask.util.ImageUtil;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/*
* 展示创建动态时选中的图片
* */
public class ShowSelectedImageActivity extends AppCompatActivity{
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
        mCurrentPosition = mIntent.getIntExtra("index", 0);
        setResult(RESULT_OK, mIntent);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mToolbar.setTitle(mCurrentPosition + 1 + "/" + photos.size());

        Log.e(TAG, "onCreate index:"+mCurrentPosition +", photos:"+photos);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(this,photos);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mViewPager.setCurrentItem(0); //设置默认当前页

        indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setCurrentItem(mCurrentPosition);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //mCurrentPosition = position;
                //mToolbar.setTitle(mCurrentPosition + 1 + "/" + photos.size());
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                mToolbar.setTitle(mCurrentPosition + 1 + "/" + photos.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDelete = (ImageView) findViewById(R.id.delete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photos.remove(mCurrentPosition);
                if(photos.size()==mCurrentPosition){
                    mCurrentPosition--;
                }
                if(mCurrentPosition==-1){
                    finish();
                }
                mViewPager.setAdapter(myFragmentPagerAdapter);
                myFragmentPagerAdapter.notifyDataSetChanged();
                //数据改变后一定要先notifyDataSetChanged
                mViewPager.setCurrentItem(mCurrentPosition); //设置默认当前页
                mToolbar.setTitle(mCurrentPosition+1+"/"+photos.size());
                indicator.notifyDataSetChanged();
            }
        });
    }

    class MyFragmentPagerAdapter extends PagerAdapter implements IconPagerAdapter {
        private Context context;
        private ArrayList<String> photos;
        public MyFragmentPagerAdapter(Context context,ArrayList<String> photos) {
            this.context = context;
            this.photos=photos;
        }

        //IconPagerAdapter 接口
        @Override
        public int getIconResId(int index) {
            return 0;
        }


        @Override
        public int getCount() {
            Log.e(TAG, "photos size:"+photos.size());
            return photos.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(ShowSelectedImageActivity.this);
            imageView.setImageBitmap(ImageUtil.getOptimizeBitmapFromFile(photos.get(position)));
            PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
            mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    ShowSelectedImageActivity.this.finish();
                }
            });
            mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float v, float v1) {
                    ShowSelectedImageActivity.this.finish();
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
}
