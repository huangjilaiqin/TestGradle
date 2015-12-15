package com.lessask.show;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.captain_miao.recyclerviewutils.EndlessRecyclerOnScrollListener;
import com.google.gson.Gson;
import com.lessask.DividerItemDecoration;
import com.lessask.MainActivity;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.GetShowResponse;
import com.lessask.model.ShowItem;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.ImprovedSwipeLayout;
import com.lessask.recyclerview.RecyclerViewInSwipeRefreshStatusSupport;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by huangji on 2015/9/16.
 * 展示动态fragment
 */
public class FragmentShow extends Fragment implements View.OnClickListener {

    private final String TAG = FragmentShow.class.getName();
    private View mRootView;
    private ShowListAdapter mRecyclerViewAdapter;
    //private RecyclerViewInSwipeRefreshStatusSupport mRecyclerView;
    private RecyclerViewStatusSupport mRecyclerView;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImprovedSwipeLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private int newShowId;
    private int oldShowId;
    private int pageNum = 10;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private final int GETPICTURE_REQUEST = 100;

    private boolean loadBackward = false;


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
            //mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);
            mSwipeRefreshLayout = (ImprovedSwipeLayout) mRootView.findViewById(R.id.swiperefresh);

            mRecyclerViewAdapter = new ShowListAdapter(getActivity());
            mRecyclerViewAdapter.setHasMoreData(true);
            mRecyclerViewAdapter.setHasFooter(false);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            mSwipeRefreshLayout.setColorSchemeResources(R.color.line_color_run_speed_13);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //下拉刷新
                    GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowUrl(), GetShowResponse.class, new GsonRequest.PostGsonRequest<GetShowResponse>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onResponse(GetShowResponse response) {
                            ArrayList<ShowItem> showdatas = response.getShowdatas();
                            //最新状态
                            mSwipeRefreshLayout.setRefreshing(false);
                            for(int i=showdatas.size()-1;i>=0;i--){
                                mRecyclerViewAdapter.appendToTop(showdatas.get(i));
                            }
                            if(showdatas.size()>0){
                                newShowId = showdatas.get(0).getId();
                                mRecyclerViewAdapter.notifyDataSetChanged();
                                //Log.e(TAG, "newShowId:"+newShowId);
                            }
                            mRecyclerView.scrollToPosition(0);

                        }

                        @Override
                        public void onError(VolleyError error) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(FragmentShow.this.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void setPostData(Map datas) {
                            datas.put("userid", "" + globalInfos.getUserid());
                            datas.put("id", "" + newShowId);
                            datas.put("direct", "forward");
                            datas.put("pagenum", "" + pageNum);

                        }
                    });
                    VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
                    //startPost(config.getGetShowUrl(), GETSHOWS_FORWARD, GetShowResponse.class);
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

                        //startPost(config.getGetShowUrl(), GETSHOWS_BACKWORD, GetShowResponse.class);
                        GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowUrl(), GetShowResponse.class, new GsonRequest.PostGsonRequest<GetShowResponse>() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onResponse(GetShowResponse response) {
                                loadBackward = false;

                                ArrayList<ShowItem> showdatas = response.getShowdatas();
                                //历史状态
                                int position = mRecyclerViewAdapter.getItemCount();
                                if(showdatas.size()==0){
                                    mRecyclerViewAdapter.setHasMoreDataAndFooter(false, true);
                                    return;
                                }
                                mRecyclerViewAdapter.appendToList(showdatas);
                                /*
                                for(int i=0;i<showdatas.size();i++){
                                    mRecyclerViewAdapter.append(showdatas.get(i));
                                }
                                */
                                if(showdatas.size()>0) {
                                    ShowItem showItem = showdatas.get(showdatas.size() - 1);
                                    oldShowId = showItem.getId();
                                    showItem = showdatas.get(0);
                                    newShowId = showItem.getId()>newShowId?showItem.getId():newShowId;

                                    mRecyclerViewAdapter.notifyDataSetChanged();
                                }
                                Log.e(TAG, "loadMore is back "+showdatas.size());
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
                                datas.put("userid", "" + globalInfos.getUserid());
                                datas.put("id", "" + oldShowId);
                                datas.put("direct", "backward");
                                datas.put("pagenum", "" + pageNum);
                            }
                        });
                        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
                    }
                }
            });
            //获取初始化数据
            GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getGetShowUrl(), GetShowResponse.class, new GsonRequest.PostGsonRequest<GetShowResponse>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onResponse(GetShowResponse response) {

                    ArrayList<ShowItem> showdatas = response.getShowdatas();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mRecyclerViewAdapter.appendToTopList(showdatas);
                    /*
                    for(int i=showdatas.size()-1;i>=0;i--){
                        mRecyclerViewAdapter.appendToTop(showdatas.get(i));
                    }
                    */
                    if(showdatas.size()>0){
                        newShowId = showdatas.get(0).getId();
                        oldShowId = showdatas.get(showdatas.size()-1).getId();
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        //Log.e(TAG, "newShowId:"+newShowId);
                    }
                    mRecyclerView.scrollToPosition(0);
                }

                @Override
                public void onError(VolleyError error) {
                    mRecyclerView.showErrorView(error.toString());
                }

                @Override
                public void setPostData(Map datas) {
                    datas.put("userid", "" + globalInfos.getUserid());
                    datas.put("pagenum", ""+4);
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
                    Toast.makeText(getContext(), "发布状态成功", Toast.LENGTH_SHORT).show();
                    ShowItem showItem = data.getParcelableExtra("showItem");
                    for(int i=0;i<showItem.getPictures().size();i++){
                        Log.e(TAG, showItem.getPictures().get(i));
                    }
                    mRecyclerViewAdapter.appendToTop(showItem);
                    mRecyclerViewAdapter.notifyItemInserted(0);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                /*
                case GETPICTURE_REQUEST:
                    ArrayList<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    for(int i=0;i<photos.size();i++) {
                        String originFileStr = photos.get(i);
                        File originFile = new File(originFileStr);

                        //获取缩略图
                        ContentResolver cr = getActivity().getContentResolver();
                        //获取原图id
                        String columns[] = new String[] { MediaStore.Images.Media._ID};
                        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, "_data=?", new String[]{originFileStr}, null);
                        int originImgId = 0;
                        if(cursor.moveToFirst()){
                            originImgId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        }


                        String[] projection = { MediaStore.Images.Thumbnails.DATA};
                        cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, "image_id=?", new String[]{originImgId+""}, null);
                        String thumbnailPath = "";
                        String thumbData = "";
                        Bitmap thumbnailBitmap = null;
                        if(cursor.moveToFirst()){
                            thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                            thumbnailBitmap = Utils.getBitmapFromFile(new File(thumbnailPath));
                        }else {
                            //不存在缩略图
                            //int width = CreateShowActivity.this.getWindowManager().getDefaultDisplay().getWidth();
                            //int height = CreateShowActivity.this.getWindowManager().getDefaultDisplay().getHeight();
                            thumbnailBitmap = Utils.optimizeBitmap(originFile.getAbsolutePath(), 100, 100);
                        }

                        String fileName = originFile.getName();
                        String name = fileName.substring(0, fileName.indexOf("."));
                        String ex = fileName.substring(fileName.indexOf(".") + 1);
                        String newName = name+"_cmp1."+ex;

                        File dir = Environment.getExternalStorageDirectory();
                        dir = new File(dir, "testImage");
                        if(!dir.exists())
                            dir.mkdir();

                        Utils.setBitmapToFile(new File(dir, newName), thumbnailBitmap);

                    }
                    Intent intent = new Intent(getActivity(), CreateShowActivity.class);
                    intent.putStringArrayListExtra("images", photos);
                    intent.putExtra("forResultCode", CREATE_SHOW);
                    startActivityForResult(intent, CREATE_SHOW);
                    break;
                    */

            }
        }
    }
}
