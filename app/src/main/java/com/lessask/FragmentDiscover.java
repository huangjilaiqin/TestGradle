package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.lessask.chat.FragmentChat;
import com.lessask.library.FragmentLibrary;
import com.lessask.show.CreateShowActivity;
import com.lessask.show.FragmentShow;
import com.lessask.sports.FragmentSports;
import com.viewpagerindicator.IconPageIndicator;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;

public class FragmentDiscover extends Fragment implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private static final String TAG = FragmentDiscover.class.getName();
    private View rootView;

    private ViewPager mViewPager;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();
    private MainActivity mainActivity;
    private int currentPager;
    private boolean onlyOut;
    private IconPageIndicator iconPageIndicator;
    private FloatingActionButton mCreate;

    private final int CAHNGE_FAB_COLOR = 1;
    private FragmentShow fragmentShow;
    private FragmentSports fragmentSports;
    private FragmentLibrary fragmentLibrary;
    private FragmentChat fragmentChat;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:" + msg.what);
            switch (msg.what) {
                case CAHNGE_FAB_COLOR:
                    Log.e(TAG, "handler currentpager:" + currentPager);
                    if (currentPager == 0) {
                        mCreate.setBackgroundTintList(getResources().getColorStateList(R.color.main_color));
                        mCreate.setImageResource(R.drawable.camera);
                    } else if (currentPager == 1) {
                        mCreate.setBackgroundTintList(getResources().getColorStateList(R.color.red_fab));
                        mCreate.setImageResource(R.drawable.add);
                    } else if (currentPager == 2) {
                        mCreate.setBackgroundTintList(getResources().getColorStateList(R.color.purple_fab));
                    }
                    Animation ani2 = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ani2.setInterpolator(new DecelerateInterpolator());
                    ani2.setDuration(250);
                    mCreate.startAnimation(ani2);

                default:
                    break;
            }
        }
    };


    public void setIconPageIndicator(IconPageIndicator iconPageIndicator) {
        this.iconPageIndicator = iconPageIndicator;
    }

    public void setCurrentPager(int position) {
        this.currentPager = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main, null);
            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

            MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
            fragmentShow = new FragmentShow();
            myFragmentPagerAdapter.addFragment(fragmentShow, "动态");

            fragmentChat = new FragmentChat();
            myFragmentPagerAdapter.addFragment(fragmentChat,"消息");

            mViewPager.setAdapter(myFragmentPagerAdapter);

            TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            mainActivity = (MainActivity) getActivity();
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
