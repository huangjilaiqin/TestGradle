package com.lessask.me;


import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.chat.Chat;
import com.lessask.global.GlobalInfos;

import java.util.ArrayList;
import com.lessask.MyFragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Created by JHuang on 2015/8/23.
 */
public class FragmentMe extends Fragment{
    private String TAG = FragmentMe.class.getSimpleName();
    private Chat chat = Chat.getInstance();
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private View rootView;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private FragmentWorkoutPlan fragmentWorkoutPlan;
    public final static int WORKOUT_ADD=1;
    public final static int WORKOUT_CHANGE=2;

    private DrawerLayout mDrawerLayout;
    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_me, container,false);

            Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            mToolbar.setTitle("我");
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentWorkoutPlan = new FragmentWorkoutPlan();
        fragmentWorkoutPlan.setUserid(globalInfos.getUserId());
        FragmentStatus fragmentStatus = new FragmentStatus();
        fragmentStatus.setInsterestUserid(globalInfos.getUserId());
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        myFragmentPagerAdapter.addFragment(fragmentStatus, "动态");
        myFragmentPagerAdapter.addFragment(fragmentWorkoutPlan, "训练");
        mViewPager = (ViewPager)rootView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(myFragmentPagerAdapter);

        tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case WORKOUT_ADD:
                case WORKOUT_CHANGE:
                    fragmentWorkoutPlan.onActivityResult(requestCode,resultCode,data);
                    break;
            }
        }
    }
}



