package com.lessask.show;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.fixed.ViewPagerFixed;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.VolleyHelper;
import com.lessask.util.ImageUtil;
import com.lessask.util.ScreenUtil;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/*
* 查看动态中的图片
* */

public class ShowImageActivity extends FragmentActivity {
    private final String TAG = ShowImageActivity.class.getName();
    //private ViewPager mViewPager;
    private ViewPagerFixed mViewPager;
    private ArrayList<String> photos;
    private ArrayList<Integer> picsSize;
    private ArrayList<Integer> picsColor;
    private Intent mIntent;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private int screenWidth;
    private int screenHeight;
    private int mCurrentPosition;
    private int thumbnailWidth;
    private int thumbnailHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        setContentView(R.layout.activity_show_image);
        photos = mIntent.getStringArrayListExtra("images");
        picsSize = mIntent.getIntegerArrayListExtra("picsSize");
        picsColor = mIntent.getIntegerArrayListExtra("picsColor");
        mCurrentPosition = mIntent.getIntExtra("index", 0);
        thumbnailWidth = mIntent.getIntExtra("thumbnailWidth",200);
        thumbnailHeight = mIntent.getIntExtra("thumbnailHeight",200);


        MyPagerAdapter adapter = new MyPagerAdapter(photos);
        mViewPager = (ViewPagerFixed)findViewById(R.id.viewpager);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mCurrentPosition); //设置默认当前页

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setCurrentItem(mCurrentPosition);
        screenWidth = ScreenUtil.getScreenWidth(this);
        screenHeight = ScreenUtil.getScreenHeight(this);

    }
    class MyPagerAdapter extends PagerAdapter implements IconPagerAdapter {
        private ArrayList<String> photos;
        public MyPagerAdapter(ArrayList photos) {
            this.photos=photos;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //动态添加布局一定要有一个跟布局,即使只有一个image,没有根布局居中都做不了
            RelativeLayout rootLayout = new RelativeLayout(ShowImageActivity.this);

            final ImageView bg = new ImageView(ShowImageActivity.this);
            RelativeLayout.LayoutParams bgParams = new RelativeLayout.LayoutParams(thumbnailWidth,thumbnailHeight);
            bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            bg.setAdjustViewBounds(true);
            bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bg.setBackgroundColor(picsColor.get(position));
            rootLayout.addView(bg, bgParams);

            final ProgressBar progressBar = new ProgressBar(ShowImageActivity.this);
            progressBar.setAlpha(0.4f);
            RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(100, 100);
            barParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            rootLayout.addView(progressBar, bgParams);

            final ImageView imageView = new ImageView(ShowImageActivity.this);
            //imageView.setAdjustViewBounds(true);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(screenWidth,screenHeight);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            rootLayout.addView(imageView,lp);
            container.addView(rootLayout);
            //*/

            ImageLoader.ImageListener listener1 = new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if(imageContainer.getBitmap()!=null) {
                        bg.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    imageView.setImageBitmap(imageContainer.getBitmap());
                    PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
                    mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                        @Override
                        public void onPhotoTap(View view, float v, float v1) {
                            ShowImageActivity.this.finish();
                        }
                    });
                    mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                        @Override
                        public void onViewTap(View view, float v, float v1) {
                            ShowImageActivity.this.finish();
                        }
                    });
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            };
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
            String imgUrl = imageUrlPrefix+photos.get(position);
            Log.e(TAG, "show image:"+imgUrl);
            VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener1);

            ImageLoader.ImageListener bgListener = ImageLoader.getImageListener(bg, 0, 0);
            String bgImgUrl = ImageUtil.getImageUrlWithWH(imageUrlPrefix+photos.get(position),thumbnailWidth,thumbnailHeight);
            VolleyHelper.getInstance().getImageLoader().get(bgImgUrl, bgListener,thumbnailWidth,thumbnailHeight);
            return rootLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
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
