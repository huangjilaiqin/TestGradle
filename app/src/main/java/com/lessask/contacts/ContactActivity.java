package com.lessask.contacts;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.chat.ChatGroup;
import com.lessask.chat.MyChatActivity;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.User;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.util.List;

public class ContactActivity extends AppCompatActivity {


    private String TAG = ContactActivity.class.getSimpleName();
    private FloatingActionButton mSearch;
    private RecyclerViewStatusSupport mRecyclerView;
    private ContactsAdapter mRecyclerViewAdapter;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("通讯录");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearch = (FloatingActionButton)findViewById(R.id.search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactActivity.this, "search", Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView = (RecyclerViewStatusSupport) findViewById(R.id.show_list);
        mRecyclerView.setStatusViews(findViewById(R.id.loading_view), findViewById(R.id.empty_view), findViewById(R.id.error_view));
        //用线性的方式显示listview
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerViewAdapter = new ContactsAdapter(this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Intent intent = new Intent(ContactActivity.this, MyChatActivity.class);
                User user = mRecyclerViewAdapter.getItem(position);
                int friendId = user.getUserid();
                int userid = globalInfos.getUserId();
                String chatgroupId = userid<friendId?userid+"_"+friendId:friendId+"_"+userid;
                ChatGroup chatGroup = new ChatGroup(chatgroupId, user.getNickname());

                //查看是否存在聊天列表
                if(!globalInfos.hasChatGroupId(chatgroupId))
                    intent.putExtra("notInContacts", true);
                else {
                    //加载聊天信息
                    List<ChatMessage> list = DbHelper.getChatMessage(chatgroupId, 10);
                    chatGroup.appendList(list);
                }
                intent.putExtra("chatGroup", chatGroup);
                startActivity(intent);
            }
        });

        mRecyclerView.showLoadingView();
        loadContact();
    }

    private void loadContact(){
        mRecyclerView.showLoadingView();
        SQLiteDatabase db = DbHelper.getInstance(this).getDb();
        Cursor cursor = db.rawQuery("select * from t_contact", null);
        Log.e(TAG, "query contact size:" + cursor.getCount());
        while (cursor.moveToNext()){
            mRecyclerViewAdapter.append(new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            Log.e(TAG, "name:"+cursor.getString(1));
        }
        int count = cursor.getColumnCount();
        Log.e(TAG, "query db, chatgroup size:" + count);
        if(count==0){
            mRecyclerView.showEmptyView();
        }else {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Toast.makeText(this, "add friend", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
