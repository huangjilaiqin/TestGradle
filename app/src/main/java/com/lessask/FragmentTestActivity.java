package com.lessask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FragmentTestActivity extends FragmentActivity {

    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private Class fragmentArray[] = {FragmentFriends.class, FragmentMe.class};
    private String mTextviewArray[] = {"好友", "我"};

    private ViewPager vp;
    private List<Fragment> list = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        initViews();
        initPager();

    }

    private void initViews() {
        vp = (ViewPager) findViewById(R.id.pager);
        vp.setOnPageChangeListener(new ViewPagerListener());

        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        mTabHost.setOnTabChangedListener(new TabHostListener());

        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中,每个标签与fragment关联起来
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    private void initPager() {
        FragmentFriends fgFriends = new FragmentFriends();
        list.add(fgFriends);
        FragmentMe fgMe = new FragmentMe();
        list.add(fgMe);
        vp.setAdapter(new MyAdapter(getSupportFragmentManager()));
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.main_tab_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    private class TabHostListener implements TabHost.OnTabChangeListener {
        @Override
        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            vp.setCurrentItem(position);
        }
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getCount() {
            return list.size();
        }

    }

    class ViewPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int index) {
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(index);
            widget.setDescendantFocusability(oldFocusability);
        }
    }
}
