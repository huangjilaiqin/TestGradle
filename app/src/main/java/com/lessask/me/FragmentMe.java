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
    private TitlePageIndicator indicator;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragmentDatas;
    private ArrayList<String> fragmentNames;
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
        fragmentDatas = new ArrayList<>();
        fragmentWorkoutPlan = new FragmentWorkoutPlan();
        fragmentDatas.add(new FragmentStatus());
        fragmentDatas.add(fragmentWorkoutPlan);
        fragmentNames = new ArrayList<>();
        fragmentNames.add("动态");
        fragmentNames.add("训练");

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        //myFragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager)rootView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(myFragmentPagerAdapter);

        tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e(TAG, "fragmentMe getitem:"+fragmentDatas.get(position));
            return fragmentDatas.get(position);
        }

        @Override
        public int getIconResId(int index) {
            return 0;
        }

        @Override
        public int getCount() {
            return fragmentDatas.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentNames.get(position);
        }
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



