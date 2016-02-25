package com.lessask;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lessask.me.FragmentStatus;
import com.lessask.me.FragmentWorkoutPlan;

public class UserActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private FragmentWorkoutPlan fragmentWorkoutPlan;
    private int userid;
    private String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
        userid = intent.getIntExtra("userid", -1);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(intent.getStringExtra("nickname"));
        //没有这句话右侧的菜单栏不会显示
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fragmentWorkoutPlan = new FragmentWorkoutPlan();
        fragmentWorkoutPlan.setUserid(userid);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        FragmentStatus fragmentStatus = new FragmentStatus();
        fragmentStatus.setInsterestUserid(userid);
        myFragmentPagerAdapter.addFragment(fragmentStatus, "动态");
        myFragmentPagerAdapter.addFragment(fragmentWorkoutPlan, "训练");
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(myFragmentPagerAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
