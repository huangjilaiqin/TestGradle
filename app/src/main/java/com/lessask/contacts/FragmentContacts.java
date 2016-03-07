package com.lessask.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.chat.ChatActivity;
import com.lessask.chat.ChatGroup;
import com.lessask.crud.CRUDExtend;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.User;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangji on 2015/11/24.
 */
public class FragmentContacts extends Fragment implements Toolbar.OnMenuItemClickListener {
    private View rootView;
    private String TAG = FragmentContacts.class.getSimpleName();

    private FloatingActionButton mSearch;
    private RecyclerViewStatusSupport mRecyclerView;
    private ContactsAdapter mRecyclerViewAdapter;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    private DrawerLayout mDrawerLayout;
    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_contacts, null);
            Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            mToolbar.setTitle("通信录");
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            });
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });
            mToolbar.inflateMenu(R.menu.menu_contacts);
            mSearch = (FloatingActionButton)rootView.findViewById(R.id.search);
            mSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "search", Toast.LENGTH_SHORT).show();
                }
            });

            mRecyclerView = (RecyclerViewStatusSupport) rootView.findViewById(R.id.show_list);
            mRecyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            //用线性的方式显示listview
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

            mRecyclerViewAdapter = new ContactsAdapter(getContext());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    User user = mRecyclerViewAdapter.getItem(position);
                    int friendId = user.getUserid();
                    int userid = globalInfos.getUserId();
                    String chatgroupId = userid<friendId?userid+"_"+friendId:friendId+"_"+userid;
                    ChatGroup chatGroup = new ChatGroup(chatgroupId, user.getNickname());
                    intent.putExtra("chatGroup", chatGroup);

                    //查看是否存在聊天列表
                    SQLiteDatabase db = DbHelper.getInstance(getContext()).getDb();
                    Cursor cursor = db.rawQuery("select 1 from t_chatgroup where chatgroup_id=?", new String[]{chatgroupId});
                    if(!cursor.moveToNext())
                        intent.putExtra("notInContacts", true);
                    cursor.close();
                    startActivity(intent);
                }
            });

            mRecyclerView.showLoadingView();
            loadContact();
        }
        return rootView;
    }
    private void loadContact(){
        mRecyclerView.showLoadingView();
        SQLiteDatabase db = DbHelper.getInstance(getContext()).getDb();
        Cursor cursor = db.rawQuery("select * from t_contact", null);
        while (cursor.moveToNext()){
            mRecyclerViewAdapter.append(new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            Log.e(TAG, "name:"+cursor.getString(1));
        }
        int count = cursor.getColumnCount();
        Log.e(TAG, "query db, chatgroup size:"+count);
        if(count==0){
            mRecyclerView.showEmptyView();
        }else {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contacts, menu);
        menu.add("Menu 1a").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Toast.makeText(getContext(), "add friend", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

}
