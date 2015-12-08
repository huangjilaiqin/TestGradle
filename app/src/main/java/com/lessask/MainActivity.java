package com.lessask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.action.CreateActionActivity;
import com.lessask.action.FragmentAction;
import com.lessask.contacts.FragmentContacts;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.lesson.CreateLessonActivity;
import com.lessask.lesson.FragmentLesson;
import com.lessask.library.FragmentLibrary;
import com.lessask.me.FragmentMe;
import com.lessask.model.ActionItem;
import com.lessask.tag.GetTagsRequest;
import com.lessask.tag.GetTagsResponse;
import com.lessask.tag.TagData;
import com.lessask.tag.TagNet;
import com.lessask.test.FragmentTest;
import com.lessask.video.RecordVideoActivity;
import com.viewpagerindicator.IconPageIndicator;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private DrawerAdapter mAdapter;
    private ArrayList<DrawerItem> datas;
    private RelativeLayout mDrawerView;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private Gson gson = new Gson();
    private ArrayList<Fragment> fragments ;
    private ArrayList<LinearLayout> toolActons;
    private IconPageIndicator iconPageIndicator;
    private Fragment currentFragment;
    private LinearLayout currentToolAction;
    private LinearLayout mainToolAction;
    private LinearLayout lessonToolAction;
    private LinearLayout actionToolAction;

    private static final int CREATE_LESSON = 0;
    public static final int EDIT_ACTION = 1;
    public static final int CREATE_ACTION = 2;
    public static final int RECORD_ACTION = 3;

    private FragmentAction fragmentAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        fragments = new ArrayList<>();
        FragmentMain fragmentMain = new FragmentMain();
        iconPageIndicator = (IconPageIndicator)findViewById(R.id.main_indicator);
        fragmentMain.setIconPageIndicator(iconPageIndicator);
        fragments.add(fragmentMain);
        fragments.add(new FragmentLibrary());
        fragments.add(new FragmentContacts());
        fragments.add(new FragmentLesson());
        fragmentAction = new FragmentAction();
        fragments.add(fragmentAction);
        fragments.add(new FragmentMe());
        fragments.add(new FragmentTest());

        //工具栏布局
        toolActons = new ArrayList<>();
        toolActons.add((LinearLayout)findViewById(R.id.main_tool));
        toolActons.add(null);
        toolActons.add(null);
        toolActons.add((LinearLayout)findViewById(R.id.lesson_tool));
        toolActons.add((LinearLayout)findViewById(R.id.action_tool));
        toolActons.add(null);
        toolActons.add(null);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainToolAction = (LinearLayout)findViewById(R.id.main_tool);
        lessonToolAction = (LinearLayout)findViewById(R.id.lesson_tool);
        actionToolAction = (LinearLayout)findViewById(R.id.action_tool);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        //少了这句就没有动画了
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerView = (RelativeLayout) findViewById(R.id.drawer_view);

        mDrawerList = (ListView) findViewById(R.id.listview_drawer);
        //获取drawer目录
        datas = getDatas();
        mAdapter = new DrawerAdapter(this, datas);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        initToolBar();

        //设置选中 发现 界面
        selectItemManual(0);

        loadData();
    }
    private void initToolBar(){
        findViewById(R.id.create_lesson).setOnClickListener(this);
        findViewById(R.id.create_action).setOnClickListener(this);
    }
    private ArrayList<DrawerItem> getDatas(){
        ArrayList<DrawerItem> datas = new ArrayList<>();
        datas.add(new DrawerItem(R.id.head, "首页"));
        datas.add(new DrawerItem(R.id.head, "图书馆"));
        datas.add(new DrawerItem(R.id.head, "通讯录"));
        datas.add(new DrawerItem(R.id.head, "课程"));
        datas.add(new DrawerItem(R.id.head, "动作库"));
        datas.add(new DrawerItem(R.id.head, "我"));
        datas.add(new DrawerItem(R.id.head, "测试"));
        return  datas;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if(data==null){
            Log.e(TAG, "intent is null");
        }
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case EDIT_ACTION:
                case CREATE_ACTION:
                    fragmentAction.onActivityResult(requestCode, resultCode, data);
                    Log.e(TAG, "CREATE_ACTION back");
                    break;
                case RECORD_ACTION:
                    Intent intent = new Intent(MainActivity.this, CreateActionActivity.class);
                    intent.putExtra("path", data.getStringExtra("path"));
                    intent.putExtra("ratio", data.getFloatExtra("ratio", 0.5f));
                    intent.putExtra("imagePath", data.getStringExtra("imagePath"));
                    startActivityForResult(intent, CREATE_ACTION);
                    Log.e(TAG, "RECORD_ACTION back");
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
        GetTagsRequest request = new GetTagsRequest(globalInfos.getUserid());
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
            case R.id.create_lesson:
                intent = new Intent(MainActivity.this, CreateLessonActivity.class);
                startActivityForResult(intent, CREATE_LESSON);
                break;
            case R.id.create_action:
                intent = new Intent(MainActivity.this, RecordVideoActivity.class);
                intent.putExtra("className", CreateActionActivity.class.getName());
                intent.putExtra("startActivityForResult", true);
                startActivityForResult(intent, RECORD_ACTION);
                break;
        }
    }

    class DrawerAdapter extends BaseAdapter{
        private ArrayList<DrawerItem> datas;
        private Context context;
        private int selectItem;

        public DrawerAdapter(Context context, ArrayList<DrawerItem> datas){
            this.context = context;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_item, null);
            TextView name = (TextView)convertView.findViewById(R.id.name);
            DrawerItem data = datas.get(position);
            name.setText(data.getName());
            if(position==selectItem){
                convertView.setBackgroundColor(getResources().getColor(R.color.background_white_not_transparent));
                name.setTextColor(getResources().getColor(R.color.main_color));
            }else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
                name.setTextColor(getResources().getColor(R.color.black_40));
            }

            return convertView;
        }
        public  void setSelectItem(int selectItem) {
             this.selectItem = selectItem;
        }
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void changeToolbar(int position){
        mDrawerList.setItemChecked(position, true);
        setTitle(datas.get(position).getName());
        Log.e(TAG, "scroll setTitle:"+datas.get(position).getName());
    }
    private void selectItemManual(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = fragments.get(position);
        currentFragment = fragment;
        currentToolAction = mainToolAction;

        FragmentMain f = (FragmentMain)fragment;
        //f.selectViewPagerItem(position);
        f.setCurrentPager(position);


        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(datas.get(position).getName());
        mAdapter.setSelectItem(position);
        mAdapter.notifyDataSetChanged();
    }

    public void changeDrawerMenu(int position){
        mAdapter.setSelectItem(position);
        mAdapter.notifyDataSetChanged();
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        mAdapter.setSelectItem(position);
        mAdapter.notifyDataSetChanged();



        // Insert the fragment by replacing any existing fragment
        Fragment fragment = fragments.get(position);
        if(currentFragment!=fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (!fragment.isAdded()) {
                fragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.main_fragment_container, fragment)
                        .commit();
            }else{
                fragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .show(fragment)
                        .commit();
            }
            currentFragment=fragment;
            //设置fragment对应的toolbar布局
            LinearLayout toolAction = toolActons.get(position);
            if(toolAction!=currentToolAction) {
                if(currentToolAction!=null){
                    currentToolAction.setVisibility(View.INVISIBLE);
                }
                if(toolAction!=null){
                    toolAction.setVisibility(View.VISIBLE);
                }
                currentToolAction=toolAction;
            }
            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(datas.get(position).getName());
            mDrawerLayout.closeDrawer(mDrawerView);
        }
    }
}
