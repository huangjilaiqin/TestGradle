package com.lessask;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ShowImageActivity extends Activity {

    private View mRootView;
    private ViewPager mViewPager;
    private List<View> viewList;
    private List<String> titleList;
    private TitlePageIndicator mTitlePageIndicator;
    private LayoutInflater mInflate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        viewList = new ArrayList<>();
        mInflate = getLayoutInflater().from(this);
        View v1 = mInflate.inflate(R.layout.image, null);
        ImageView image1 = (ImageView)v1.findViewById(R.id.image);
        new PhotoViewAttacher(image1);
        View v2 = mInflate.inflate(R.layout.image,null);
        ImageView image2 = (ImageView)v1.findViewById(R.id.image);
        new PhotoViewAttacher(image2);
        View v3 = mInflate.inflate(R.layout.image,null);
        ImageView image3 = (ImageView)v1.findViewById(R.id.image);
        new PhotoViewAttacher(image3);

        viewList.add(v1);
        viewList.add(v2);
        viewList.add(v3);
        //mTitlePageIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.title);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(viewList));
        mViewPager.setCurrentItem(0); //设置默认当前页

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
    }
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mListViews;
         public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;//构造方法，参数是我们的页卡，这样比较方便。
        }
         @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }

            @Override
            public int getCount() {

                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                    Object object) {
                container.removeView(viewList.get(position));

            }

            @Override
            public int getItemPosition(Object object) {

                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {

                //return titleList.get(position);
                return null;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
    }
}
