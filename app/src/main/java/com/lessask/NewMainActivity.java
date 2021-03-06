package com.lessask;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lessask.chat.Chat;
import com.lessask.chat.ChatGroup;
import com.lessask.chat.FragmentMessage;
import com.lessask.contacts.ContactActivity;
import com.lessask.contacts.FindFriendActivity;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.me.FragmentMe;
import com.lessask.me.FragmentNewMe;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.User;
import com.lessask.model.VerifyToken;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.tag.GetTagsRequest;
import com.lessask.tag.GetTagsResponse;
import com.lessask.tag.TagData;
import com.lessask.tag.TagNet;
import com.lessask.test.CoordinatorLayout2Activity;
import com.lessask.test.CoordinatorLayoutActivity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewMainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private Gson gson = new Gson();
    private SharedPreferences baseInfo;
    private SharedPreferences.Editor editor;
    private String TAG = NewMainActivity.class.getSimpleName();
    private Chat chat = Chat.getInstance(getBaseContext());
    private FragmentWorkout fragmentWorkout;
    private FragmentMessage fragmentMessage;
    private FragmentNewMe fragmentMe;
    private int currentFragmentId;
    private Map<Integer,Fragment> fragments=new HashMap<>();

    private View addFriend;
    private Menu menu;

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("少问");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        fragmentWorkout = new FragmentWorkout();
        myFragmentPagerAdapter.addFragment(fragmentWorkout, "训练");
        fragmentMessage = new FragmentMessage();
        myFragmentPagerAdapter.addFragment(fragmentMessage, "消息");
        fragmentMe = new FragmentNewMe();
        myFragmentPagerAdapter.addFragment(fragmentMe, "我");
        mViewPager.setAdapter(myFragmentPagerAdapter);

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==1){
                    menu.getItem(0).setVisible(true);
                }else {
                    menu.getItem(0).setVisible(false);
                }
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);
        editor = baseInfo.edit();
        Log.e(TAG, "onCreate");

        chat.setLoadInitDataListener(new Chat.LoadInitDataListener() {
            @Override
            public void loadInitData(String data) {

            }
        });

        //加载数据
        loadBaseData();


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



        loadData();

    }

    //加载基础数据
    private void loadBaseData(){
        Log.e(TAG, "loadBaseData");
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
        chat.emit("loadInitData",gson.toJson(new VerifyToken(globalInfos.getUserId(),globalInfos.getToken())));
        //todo 监听加载数据回调
    }


    private void loadChatGroupsFromDb(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from t_chatgroup", null);
        ArrayList<ChatGroup> chatGroups = new ArrayList<>();
        while (cursor.moveToNext()){
            String chatgroupId = cursor.getString(0);
            String name = cursor.getString(1);
            int status = cursor.getInt(2);
            chatGroups.add(new ChatGroup(chatgroupId,name,status));
        }
        cursor.close();
        globalInfos.addChatGroups(chatGroups);
    }
    private void loadFriendsFromDb(SQLiteDatabase db){

    }
    private void loadUserFromDb(SQLiteDatabase db){
        Log.e(TAG, "userid:"+globalInfos.getUserId());
        Cursor cursor = db.rawQuery("select * from t_user where userid=?", new String[]{"" + globalInfos.getUserId()});
        if(cursor.moveToNext()){
            int userid = cursor.getInt(0);
            String nickname = cursor.getString(1);
            String headImg = cursor.getString(2);
            User user = new User(userid,nickname,headImg);
            globalInfos.setUser(user);
            Log.e(TAG, "setUser");
        }else {
            loadUser(db);
        }
        cursor.close();
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
                Toast.makeText(NewMainActivity.this, "网络错误:加载聊天列表" + error, Toast.LENGTH_SHORT);
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
                Toast.makeText(NewMainActivity.this, "网络错误:加载聊天列表" + error, Toast.LENGTH_SHORT);
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
            public void onStart() {}
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
                Toast.makeText(NewMainActivity.this, "网络错误:加载好友列表" + error, Toast.LENGTH_SHORT);
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
    private void loadData(){
        TagNet mTagNet = TagNet.getInstance();
        mTagNet.setGetTagsListener(getTagsListener);
        GetTagsRequest request = new GetTagsRequest(globalInfos.getUserId());
        mTagNet.emit("gettags", gson.toJson(request));
    }
    private TagNet.GetTagsListener getTagsListener = new TagNet.GetTagsListener() {
        @Override
        public void getTagsResponse(GetTagsResponse response) {
            ArrayList<TagData> allTags = response.getTagDatas();
            globalInfos.getActionTagsHolder().setActionTags(allTags);
            Log.e(TAG, "gettags resp size:" + allTags.size());
        }

    };
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragmentId = savedInstanceState.getInt("currentFragmentId");
        replaceFragment(fragments.get(currentFragmentId));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragmentId", currentFragmentId);
        outState.putInt("userId", globalInfos.getUserId());
        Log.e(TAG, "onSaveInstanceState currentFragmentId:" + currentFragmentId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()){
            case R.id.contact:
                intent = new Intent(NewMainActivity.this, ContactActivity.class);
                startActivity(intent);
                break;
            case R.id.add_friend:
                intent = new Intent(NewMainActivity.this, FindFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.test:
                intent = new Intent(NewMainActivity.this, CoordinatorLayout2Activity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}












