package com.lessask.show;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.VolleyHelper;
import com.lessask.util.ImageUtil;
import com.lessask.util.ScreenUtil;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.zip.Inflater;

import uk.co.senab.photoview.PhotoViewAttacher;

/*
* 查看动态中的图片
* */

public class ShowImageActivity extends FragmentActivity {
    private final String TAG = ShowImageActivity.class.getName();
    private ViewPager mViewPager;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        setContentView(R.layout.activity_show_image);
        photos = mIntent.getStringArrayListExtra("images");
        picsSize = mIntent.getIntegerArrayListExtra("picsSize");
        picsColor = mIntent.getIntegerArrayListExtra("picsColor");
        mCurrentPosition = mIntent.getIntExtra("index", 0);


        MyPagerAdapter adapter = new MyPagerAdapter(photos);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
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
            int w = picsSize.get(position);
            int h = picsSize.get(position+1);
            Log.e(TAG, "cumtome image size before w:" + w + ", h:" + h);
            if(w>screenWidth || h>screenHeight){
                h=screenWidth*h/w;
                w=screenWidth;
            }
            Log.e(TAG, "cumtome image size w:" + w + ", h:" + h);
            //*
            final ImageView imageView = new ImageView(ShowImageActivity.this);
            //imageView.setAdjustViewBounds(true);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w,h);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setBackgroundColor(picsColor.get(position));
            //imageView.setLayoutParams(lp);
            //imageView.setImageResource(R.drawable.women);
            ViewGroup.LayoutParams params = new ActionBar.LayoutParams(200,200);
            container.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            container.addView(imageView,params);
            //*/

            /*
            LayoutInflater inflater = getLayoutInflater();
            View root = inflater.inflate(R.layout.image_test, null);
            root.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
            final ImageView imageView = (ImageView)root.findViewById(R.id.image);
            imageView.setBackgroundColor(picsColor.get(position));
            container.addView(root);
            //*/
            //container.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, screenHeight));

            ImageLoader.ImageListener listener1 = new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
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
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0,0);
            String imgUrl = ImageUtil.getImageUrlWithWH(imageUrlPrefix+photos.get(position),w,h);
            VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener1);
            //imageView.setImageBitmap(ImageUtil.getOptimizeBitmapFromFile(photos.get(position)));
            /*
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
            //*/
            return imageView;
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
