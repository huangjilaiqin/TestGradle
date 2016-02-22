package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.lessask.chat.FragmentChat;
import com.lessask.recyclerview.RecycleViewScrollListener;
import com.lessask.show.CreateShowActivity;
import com.lessask.show.FragmentShow;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class FragmentDiscover extends Fragment implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private static final String TAG = FragmentDiscover.class.getName();
    private View rootView;

    private ViewPager mViewPager;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();
    private int currentPager;
    private boolean onlyOut;
    private FloatingActionButton mCreate;

    private final int CAHNGE_FAB_COLOR = 1;
    private FragmentShow fragmentShow;
    private FragmentChat fragmentChat;
    public static final int CREATE_SHOW = 4;
    private FloatingActionButton mFab;
    private DrawerLayout mDrawerLayout;
    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    private View.OnClickListener createShowLintener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PhotoPickerIntent intent = new PhotoPickerIntent(getActivity());
            intent.setPhotoCount(4);
            intent.setShowCamera(true);
            intent.setShowGif(true);
            startActivityForResult(intent, MainActivity.GETPICTURE_REQUEST);

            //Intent intent = new Intent(getContext(), CreateShowActivity.class);
            //startActivityForResult(intent, CREATE_SHOW);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_discover, null);
            Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            mToolbar.setTitle("发现");
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            });

            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

            MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
            RecycleViewScrollListener recycleViewScrollListener = new RecycleViewScrollListener() {
                @Override
                public void onRecycleViewScroll(RecyclerView recyclerView, int dx, int dy) {
                    if(dy>0){
                        //向上滚动
                        mFab.hide();
                    }else if(dy<0){
                        //向下滚动
                        mFab.show();
                    }
                }
            };
            fragmentShow = new FragmentShow();
            fragmentShow.setRecycleViewScrollListener(recycleViewScrollListener);
            myFragmentPagerAdapter.addFragment(fragmentShow, "动态");

            fragmentChat = new FragmentChat();
            myFragmentPagerAdapter.addFragment(fragmentChat, "消息");

            mViewPager.setAdapter(myFragmentPagerAdapter);

            mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            mFab.setOnClickListener(createShowLintener);

            TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition()==0){
                        mFab.show();
                        mViewPager.setCurrentItem(0);
                    }else {
                        mFab.hide();
                        mViewPager.setCurrentItem(1);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

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
                case MainActivity.CREATE_SHOW:
                    fragmentShow.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

}
