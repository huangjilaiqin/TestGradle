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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lessask.global.GlobalInfos;
import com.lessask.me.FragmentMe;
import com.lessask.tag.GetTagsRequest;
import com.lessask.tag.GetTagsResponse;
import com.lessask.tag.TagData;
import com.lessask.tag.TagNet;
import com.viewpagerindicator.IconPageIndicator;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private DrawerAdapter mAdapter;
    private ArrayList<DrawerItem> datas;
    private RelativeLayout mDrawerView;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private ArrayList<Fragment> fragments ;
    private int currentSelectItem;
    private IconPageIndicator iconPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        fragments.add(fragmentMain);
        fragments.add(fragmentMain);
        fragments.add(new FragmentMe());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        //少了这句就没有动画了
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerView = (RelativeLayout) findViewById(R.id.drawer_view);

        mDrawerList = (ListView) findViewById(R.id.listview_drawer);
        datas = getDatas();
        mAdapter = new DrawerAdapter(this, datas);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //设置选中 发现 界面
        selectItemManual(0);

        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
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

    private ArrayList<DrawerItem> getDatas(){
        ArrayList<DrawerItem> datas = new ArrayList<>();
        datas.add(new DrawerItem(R.id.head, "发现"));
        datas.add(new DrawerItem(R.id.head, "训练"));
        datas.add(new DrawerItem(R.id.head, "聊天"));
        datas.add(new DrawerItem(R.id.head, "我"));
        return  datas;
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
        currentSelectItem = position;
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

        Fragment fragment = null;
        if(position<3) {
            iconPageIndicator.setVisibility(View.VISIBLE);
            fragment = new FragmentMain();
            FragmentMain fragmentMain = (FragmentMain)fragment;
            fragmentMain.setIconPageIndicator(iconPageIndicator);
            fragmentMain.setCurrentPager(position);
            Log.e(TAG, "selectItem:"+position);
            if(currentSelectItem>=3){
                fragmentMain.setOnlyOut(true);
            }else {
                fragmentMain.setOnlyOut(false);
            }
        }else if(position==3) {
            fragment = new FragmentMe();
            Log.e(TAG, "selectItem:"+position);
            iconPageIndicator.setVisibility(View.INVISIBLE);
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(datas.get(position).getName());
        mDrawerLayout.closeDrawer(mDrawerView);
        currentSelectItem = position;
    }
}
