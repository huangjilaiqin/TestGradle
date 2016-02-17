package com.lessask.show;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.captain_miao.recyclerviewutils.EndlessRecyclerOnScrollListener;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.MainActivity;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.ImprovedSwipeLayout;
import com.lessask.recyclerview.RecycleViewScrollListener;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by huangji on 2015/9/16.
 * 展示动态fragment
 */
public class FragmentShow extends Fragment implements View.OnClickListener {

    private final String TAG = FragmentShow.class.getName();
    private View mRootView;
    private ShowListAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private ImprovedSwipeLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private int newShowId;
    private int oldShowId;
    private int pageNum = 10;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private final int GETPICTURE_REQUEST = 100;

    private boolean loadBackward = false;
    private RecycleViewScrollListener recycleViewScrollListener;

    public void setRecycleViewScrollListener(RecycleViewScrollListener recycleViewScrollListener) {
        this.recycleViewScrollListener = recycleViewScrollListener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_show, null);
            //mRecyclerView = (RecyclerViewInSwipeRefreshStatusSupport) mRootView.findViewById(R.id.show_list);
            mRecyclerView = (RecyclerViewStatusSupport) mRootView.findViewById(R.id.show_list);
            mRecyclerView.setStatusViews(mRootView.findViewById(R.id.loading_view), mRootView.findViewById(R.id.empty_view), mRootView.findViewById(R.id.error_view));
            //用线性的方式显示listview
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mSwipeRefreshLayout = (ImprovedSwipeLayout) mRootView.findViewById(R.id.swiperefresh);

            mRecyclerViewAdapter = new ShowListAdapter(getActivity());
            mRecyclerViewAdapter.setHasMoreData(true);
            mRecyclerViewAdapter.setHasFooter(false);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (recycleViewScrollListener != null) {
                        recycleViewScrollListener.onRecycleViewScroll(recyclerView, dx, dy);
                    }
                }
            });

            mSwipeRefreshLayout.setColorSchemeResources(R.color.line_color_run_speed_13);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //下拉刷新
                    Type type = new TypeToken<ArrayListResponse<ShowTime>>() {}.getType();
                    GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onResponse(ArrayListResponse response) {
                            if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                                //Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                                mRecyclerView.showErrorView(response.getError());
                            }else {
                                ArrayList<ShowTime> showdatas = response.getDatas();
                                //最新状态
                                mSwipeRefreshLayout.setRefreshing(false);
                                for(int i=showdatas.size()-1;i>=0;i--){
                                    mRecyclerViewAdapter.appendToTop(showdatas.get(i));
                                }
                                if(showdatas.size()>0){
                                    newShowId = showdatas.get(0).getId();
                                    mRecyclerViewAdapter.notifyDataSetChanged();
                                    //Log.e(TAG, "newShowId:"+newShowId);
                                }else if(showdatas.size()==0 && mRecyclerViewAdapter.getItemCount()==0) {
                                    Log.e(TAG, "showEmptyView");
                                    mRecyclerView.showEmptyView();
                                }
                                mRecyclerView.scrollToPosition(0);

                                //mRecyclerViewAdapter.appendToList((List)response.getDatas());
                                //mRecyclerViewAdapter.notifyDataSetChanged();
                            }


                        }

                        @Override
                        public void onError(VolleyError error) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(FragmentShow.this.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void setPostData(Map datas) {
                            datas.put("userId", "" + globalInfos.getUserId());
                            datas.put("id", "" + newShowId);
                            datas.put("direct", "forward");
                            datas.put("pagenum", "" + pageNum);

                        }

                        @Override
                        public Map getPostData() {
                            Map datas = new HashMap();
                            datas.put("userId", "" + globalInfos.getUserId());
                            datas.put("id", "" + newShowId);
                            datas.put("direct", "forward");
                            datas.put("pagenum", "" + pageNum);
                            return datas;
                        }
                    });
                    VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
                }
            });
            mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {

                @Override
                public void onLoadMore(int current_page) {
                    //上拉加载
                    //不用将footer隐藏, 因为这个控件是通过item个数来判断是否进行下一次加载,目前发现好像不是很可靠
                    //不用担心footer被看见，因为加载成功后footer就在后面了,当它出现时就是进行下一次加载的时候了
                    //to do 加载失败怎么办
                    if(loadBackward){
                        Log.e(TAG, "loadBackward ing");
                        //return;
                    }else {
                        loadBackward = true;
                        mRecyclerViewAdapter.setHasFooter(true);
                        Type type = new TypeToken<ArrayListResponse<ShowTime>>() {}.getType();
                        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
                        //GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowUrl(), GetShowResponse.class, new GsonRequest.PostGsonRequest<GetShowResponse>() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onResponse(ArrayListResponse response) {
                                Log.e(TAG, "get show loadmore response");
                                loadBackward = false;

                                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                                    //Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                                    mRecyclerView.showErrorView(response.getError());
                                }else {
                                    ArrayList<ShowTime> showdatas = response.getDatas();
                                    //历史状态
                                    int position = mRecyclerViewAdapter.getItemCount();
                                    if (showdatas.size() == 0) {
                                        if (mRecyclerViewAdapter.getItemCount() == 0) {
                                            Log.e(TAG, "showEmptyView");
                                            mRecyclerView.showEmptyView();
                                        } else {
                                            mRecyclerViewAdapter.setHasMoreDataAndFooter(false, true);
                                        }
                                        return;
                                    }
                                    mRecyclerViewAdapter.appendToList(showdatas);

                                    if (showdatas.size() > 0) {
                                        ShowTime showTime = showdatas.get(showdatas.size() - 1);
                                        oldShowId = showTime.getId();
                                        showTime = showdatas.get(0);
                                        newShowId = showTime.getId() > newShowId ? showTime.getId() : newShowId;
                                        mRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                    Log.e(TAG, "loadMore is back " + showdatas.size());
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                loadBackward = false;
                                Toast.makeText(getContext(), "网络错误" + error, Toast.LENGTH_SHORT);
                                mRecyclerViewAdapter.setHasFooter(false);
                                mRecyclerViewAdapter.setHasFooter(true);
                            }

                            @Override
                            public void setPostData(Map datas) {
                                datas.put("userId", "" + globalInfos.getUserId());
                                datas.put("id", "" + oldShowId);
                                datas.put("direct", "backward");
                                datas.put("pagenum", "" + pageNum);
                            }

                            @Override
                            public Map getPostData() {
                                Map datas = new HashMap();
                                datas.put("userId", "" + globalInfos.getUserId());
                                datas.put("id", "" + oldShowId);
                                datas.put("direct", "backward");
                                datas.put("pagenum", "" + pageNum);
                                return datas;
                            }
                        });
                        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
                    }
                }
            });
            //获取初始化数据
            Type type = new TypeToken<ArrayListResponse<ShowTime>>() {}.getType();
            GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onResponse(ArrayListResponse response) {
                    if(response.getError()!=null || response.getErrno()!=0){
                        return;
                    }else {
                        ArrayList<ShowTime> showdatas = response.getDatas();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mRecyclerViewAdapter.appendToTopList(showdatas);

                        if (showdatas.size() > 0) {
                            newShowId = showdatas.get(0).getId();
                            oldShowId = showdatas.get(showdatas.size() - 1).getId();
                            mRecyclerViewAdapter.notifyDataSetChanged();
                            //Log.e(TAG, "newShowId:"+newShowId);
                        }else if(showdatas.size()==0){
                            if(mRecyclerViewAdapter.getItemCount()==0) {
                                mRecyclerView.showEmptyView();
                            }
                        }
                        mRecyclerView.scrollToPosition(0);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    mRecyclerView.showErrorView(error.toString());
                }

                @Override
                public void setPostData(Map datas) {
                    datas.put("userId", "" + globalInfos.getUserId());
                    datas.put("pagenum", ""+4);
                }

                @Override
                public Map getPostData() {
                    Map datas = new HashMap();
                    datas.put("userId", "" + globalInfos.getUserId());
                    datas.put("pagenum", ""+4);
                    return datas;
                }
            });
            VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
        }
        return mRootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.up:
                ImageView view = (ImageView)v;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case MainActivity.CREATE_SHOW:
                    Log.e(TAG, "发布状态成功");
                    ShowTime showTime = data.getParcelableExtra("showTime");
                    for(int i=0;i<showTime.getPictures().size();i++){
                        Log.e(TAG, showTime.getPictures().get(i));
                    }
                    mRecyclerViewAdapter.appendToTop(showTime);
                    mRecyclerViewAdapter.notifyItemInserted(0);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    Log.e(TAG, "add show:"+showTime.getId());
                    newShowId = showTime.getId();
                    break;
            }
        }
    }
}
