package com.lessask;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lessask.action.FragmentAction;
import com.lessask.chat.ChatGroup;
import com.lessask.contacts.FragmentContacts;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.lesson.FragmentLesson;
import com.lessask.me.FragmentMe;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.User;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.tag.GetTagsRequest;
import com.lessask.tag.GetTagsResponse;
import com.lessask.tag.TagData;
import com.lessask.tag.TagNet;
import com.lessask.test.FragmentTest;

import java.io.File;
import java.lang.reflect.Type;
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

    private int currentFragmentId;

    private SharedPreferences baseInfo;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);
        editor = baseInfo.edit();
        Log.e(TAG, "onCreate");
        //加载数据
        loadBaseData();

        setStatusTransparent();
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
        fragmentOnTheLoad.setmDrawerLayout(mDrawerLayout);
        fragments.put(R.id.on_the_load, fragmentOnTheLoad);
        titles.put(R.id.on_the_load, "在路上");

        FragmentDiscover fragmentDiscover = new FragmentDiscover();
        fragmentDiscover.setmDrawerLayout(mDrawerLayout);
        fragments.put(R.id.discover, fragmentDiscover);
        titles.put(R.id.discover, "发现");

        FragmentContacts fragmentContacts = new FragmentContacts();
        fragmentContacts.setmDrawerLayout(mDrawerLayout);
        fragments.put(R.id.contact, fragmentContacts);
        titles.put(R.id.contact, "通讯录");

        FragmentTeach fragmentTeach = new FragmentTeach();
        fragmentTeach.setmDrawerLayout(mDrawerLayout);
        fragments.put(R.id.teach, fragmentTeach);
        titles.put(R.id.teach, "教学");

        FragmentMe fragmentMe = new FragmentMe();
        fragmentMe.setmDrawerLayout(mDrawerLayout);
        fragments.put(R.id.me, fragmentMe);
        titles.put(R.id.me, "我");

        fragments.put(R.id.test, new FragmentTest());
        titles.put(R.id.test, "测试");

        //mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.inflateMenu(R.menu.menu_main);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        ////少了这句就没有动画了
        //mDrawerToggle.syncState();
        //mDrawerLayout.setDrawerListener(mDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.drawer_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadData();

        if(savedInstanceState==null){
            //设置选中 发现 界面
            replaceFragment(fragmentOnTheLoad);
            setTitle(titles.get(R.id.on_the_load));
        }
    }

    //加载基础数据
    private void loadBaseData(){
        if(!baseInfo.getBoolean("syncData", false)) {
            SQLiteDatabase db = globalInfos.getDb(getBaseContext());
            loadUser(db);
            loadChatGroups(db);
            loadFriends(db);

            editor.putBoolean("syncData", true);
            editor.commit();
        }else{
            SQLiteDatabase db = globalInfos.getDb(getBaseContext());
            loadUserFromDb(db);
            loadChatGroupsFromDb(db);
            loadFriendsFromDb(db);
        }
    }

    private void loadChatGroupsFromDb(SQLiteDatabase db){

    }
    private void loadFriendsFromDb(SQLiteDatabase db){

    }
    private void loadUserFromDb(SQLiteDatabase db){

    }

    //加载用户自己的信息
    private void loadUser(final SQLiteDatabase db){
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getUserUrl(), User.class, new GsonRequest.PostGsonRequest<User>() {
            @Override
            public void onStart() {}
            @Override
            public void onResponse(User user) {
                if(user.getError()!=null && user.getError()!="" || user.getErrno()!=0){
                    Log.e(TAG, "loadUser onResponse error:" + user.getError() + ", " + user.getErrno());
                }else {
                    ContentValues values = new ContentValues();
                    values.put("userid", user.getUserid());
                    values.put("nickname", user.getNickname());
                    values.put("headimg", user.getHeadImg());
                    Cursor cursor = db.rawQuery("select 1 from t_user where userid=?", new String[]{"" + user.getUserid()});
                    if(cursor.getCount()==0){
                        db.insert("t_user", "", values);
                    }else {
                        db.update("t_user", values,"userid=?", new String[]{""+user.getUserid()});
                    }
                    globalInfos.setUser(user);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(MainActivity.this, "网络错误:加载聊天列表" + error, Toast.LENGTH_SHORT);
            }
            @Override
            public void setPostData(Map datas) {
                datas.put("userid", "" + globalInfos.getUserId());
                datas.put("token", globalInfos.getToken());
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", "" + globalInfos.getUserId());
                datas.put("token", globalInfos.getToken());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
    //加载用户聊天列表
    private void loadChatGroups(final SQLiteDatabase db){
        Type type = new TypeToken<ArrayListResponse<ChatGroup>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getChatGroupUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {}

            @Override
            public void onResponse(ArrayListResponse response) {

                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    //Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                }else {
                    ArrayList<ChatGroup> datas = response.getDatas();
                    //入库,本地化
                    for(int i=0;i<datas.size();i++){
                        ChatGroup chatGroup = datas.get(i);
                        ContentValues values = new ContentValues();
                        values.put("chatgroup_id", chatGroup.getChatgroupId());
                    values.put("name", chatGroup.getName());
                        db.insert("t_chatgroup", "", values);
                    }
                    Log.e(TAG, "insert db");
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(MainActivity.this, "网络错误:加载聊天列表" + error, Toast.LENGTH_SHORT);
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", "" + globalInfos.getUserId());
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", "" + globalInfos.getUserId());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

    //加载用户聊天列表
    private void loadFriends(final SQLiteDatabase db){
        Type type = new TypeToken<ArrayListResponse<User>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getFriendsUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(ArrayListResponse response) {

                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                }else {
                    ArrayList<User> datas = response.getDatas();
                    //入库,本地化
                    for(int i=0;i<datas.size();i++){
                        User user = datas.get(i);
                        ContentValues values = new ContentValues();
                        values.put("userid", user.getUserid());
                        values.put("nickname", user.getNickname());
                        values.put("headImg", user.getHeadImg());
                        db.insert("t_contact", "", values);
                        Log.e(TAG, "insert db:"+user.getNickname());
                    }

                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(MainActivity.this, "网络错误:加载好友列表" + error, Toast.LENGTH_SHORT);
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", "" + globalInfos.getUserId());
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", "" + globalInfos.getUserId());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragmentId = savedInstanceState.getInt("currentFragmentId");
        globalInfos.setUserId(savedInstanceState.getInt("userId"));
        Log.e(TAG, "onRestoreInstanceState currentFragmentId:"+currentFragmentId);
        Log.e(TAG, "fragments size:"+fragments.size()+ " currentFragmentId:"+currentFragmentId);
        replaceFragment(fragments.get(currentFragmentId));
        setTitle(titles.get(currentFragmentId));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragmentId", currentFragmentId);
        outState.putInt("userId", globalInfos.getUserId());
        //outState.putParcelable("user", globalInfos.getUser());
        Log.e(TAG, "onSaveInstanceState currentFragmentId:" + currentFragmentId);
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
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        currentFragmentId = id;
        Log.e(TAG, "onNavigationItemSelected currentFragmentId:"+currentFragmentId+", "+titles.get(id));

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
        } else if (id == R.id.teach) {
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        } else if (id == R.id.me) {
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        } else if (id == R.id.test){
            setTitle(titles.get(id));
            replaceFragment(fragments.get(id));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
