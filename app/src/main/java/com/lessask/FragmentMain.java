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

import java.util.ArrayList;

public class FragmentMain extends Fragment implements ViewPager.OnPageChangeListener {

    private LayoutInflater layoutInflater;
    private int REQUEST_CODE = 100;
    private static final String TAG = FragmentMain.class.getName();
    private View rootView;
    private ViewPager vp;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();
    private MainActivity mainActivity;
    private int currentPager;

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
        if(vp!=null)
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
        FragmentChat fgFriends = new FragmentChat();
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
        mainActivity.changeToolbar(index);
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
