package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lessask.action.FragmentAction;
import com.lessask.chat.FragmentChat;
import com.lessask.lesson.FragmentLesson;
import com.lessask.recyclerview.RecycleViewScrollListener;
import com.lessask.recyclerview.ScrollAwareFABBehavior;
import com.lessask.show.CreateShowActivity;
import com.lessask.show.FragmentShow;
import com.lessask.util.ScreenUtil;

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
    private FragmentShow fragmentShow;
    private FragmentChat fragmentChat;
    private FragmentLesson fragmentLesson;
    private FragmentAction fragmentAction;
    private FloatingActionButton mFab;
    private void animateIn(FloatingActionButton button) {
            button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(new FastOutSlowInInterpolator()).withLayer().setListener(null)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_in);
            anim.setDuration(200L);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            button.startAnimation(anim);
        }
    }
    private ScrollAwareFABBehavior scrollAwareFABBehavior;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_teach, null);

            AppBarLayout appBarLayout = (AppBarLayout)rootView.findViewById(R.id.appbar_layout);
            Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            mToolbar.setTitle("教学");
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);

            scrollAwareFABBehavior = new ScrollAwareFABBehavior(getContext(),null);

            mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);

            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

            MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
            fragmentShow = new FragmentShow();
            fragmentLesson = new FragmentLesson();
            //
            RecycleViewScrollListener recycleViewScrollListener = new RecycleViewScrollListener() {
                @Override
                public void onRecycleViewScroll(RecyclerView recyclerView, int dx, int dy) {
                    Log.e(TAG, "dy:"+dy);
                    if(dy>0){
                        //向上滚动
                        //scrollAwareFABBehavior.animateOut(mFab);
                        mFab.hide();
                    }else if(dy<0){
                        //向下滚动
                        //animateIn(mFab);
                        mFab.show();
                    }
                }
            };
            fragmentLesson.setRecycleViewScrollListener(recycleViewScrollListener);
            myFragmentPagerAdapter.addFragment(fragmentLesson, "课程");

            fragmentChat = new FragmentChat();
            fragmentAction = new FragmentAction();
            myFragmentPagerAdapter.addFragment(fragmentAction, "动作库");

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
