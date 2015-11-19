package com.lessask.test;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import com.github.captain_miao.recyclerviewutils.EndlessRecyclerOnScrollListener;
import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.GetShowResponse;
import com.lessask.model.ShowItem;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class SwipeRefreshAndLoadMoreActivity extends ActionBarActivity {

    private SimpleAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private final int HANDLER_GETSHOW_DONE = 2;
    private final int HANDLER_GETSHOW_START = 1;
    private String TAG = SwipeRefreshAndLoadMoreActivity.class.getSimpleName();
    private PostSingle postSingle;
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private int newShowId;
    private int oldShowId;
    private int pageNum = 1;

    private PostSingleEvent postSingleEvent = new PostSingleEvent() {
        @Override
        public void onStart() {
            Message msg = new Message();
            msg.what = HANDLER_GETSHOW_START;
            handler.sendMessage(msg);
        }

        @Override
        public void onDone(boolean success, PostResponse response) {
            Message msg = new Message();
            GetShowResponse getShowResponse = gson.fromJson(response.getBody(), GetShowResponse.class);
            msg.obj = getShowResponse;
            msg.what = HANDLER_GETSHOW_DONE;
            handler.sendMessage(msg);
        }
    };
    private PostSingleEvent postSingleEvent1 = new PostSingleEvent() {
        @Override
        public void onStart() {
            Message msg = new Message();
            msg.what = HANDLER_GETSHOW_START;
            handler.sendMessage(msg);
        }

        @Override
        public void onDone(boolean success, PostResponse response) {
            Message msg = new Message();
            msg.what = HANDLER_GETSHOW_START;
            handler.sendMessage(msg);
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_GETSHOW_START:
                    break;
                case HANDLER_GETSHOW_DONE:
                    int statusCode = msg.arg1;

                    if(statusCode==200){
                        GetShowResponse getShowResponse = (GetShowResponse)msg.obj;
                        ArrayList<ShowItem> showdatas = getShowResponse.getShowdatas();
                        String direct = getShowResponse.getDirect();

                        if(direct.equals("backward")){
                            mAdapter.setHasFooter(false);
                            //历史状态
                            int position = mAdapter.getItemCount();
                            if(showdatas.size()==0){
                                mAdapter.setHasMoreDataAndFooter(false, true);
                                return;
                            }
                            for(int i=0;i<showdatas.size();i++){
                                mAdapter.append("" + i);
                            }
                            if(showdatas.size()>0) {
                                ShowItem showItem = showdatas.get(showdatas.size() - 1);
                                oldShowId = showItem.getId();
                                showItem = showdatas.get(0);
                                newShowId = showItem.getId()>newShowId?showItem.getId():newShowId;

                                //Log.e(TAG, "oldShowId:" + oldShowId + " newShowId:" + newShowId);
                                mAdapter.notifyDataSetChanged();
                                //mRecyclerView.scrollToPosition(position);
                            }
                            Log.e(TAG, "loadMoreStatus is back "+showdatas.size());
                        }else{
                            //最新状态
                            mSwipeRefreshLayout.setRefreshing(false);
                            for(int i=showdatas.size()-1;i>=0;i--){
                                mAdapter.appendToTop("" + i);
                            }
                            if(showdatas.size()>0){
                                newShowId = showdatas.get(0).getId();
                                mAdapter.notifyDataSetChanged();
                                //Log.e(TAG, "newShowId:"+newShowId);
                            }
                            mRecyclerView.scrollToPosition(0);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT);
                        Log.e(TAG, "loadMoreStatus is error");
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.setHasFooter(false);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_refresh_and_load_more);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView =  (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SimpleAdapter(values);
        mAdapter.setHasMoreData(false);
        mAdapter.setHasFooter(false);
        mRecyclerView.setAdapter(mAdapter);

        postSingle = new PostSingle(config.getGetShowUrl(), postSingleEvent);
        HashMap<String, String> requestArgs = new HashMap<>();
        requestArgs.put("userid", "" + globalInfos.getUserid());
        requestArgs.put("pagenum", ""+4);
        postSingle.setHeaders(requestArgs);
        postSingle.start();


        //设置加载圈圈的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.line_color_run_speed_13);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postSingle = new PostSingle(config.getGetShowUrl(), postSingleEvent);
                HashMap<String, String> requestArgs = new HashMap<>();
                requestArgs.put("userid", "" + globalInfos.getUserid());
                requestArgs.put("id", ""+newShowId);
                requestArgs.put("direct", "forward");
                requestArgs.put("pagenum", ""+pageNum);
                Log.e(TAG, requestArgs.toString());
                postSingle.setHeaders(requestArgs);
                postSingle.start();
            }
        });


        mAdapter.setHasMoreData(true);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                mAdapter.setHasFooter(true);
                postSingle = new PostSingle(config.getGetShowUrl(), postSingleEvent);
                HashMap<String, String> requestArgs = new HashMap<>();
                requestArgs.put("userid", "" + globalInfos.getUserid());
                requestArgs.put("id", "" + oldShowId);
                requestArgs.put("direct", "backward");
                requestArgs.put("pagenum", "" + pageNum);
                Log.e(TAG, requestArgs.toString());
                postSingle.setHeaders(requestArgs);
                postSingle.start();
            }
        });
    }


    ArrayList<String> values = new ArrayList<String>() {{
        add("Android");
    }};


}
