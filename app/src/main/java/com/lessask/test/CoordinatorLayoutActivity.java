package com.lessask.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lessask.R;
import com.lessask.me.FragmentStatus;
import com.lessask.me.FragmentTimeline;
import com.lessask.me.FragmentWorkout;

import java.util.ArrayList;

public class CoordinatorLayoutActivity extends AppCompatActivity {
    private ArrayList<Fragment> fragmentDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_layout);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setTitle("CoordinatorLayout");
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        fragmentDatas = new ArrayList<>();
        fragmentDatas.add(new FragmentStatus());
        fragmentDatas.add(new FragmentTimeline());
        fragmentDatas.add(new FragmentWorkout());

        //myFragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);
    }
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentDatas.get(position);
        }


        @Override
        public int getCount() {
            return fragmentDatas.size();
        }

    }
}
