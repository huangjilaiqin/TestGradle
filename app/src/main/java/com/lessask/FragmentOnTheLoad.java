package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lessask.library.FragmentLibrary;
import com.lessask.FragmentWorkout;
import com.lessask.show.CreateShowActivity;
import com.viewpagerindicator.IconPageIndicator;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;

public class FragmentOnTheLoad extends Fragment implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private static final String TAG = FragmentOnTheLoad.class.getName();
    private View rootView;

    private ViewPager mViewPager;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();
    private MainActivity mainActivity;
    private int currentPager;
    private boolean onlyOut;
    private FloatingActionButton mCreate;

    private final int CAHNGE_FAB_COLOR = 1;
    private FragmentWorkout fragmentWorkout;
    private FragmentLibrary fragmentLibrary;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_on_the_load, container, false);

        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("在路上");
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
        //mToolbar.inflateMenu(R.menu.menu_main);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        fragmentWorkout = new FragmentWorkout();
        Log.e(TAG, fragmentWorkout.toString());
        myFragmentPagerAdapter.addFragment(fragmentWorkout, "训练");

        fragmentLibrary = new FragmentLibrary();
        myFragmentPagerAdapter.addFragment(fragmentLibrary, "图书馆");

        mViewPager.setAdapter(myFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mainActivity = (MainActivity) getActivity();
        Log.e(TAG, "FragmentOnTheLoad");
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
                    break;
            }
        }
    }

}
