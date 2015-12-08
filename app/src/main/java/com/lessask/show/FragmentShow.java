package com.lessask.show;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.captain_miao.recyclerviewutils.EndlessRecyclerOnScrollListener;
import com.google.gson.Gson;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.GetShowResponse;
import com.lessask.model.ShowItem;
import com.lessask.model.Utils;
import com.lessask.net.NetFragment;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;
import com.lessask.test.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import me.iwf.photopicker.PhotoPickerActivity;

/**
 * Created by huangji on 2015/9/16.
 * 展示动态fragment
 */
public class FragmentShow extends NetFragment implements View.OnClickListener {

    private final String TAG = FragmentShow.class.getName();
    private View mRootView;
    private ShowListAdapter mRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private int newShowId;
    private int oldShowId;
    private int pageNum = 1;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private int REQUEST_CODE = 100;
    private final int HANDLER_GETSHOW_START = 1;
    private final int HANDLER_GETSHOW_DONE = 2;

    private final int GETSHOWS_INIT = 1;
    private final int GETSHOWS_FORWARD = 2;
    private final int GETSHOWS_BACKWORD = 3;

    private boolean loadBackward = false;

    private ImageView ivUp;

    private PostSingle postSingle;
    private SimpleAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_show, null);
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.show_list);
            //用线性的方式显示listview
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefresh);

            mRecyclerViewAdapter = new ShowListAdapter(getActivity());
            mRecyclerViewAdapter.setHasMoreData(true);
            mRecyclerViewAdapter.setHasFooter(false);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            //获取初始化数据
            startPost(config.getGetShowUrl(), GETSHOWS_INIT, GetShowResponse.class);

            ivUp = (ImageView) mRootView.findViewById(R.id.up);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.line_color_run_speed_13);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //下拉刷新
                    startPost(config.getGetShowUrl(), GETSHOWS_FORWARD, GetShowResponse.class);
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

                        startPost(config.getGetShowUrl(), GETSHOWS_BACKWORD, GetShowResponse.class);
                    }
                }
            });
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
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
                startActivity(intent);
            }
        }
    }

    @Override
    public void onStart(int requestCode) {

    }

    @Override
    public void onDone(int requestCode, Object response) {
        GetShowResponse getShowResponse = (GetShowResponse)response;
        ArrayList<ShowItem> showdatas = getShowResponse.getShowdatas();
        String direct = getShowResponse.getDirect();
        switch (requestCode){
            case GETSHOWS_INIT:
            case GETSHOWS_FORWARD:
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
                break;
            case GETSHOWS_BACKWORD:
                loadBackward = false;
                //历史状态
                int position = mRecyclerViewAdapter.getItemCount();
                if(showdatas.size()==0){
                    mRecyclerViewAdapter.setHasMoreDataAndFooter(false, true);
                    return;
                }
                for(int i=0;i<showdatas.size();i++){
                    mRecyclerViewAdapter.append(showdatas.get(i));
                }
                if(showdatas.size()>0) {
                    ShowItem showItem = showdatas.get(showdatas.size() - 1);
                    oldShowId = showItem.getId();
                    showItem = showdatas.get(0);
                    newShowId = showItem.getId()>newShowId?showItem.getId():newShowId;

                    //Log.e(TAG, "oldShowId:" + oldShowId + " newShowId:" + newShowId);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    //mRecyclerView.scrollToPosition(position);
                }
                Log.e(TAG, "loadMore is back "+showdatas.size());
                break;
        }
    }

    @Override
    public void onError(int requestCode, String error) {
        Toast.makeText(getContext(), "网络错误"+error, Toast.LENGTH_SHORT);
        Log.e(TAG, "loadMore is error");
        mSwipeRefreshLayout.setRefreshing(false);
        loadBackward = false;
    }

    @Override
    public void postData(int requestCode, Map headers, Map files) {
        switch (requestCode){
            case GETSHOWS_INIT:
                headers.put("userid", "" + globalInfos.getUserid());
                headers.put("pagenum", ""+4);
                break;
            case GETSHOWS_FORWARD:
                headers.put("userid", "" + globalInfos.getUserid());
                headers.put("id", ""+newShowId);
                headers.put("direct", "forward");
                headers.put("pagenum", ""+pageNum);
                break;
            case GETSHOWS_BACKWORD:
                headers.put("userid", "" + globalInfos.getUserid());
                headers.put("id", "" + oldShowId);
                headers.put("direct", "backward");
                headers.put("pagenum", "" + pageNum);
                break;
        }
    }
}
