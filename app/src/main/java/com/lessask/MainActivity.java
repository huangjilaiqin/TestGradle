package com.lessask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lessask.action.FragmentAction;
import com.lessask.contacts.FragmentContacts;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.lesson.FragmentLesson;
import com.lessask.me.FragmentMe;
import com.lessask.tag.GetTagsRequest;
import com.lessask.tag.GetTagsResponse;
import com.lessask.tag.TagData;
import com.lessask.tag.TagNet;
import com.lessask.test.FragmentTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends MyAppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener{
    private String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ArrayList<DrawerItem> datas;
    private RelativeLayout mDrawerView;
    private NavigationView navigationView;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private Gson gson = new Gson();
    private Map<Integer,Fragment> fragments ;
    private Map<Integer,String> titles;
    private LinearLayout currentToolAction;
    private LinearLayout mainToolAction;

    private static final int CREATE_LESSON = 0;
    public static final int CREATE_SHOW = 4;
    public static final int GETPICTURE_REQUEST = 5;


    private FragmentAction fragmentAction;
    private FragmentOnTheLoad fragmentOnTheLoad;
    private FragmentDiscover fragmentDiscover;

    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusTransparent();
        setContentView(R.layout.activity_main);

        File videoFile = getBaseContext().getExternalFilesDir("video");
        if(videoFile==null)
            videoFile = this.getFileStreamPath("file1");
        config.setVideoCachePath(videoFile);

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		int width = outMetrics.widthPixels;
		int height = outMetrics.heightPixels;
        globalInfos.setScreenWidth(width);
        globalInfos.setScreenHeight(height);

        fragments = new HashMap<>();
        titles = new HashMap<>();
        //fragmentDiscover = new FragmentDiscover();
        fragmentOnTheLoad = new FragmentOnTheLoad();
        fragments.put(R.id.on_the_load, fragmentOnTheLoad);
        titles.put(R.id.on_the_load, "在路上");
        fragments.put(R.id.discover, new FragmentDiscover());
        titles.put(R.id.discover, "发现");
        fragments.put(R.id.contact, new FragmentContacts());
        titles.put(R.id.contact, "通讯录");
        fragments.put(R.id.lesson,new FragmentLesson());
        titles.put(R.id.lesson, "课程");
        fragmentAction = new FragmentAction();
        fragments.put(R.id.action, fragmentAction);
        titles.put(R.id.action, "动作");
        fragments.put(R.id.me, new FragmentMe());
        titles.put(R.id.me, "我");
        fragments.put(R.id.test, new FragmentTest());
        titles.put(R.id.test, "测试");

        //mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.inflateMenu(R.menu.menu_main);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        ////少了这句就没有动画了
        //mDrawerToggle.syncState();
        //mDrawerLayout.setDrawerListener(mDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        //设置选中 发现 界面
        addFragment(fragmentOnTheLoad);


        setTitle(titles.get(R.id.on_the_load));

        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //父类处理fragment的startActivityForresult
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                default:
                    Log.e(TAG, "not match requestCode:"+requestCode);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e(TAG, "isTaskRoot:"+isTaskRoot());
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private TagNet.GetTagsListener getTagsListener = new TagNet.GetTagsListener() {
        @Override
        public void getTagsResponse(GetTagsResponse response) {
            ArrayList<TagData> allTags = response.getTagDatas();
            globalInfos.getActionTagsHolder().setActionTags(allTags);
            Log.e(TAG, "gettags resp size:" + allTags.size());
        }
    };
    private void loadData(){
        TagNet mTagNet = TagNet.getInstance();
        mTagNet.setGetTagsListener(getTagsListener);
        GetTagsRequest request = new GetTagsRequest(globalInfos.getUserId());
        mTagNet.emit("gettags", gson.toJson(request));
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            /*
            case R.id.create_action:
                intent = new Intent(MainActivity.this, RecordVideoActivity.class);
                intent.putExtra("className", CreateActionActivity.class.getName());
                intent.putExtra("startActivityForResult", true);
                startActivityForResult(intent, RECORD_ACTION);
                break;
                */
        }
    }

    private void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, fragment)
            .commit();
        currentFragment = fragment;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit();
        currentFragment = fragment;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.on_the_load) {
            replaceFragment(fragments.get(id));
            //replaceFragment(new FragmentOnTheLoad());
            setTitle(titles.get(id));
        } else if (id == R.id.discover) {
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
            Log.e(TAG, "discover");
        } else if (id == R.id.contact) {
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        } else if (id == R.id.lesson) {
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        } else if (id == R.id.action) {
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        } else if (id == R.id.me) {
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        }else if (id == R.id.test){
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
