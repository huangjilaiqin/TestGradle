package com.lessask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.show.FragmentShow;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

public class FragmentMain extends Fragment implements ViewPager.OnPageChangeListener {

    private LayoutInflater layoutInflater;
    private int REQUEST_CODE = 100;
    private static final String TAG = FragmentMain.class.getName();
    private View rootView;
    private ViewPager mViewPager;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();
    private MainActivity mainActivity;
    private int currentPager;
    private IconPageIndicator iconPageIndicator;

    public void setIconPageIndicator(IconPageIndicator iconPageIndicator) {
        this.iconPageIndicator = iconPageIndicator;
    }

    public void setCurrentPager(int position){
        this.currentPager = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_main, null);
            initViews();
            initPager();
            mainActivity = (MainActivity)getActivity();
        }
        Log.e(TAG, "onCreateView");
        selectViewPagerItem(currentPager);
        return rootView;
    }

    public void selectViewPagerItem(int position){
        if(mViewPager!=null)
            mViewPager.setCurrentItem(position);
    }


    private void initViews() {
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
    }

    private void initPager() {
        FragmentShow fgShow = new FragmentShow();
        list.add(fgShow);
        FragmentSports fgSports = new FragmentSports();
        list.add(fgSports);
        FragmentChat fgFriends = new FragmentChat();
        list.add(fgFriends);
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager(), list));
        this.iconPageIndicator.setViewPager(mViewPager);
        this.iconPageIndicator.setOnPageChangeListener(this);
        this.iconPageIndicator.setCurrentItem(0);
    }

    class MyAdapter extends FragmentPagerAdapter implements IconPagerAdapter{
        ArrayList<Fragment> list;
        protected final int[] ICONS = new int[] {
            R.drawable.show_indicator,
            R.drawable.sport_indicator,
            R.drawable.chat_indicator,
        };
        public MyAdapter(FragmentManager fm, ArrayList<Fragment> mList) {
            super(fm);
            list = mList;
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getIconResId(int index) {
            return ICONS[index % ICONS.length];
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
        mainActivity.changeToolbar(index);
        Log.e(TAG, "onPageSelected "+index);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
    }
}
