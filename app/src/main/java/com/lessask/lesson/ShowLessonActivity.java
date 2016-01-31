package com.lessask.lesson;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Lesson;
import com.lessask.net.VolleyHelper;

import java.util.ArrayList;

public class ShowLessonActivity extends AppCompatActivity {
    private Intent mIntent;
    private Lesson lesson;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_lesson);

        mIntent = getIntent();
        lesson = mIntent.getParcelableExtra("lesson");
        getWindow().setStatusBarColor(Color.TRANSPARENT);

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

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        FragmentLessonActions fragmentLessonActions = new FragmentLessonActions();
        fragmentLessonActions.setLesson(lesson);
        myFragmentPagerAdapter.addFragment(fragmentLessonActions, "动作");
        myFragmentPagerAdapter.addFragment(new FragmentLessonDetail(), "详情");
        viewPager.setAdapter(myFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(lesson.getName());

        ImageView cover = (ImageView)findViewById(R.id.cover);
        cover.setBackgroundColor(getResources().getColor(R.color.main_color));
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(cover,0,0);
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + lesson.getCover(), listener);

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
