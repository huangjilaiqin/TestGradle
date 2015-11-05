package com.lessask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.lessask.model.Login;
import com.lessask.show.FragmentShow;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

import me.iwf.photopicker.utils.PhotoPickerIntent;

public class FragmentMain extends Fragment implements ViewPager.OnPageChangeListener,View.OnClickListener {

    private LayoutInflater layoutInflater;
    private int REQUEST_CODE = 100;
    private static final String TAG = FragmentMain.class.getName();
    private View rootView;
    private ViewPager mViewPager;
    private ArrayList<Fragment> list = new ArrayList<Fragment>();
    private MainActivity mainActivity;
    private int currentPager;
    private IconPageIndicator iconPageIndicator;
    private FloatingActionButton mCreate;

    private final int CAHNGE_FAB_COLOR = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:"+msg.what);
            switch (msg.what){
                case CAHNGE_FAB_COLOR:
                    Log.e(TAG, "handler currentpager:"+currentPager);
                    if(currentPager==0) {
                        mCreate.setBackgroundTintList(getResources().getColorStateList(R.color.main_color));
                        mCreate.setImageResource(R.drawable.camera);
                    }else if(currentPager==1){
                        mCreate.setBackgroundTintList(getResources().getColorStateList(R.color.red_fab));
                        mCreate.setImageResource(R.drawable.add);
                    }else if(currentPager==2){
                        mCreate.setBackgroundTintList(getResources().getColorStateList(R.color.purple_fab));
                    }
                    Animation ani2 = new ScaleAnimation(0f,1f,0f,1f,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
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
        Log.e(TAG, "onCreateView:"+currentPager);
        selectViewPagerItem(currentPager);
        return rootView;
    }

    public void selectViewPagerItem(int position){
        if(mViewPager!=null)
            mViewPager.setCurrentItem(position);
        if(iconPageIndicator!=null)
            iconPageIndicator.setCurrentItem(position);
    }


    private void initViews() {
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        mCreate = (FloatingActionButton) rootView.findViewById(R.id.create);
        mCreate.setBackgroundTintList(getResources().getColorStateList(R.color.main_color));
        mCreate.setOnClickListener(this);
    }

    private void initPager() {
        FragmentShow fgShow = new FragmentShow();
        list.add(fgShow);
        FragmentSports fgSports = new FragmentSports();
        list.add(fgSports);
        FragmentChat fgFriends = new FragmentChat();
        list.add(fgFriends);
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager(), list));
        iconPageIndicator.setViewPager(mViewPager);
        iconPageIndicator.setOnPageChangeListener(this);
        iconPageIndicator.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create:
                //mCreate.animate().scaleX(0.1f).scaleY(0.1f).start();
                PhotoPickerIntent intent = new PhotoPickerIntent(getActivity());
                intent.setPhotoCount(4);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
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
        if(mainActivity==null)
            mainActivity=(MainActivity)getActivity();
        mainActivity.changeToolbar(index);
        Log.e(TAG, "onPageSelected " + index);

        currentPager = index;
        Animation ani = new ScaleAnimation(1f,0f,1f,0f,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        ani.setInterpolator(new AccelerateInterpolator());
        ani.setDuration(250);
        mCreate.startAnimation(ani);
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Message msg = new Message();
                msg.what = CAHNGE_FAB_COLOR;
                handler.sendMessage(msg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        //mCreate.animate().scaleX(1f).scaleY(1f).start();
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
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
    }
}
