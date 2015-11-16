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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ShowItem;
import com.lessask.model.Utils;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;

/**
 * Created by huangji on 2015/9/16.
 * 展示动态fragment
 */
public class FragmentShow extends Fragment implements View.OnClickListener {

    private final String TAG = FragmentShow.class.getName();
    private View mRootView;
    private ShowListAdapter1 mShowListAdapter1;
    private RecyclerView mShowList;
    private ArrayList showItems;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private int REQUEST_CODE = 100;
    private final int HANDLER_GETSHOW_START = 1;
    private final int HANDLER_GETSHOW_DONE = 2;

    private ImageView ivUp;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "register handler:" + msg.what);
            switch (msg.what) {
                case HANDLER_GETSHOW_START:
                    Toast.makeText(getActivity(), "请求数据", Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_GETSHOW_DONE:
                    int statusCode = msg.arg1;
                    ArrayList<ShowItem> showdatas = (ArrayList<ShowItem>)msg.obj;
                    if(statusCode==200){
                        for(int i=0;i<showdatas.size();i++){
                            showItems.add(0,showdatas.get(i));
                        }
                        mShowListAdapter1.notifyDataSetChanged();
                    }else {

                    }
                    break;
            }
        }
    };

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
            msg.what = HANDLER_GETSHOW_DONE;
            if(response!=null) {
                msg.arg1 = response.getCode();
                //to do 动态条目 module类
                Log.e(TAG, response.getBody());
                ArrayList<ShowItem> showdatas = gson.fromJson(response.getBody(), new TypeToken<List<ShowItem>>() {
                }.getType());
                msg.obj = showdatas;
            }else {
                msg.arg1 = -1;
            }
            handler.sendMessage(msg);
        }
    };
    private PostSingle postSingle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_show, null);
            mShowList = (RecyclerView) mRootView.findViewById(R.id.show_list);
            //用线性的方式显示listview
            mShowList.setLayoutManager(new LinearLayoutManager(getActivity()));

            //获取数据状态数据
            postSingle = new PostSingle(config.getGetShowUrl(), postSingleEvent);
            HashMap<String, String> requestArgs = new HashMap<>();
            requestArgs.put("userid", "" + globalInfos.getUserid());
            postSingle.setHeaders(requestArgs);
            postSingle.start();

            showItems = new ArrayList();
            mShowListAdapter1 = new ShowListAdapter1(getActivity(), showItems);
            mShowList.setAdapter(mShowListAdapter1);

            ivUp = (ImageView) mRootView.findViewById(R.id.up);

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
}
