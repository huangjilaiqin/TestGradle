package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lessask.action.FragmentAction;
import com.lessask.chat.FragmentMessage;
import com.lessask.lesson.CreateLessonActivity;
import com.lessask.lesson.FragmentLesson;
import com.lessask.recyclerview.RecycleViewScrollListener;
import com.lessask.recyclerview.ScrollAwareFABBehavior;
import com.lessask.show.CreateShowActivity;
import com.lessask.show.FragmentShow;
import com.lessask.video.RecordVideoActivity;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;

public class FragmentTeach extends Fragment implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private static final String TAG = FragmentTeach.class.getName();
    private View rootView;

    private ViewPager mViewPager;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();
    private MainActivity mainActivity;
    private int currentPager;
    private boolean onlyOut;

    private final int CAHNGE_FAB_COLOR = 1;
    private FragmentLesson fragmentLesson;
    private FragmentAction fragmentAction;
    private FloatingActionButton mFab;

    public static final int RECORD_ACTION = 3;
    public static final int CREATE_SHOW = 4;
    private final int CREATE_LESSON = 1;

    private ScrollAwareFABBehavior scrollAwareFABBehavior;

    private View.OnClickListener createLessonLintener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), CreateLessonActivity.class);
            startActivityForResult(intent, CREATE_LESSON);
        }
    };
    private View.OnClickListener createActionLintener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), RecordVideoActivity.class);
            startActivityForResult(intent, RECORD_ACTION);
        }
    };
    private DrawerLayout mDrawerLayout;
    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_teach, null);

            AppBarLayout appBarLayout = (AppBarLayout)rootView.findViewById(R.id.appbar_layout);
            Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            mToolbar.setTitle("教学");
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            });

            scrollAwareFABBehavior = new ScrollAwareFABBehavior(getContext(),null);

            mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);

            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

            MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
            fragmentLesson = new FragmentLesson();
            //
            RecycleViewScrollListener recycleViewScrollListener = new RecycleViewScrollListener() {
                @Override
                public void onRecycleViewScroll(RecyclerView recyclerView, int dx, int dy) {
                    Log.e(TAG, "dy:"+dy);
                    if(dy>0){
                        //向上滚动
                        mFab.hide();
                    }else if(dy<0){
                        //向下滚动
                        mFab.show();
                    }
                }
            };
            fragmentLesson.setRecycleViewScrollListener(recycleViewScrollListener);
            myFragmentPagerAdapter.addFragment(fragmentLesson, "课程");

            fragmentAction = new FragmentAction();
            myFragmentPagerAdapter.addFragment(fragmentAction, "动作库");

            mViewPager.setAdapter(myFragmentPagerAdapter);

            TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mFab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onHidden(fab);
                            mFab.show();
                        }
                    });
                    Log.e(TAG, "tabSelected");
                    switch (tab.getPosition()){
                        case 0:
                            mFab.setOnClickListener(createLessonLintener);
                            mViewPager.setCurrentItem(0);
                            break;
                        case 1:
                            mFab.setOnClickListener(createActionLintener);
                            mViewPager.setCurrentItem(1);
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    Log.e(TAG, "tabUnSelected");
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });



        }
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case MainActivity.GETPICTURE_REQUEST:
                    ArrayList<String> images = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    Log.e(TAG, "onActivityResult" + images);
                    Toast.makeText(getContext(), "fragmentMain onActivityResult", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), CreateShowActivity.class);
                    intent.putStringArrayListExtra("images", data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS));
                    startActivityForResult(intent, MainActivity.CREATE_SHOW);
                    break;
            }
        }
    }

}
