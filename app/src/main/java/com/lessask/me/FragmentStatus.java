package com.lessask.me;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.captain_miao.recyclerviewutils.EndlessRecyclerOnScrollListener;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.crud.CRUD;
import com.lessask.crud.DefaultGsonRequestCRUD;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.Workout;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.ImprovedSwipeLayout;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.show.ShowListAdapter;
import com.lessask.show.ShowTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHuang on 2015/10/22.
 */
public class FragmentStatus extends Fragment{
    private String TAG = FragmentStatus.class.getSimpleName();
    private View rootView;
    private ShowListAdapter mRecyclerViewAdapter;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private boolean loadBackward = false;
    private int newShowId;
    private int oldShowId;
    private int pageNum = 10;
    private ImprovedSwipeLayout mSwipeRefreshLayout;
    private RecyclerViewStatusSupport mRecyclerView;
    private int interestUserid;

    public void setInsterestUserid(int interestUserid) {
        this.interestUserid = interestUserid;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_status, null);

            mRecyclerView = (RecyclerViewStatusSupport)rootView.findViewById(R.id.list);
            mSwipeRefreshLayout = (ImprovedSwipeLayout) rootView.findViewById(R.id.swiperefresh);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerViewAdapter = new ShowListAdapter(getActivity());
            mRecyclerViewAdapter.setHasMoreData(true);
            mRecyclerViewAdapter.setHasFooter(false);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            rootView.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    load();
                }
            });
            mSwipeRefreshLayout.setColorSchemeResources(R.color.line_color_run_speed_13);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //下拉刷新
                    Type type = new TypeToken<ArrayListResponse<ShowTime>>() {
                    }.getType();
                    GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowByUseridUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onResponse(ArrayListResponse response) {
                            if (response.getError() != null && response.getError() != "" || response.getErrno() != 0) {
                                //Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                                mRecyclerView.showErrorView(response.getError());
                            } else {
                                ArrayList<ShowTime> showdatas = response.getDatas();
                                //最新状态
                                mSwipeRefreshLayout.setRefreshing(false);
                                for (int i = showdatas.size() - 1; i >= 0; i--) {
                                    mRecyclerViewAdapter.appendToTop(showdatas.get(i));
                                }
                                if (showdatas.size() > 0) {
                                    newShowId = showdatas.get(0).getId();
                                    mRecyclerViewAdapter.notifyDataSetChanged();
                                    //Log.e(TAG, "newShowId:"+newShowId);
                                } else if (showdatas.size() == 0 && mRecyclerViewAdapter.getItemCount() == 0) {
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
                            Toast.makeText(FragmentStatus.this.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void setPostData(Map datas) {
                            datas.put("userid", "" + globalInfos.getUserId());
                            datas.put("interestUserid", ""+interestUserid);
                            datas.put("id", "" + newShowId);
                            datas.put("direct", "forward");
                            datas.put("pagenum", "" + pageNum);

                        }

                        @Override
                        public Map getPostData() {
                            Map datas = new HashMap();
                            datas.put("userid", "" + globalInfos.getUserId());
                            datas.put("interestUserid", ""+interestUserid);
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
                        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowByUseridUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
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
                                datas.put("interestUserid", ""+interestUserid);
                                datas.put("id", "" + oldShowId);
                                datas.put("direct", "backward");
                                datas.put("pagenum", "" + pageNum);
                            }

                            @Override
                            public Map getPostData() {
                                Map datas = new HashMap();
                                datas.put("userId", "" + globalInfos.getUserId());
                                datas.put("interestUserid", ""+interestUserid);
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
            load();

        }
        return rootView;
    }
    private void load(){
        //获取初始化数据
            Type type = new TypeToken<ArrayListResponse<ShowTime>>() {}.getType();
            GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowByUseridUrl(), type, new GsonRequest.PostGsonRequest<ArrayListResponse>() {
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
                    datas.put("userid", "" + globalInfos.getUserId());
                    datas.put("interestUserid", interestUserid + "");
                    datas.put("pagenum", ""+4);
                }

                @Override
                public Map getPostData() {
                    Map datas = new HashMap();
                    datas.put("userid", "" + globalInfos.getUserId());
                    datas.put("interestUserid", interestUserid + "");
                    datas.put("pagenum", ""+pageNum);
                    return datas;

                }
            });
            VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
}
