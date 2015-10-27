package com.lessask;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.lessask.global.GlobalInfos;
import com.lessask.me.FragmentMe;
import com.lessask.show.FragmentShow;
import com.lessask.tag.GetTagsRequest;
import com.lessask.tag.GetTagsResponse;
import com.lessask.tag.TagData;
import com.lessask.tag.TagNet;

import java.util.ArrayList;

public class FragmentMain extends Fragment implements ViewPager.OnPageChangeListener {

    private LayoutInflater layoutInflater;
    private int REQUEST_CODE = 100;
    private static final String TAG = FragmentMain.class.getName();
    private View rootView;
    private ViewPager vp;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_main, null);
            initViews();
            initPager();
        }
        return rootView;
    }

    public void select(int position){
        vp.setCurrentItem(position);
    }


    private void initViews() {
        vp = (ViewPager) rootView.findViewById(R.id.pager);
        vp.setOnPageChangeListener(this);
    }

    private void initPager() {
        FragmentShow fgShow = new FragmentShow();
        list.add(fgShow);
        FragmentSports fgSports = new FragmentSports();
        list.add(fgSports);
        FragmentFriends fgFriends = new FragmentFriends();
        list.add(fgFriends);
        vp.setAdapter(new MyAdapter(getChildFragmentManager(), list));
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

    }

}
