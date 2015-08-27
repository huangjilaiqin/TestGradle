package com.lessask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class FragmentTestActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener{

    private LayoutInflater layoutInflater;
    private Class fragmentArray[] = {FragmentFriends.class, FragmentMe.class};
    private String mTextviewArray[] = {"好友", "我"};
    private static final String TAG = FragmentTestActivity.class.getName();

    private ViewPager vp;
    private RadioGroup mGroup;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        initViews();
        initPager();
    }

    private void initViews() {
        mGroup = (RadioGroup) findViewById(R.id.main_navigation_bar);
        mGroup.check(R.id.friends);
        mGroup.setOnCheckedChangeListener(this);
        vp = (ViewPager) findViewById(R.id.pager);
        vp.setOnPageChangeListener(this);
        layoutInflater = LayoutInflater.from(this);
    }

    private void initPager() {
        FragmentFriends fgFriends = new FragmentFriends();
        Log.e(TAG,"create FragmentFriends ");
        list.add(fgFriends);
        FragmentMe fgMe = new FragmentMe();
        Log.e(TAG,"create FragmentMe");
        list.add(fgMe);
        vp.setAdapter(new MyAdapter(getSupportFragmentManager(), list));
    }

    class MyAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;
        public MyAdapter(FragmentManager fm, ArrayList<Fragment> mList) {
            super(fm);
            list = mList;
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

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int index) {
        switch (index) {
            case 0:
                mGroup.check(R.id.friends);
                break;
            case 1:
                mGroup.check(R.id.me);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.friends:
                vp.setCurrentItem(0);
                break;
            case R.id.me:
                vp.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

}
