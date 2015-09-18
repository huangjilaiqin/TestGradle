package com.lessask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangji on 2015/9/18.
 */
public class FragmentImageShow extends Activity {
    private View mRootView;
    private ViewPager mViewPager;
    private List<View> viewList;
    private List<String> titleList;
    private TitlePageIndicator mTitlePageIndicator;

    public View onCreate(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.show_image_fragment, null);
            mViewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
            View v1 = inflater.inflate(R.layout.image,null);
            View v2 = inflater.inflate(R.layout.image,null);
            View v3 = inflater.inflate(R.layout.image,null);
            viewList.add(v1);
            viewList.add(v2);
            viewList.add(v3);
            //mTitlePageIndicator = (TitlePageIndicator) mRootView.findViewById(R.id.title);
            mViewPager.setAdapter(new MyPagerAdapter(viewList));
            mViewPager.setCurrentItem(0); //设置默认当前页
        }
        return mRootView;
    }
    class MyPagerAdapter extends PagerAdapter{
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
