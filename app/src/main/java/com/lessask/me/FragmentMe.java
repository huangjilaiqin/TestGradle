package com.lessask.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.chat.Chat;
import com.lessask.global.GlobalInfos;
import com.lessask.me.FragmentStatus;
import com.lessask.me.FragmentTimeline;
import com.lessask.me.FragmentWorkout;
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
    private ArrayList<Fragment> fragmentDatas;
    private ArrayList<String> fragmentNames;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_me, null);
            fragmentDatas = new ArrayList<>();
            fragmentDatas.add(new FragmentStatus());
            fragmentDatas.add(new FragmentTimeline());
            fragmentDatas.add(new FragmentWorkout());
            fragmentNames = new ArrayList<>();
            fragmentNames.add("动态");
            fragmentNames.add("时间轴");
            fragmentNames.add("训练");

            //myFragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
            myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
            //myFragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().);
            mViewPager = (ViewPager)rootView.findViewById(R.id.viewpager);
            mViewPager.setAdapter(myFragmentPagerAdapter);
            mViewPager.setCurrentItem(0); //设置默认当前页
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Log.e(TAG, "onPageSelected:"+position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            indicator = (TitlePageIndicator)rootView.findViewById(R.id.indicator);
            indicator.setViewPager(mViewPager);
            indicator.setCurrentItem(0);
        }
        return rootView;
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
}
