package com.lessask;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class FragmentTestActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener{

    private LayoutInflater layoutInflater;
    private Class fragmentArray[] = {FragmentShow.class, FragmentSports.class, FragmentFriends.class, FragmentMe.class};
    private String mTextviewArray[] = {"发现", "运动", "好友", "我"};
    private int fragmentImg[] = {R.drawable.show,R.drawable.show_selected,R.drawable.sports,R.drawable.sports_selected,R.drawable.chat,R.drawable.chat_selected,R.drawable.me,R.drawable.me_selected};
    private static final String TAG = FragmentTestActivity.class.getName();
    private RadioButton rbShow;
    private RadioButton rbSports;
    private RadioButton rbFriends;
    private RadioButton rbMe;
    private RadioButton selectedTab;
    private int selectedTabIndex;

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
        rbShow = (RadioButton) findViewById(R.id.show);
        rbSports = (RadioButton) findViewById(R.id.sports);
        rbFriends = (RadioButton) findViewById(R.id.friends);
        rbMe = (RadioButton) findViewById(R.id.me);

        mGroup.check(R.id.show);
        selectedTab = rbShow;
        selectedTabIndex=0;

        mGroup.setOnCheckedChangeListener(this);
        vp = (ViewPager) findViewById(R.id.pager);
        vp.setOnPageChangeListener(this);
        layoutInflater = LayoutInflater.from(this);
    }

    private void initPager() {
        FragmentShow fgShow = new FragmentShow();
        list.add(fgShow);
        FragmentSports fgSports = new FragmentSports();
        list.add(fgSports);
        FragmentFriends fgFriends = new FragmentFriends();
        list.add(fgFriends);
        FragmentMe fgMe = new FragmentMe();
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
        //selectedTab.setCompoundDrawables(null, this.getDrawable(fragmentImg[selectedTabIndex * 2]),null,null);
        setDrawbleTop(selectedTab, fragmentImg[selectedTabIndex * 2]);
        switch (index) {
            case 0:
                mGroup.check(R.id.show);
                selectedTabIndex=0;
                selectedTab=rbShow;
                //rbShow.setBackground(this.getResources().getDrawable(R.drawable.show_selected));
                //rbShow.setButtonDrawable(R.drawable.show_selected);
                setDrawbleTop(rbShow, R.drawable.show_selected);
                break;
            case 1:
                mGroup.check(R.id.sports);
                selectedTabIndex=1;
                selectedTab=rbSports;
                //rbSports.setBackground(this.getResources().getDrawable(R.drawable.sports_selected));
                //rbSports.setButtonDrawable(R.drawable.sports_selected);
                setDrawbleTop(rbSports, R.drawable.sports_selected);
                break;
            case 2:
                mGroup.check(R.id.friends);
                selectedTabIndex=2;
                selectedTab=rbFriends;
                //rbFriends.setButtonDrawable(R.drawable.chat_selected);
                setDrawbleTop(rbFriends, R.drawable.chat_selected);
                break;
            case 3:
                mGroup.check(R.id.me);
                selectedTabIndex=3;
                selectedTab=rbMe;
                //rbMe.setButtonDrawable(R.drawable.me_selected);
                setDrawbleTop(rbMe, R.drawable.me_selected);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.show:
                vp.setCurrentItem(0);
                break;
            case R.id.sports:
                vp.setCurrentItem(1);
                break;
            case R.id.friends:
                vp.setCurrentItem(2);
                break;
            case R.id.me:
                vp.setCurrentItem(3);
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
    private void setDrawbleTop(RadioButton radioButton, int reid){

        Log.e(TAG, "drawable reid:"+reid);
        Drawable drawable = getResources().getDrawable(reid);
        if(drawable==null){
            Log.e(TAG, "drawable is null");
            return;
        }
        Log.e(TAG, "drawable is not null");
        radioButton.setCompoundDrawables(null, drawable,null,null);
    }

}
