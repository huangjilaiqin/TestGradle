package com.lessask.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lessask.R;
import com.lessask.me.FragmentStatus;
import com.lessask.me.FragmentTimeline;
import com.lessask.me.FragmentWorkoutPlan;

import java.util.ArrayList;

public class CoordinatorLayout2Activity extends AppCompatActivity {
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator2_layout);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("CoordinatorLayout");
        setSupportActionBar(mToolbar);


        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        myFragmentPagerAdapter.addFragment(new FragmentStatus(), "状态");
        viewPager.setAdapter(myFragmentPagerAdapter);

        //tabLayout = (TabLayout)findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(viewPager);

         //CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
         //collapsingToolbar.setTitle("Title");

    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentDatas;
        private ArrayList<String> fragmentNames;
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

            fragmentDatas = new ArrayList<>();
            fragmentNames = new ArrayList<>();
        }

        public void addFragment(Fragment fragment, String name){
            fragmentDatas.add(fragment);
            fragmentNames.add(name);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentDatas.get(position);
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
