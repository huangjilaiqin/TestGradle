package com.lessask.show;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.lessask.R;
import com.lessask.model.ShowItem;
import com.lessask.model.Utils;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * Created by huangji on 2015/9/16.
 */
public class FragmentShow extends Fragment implements View.OnClickListener {

    private final String TAG = FragmentShow.class.getName();
    private View mRootView;
    private ShowListAdapter mShowListAdapter;
    private ListView mShowList;

    private int REQUEST_CODE = 100;

    private ImageView ivUp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_show, null);
            mShowList = (ListView) mRootView.findViewById(R.id.show_list);
            //获取数据状态数据
            ArrayList showItems = getData();
            mShowListAdapter = new ShowListAdapter(getActivity(), showItems);
            mShowList.setAdapter(mShowListAdapter);

            ivUp = (ImageView) mRootView.findViewById(R.id.up);

        }
        return mRootView;
    }
    private ArrayList getData(){

        ArrayList<ShowItem> showItems = new ArrayList<>();
        ArrayList<String> showImgs1 = new ArrayList<>();
        showImgs1.add(""+R.drawable.runnging);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs1, "今天天气真不错！！！", 89, 23,0));
        ArrayList<String> showImgs2 = new ArrayList<>();
        showImgs2.add(""+R.drawable.runnging);
        showImgs2.add(""+R.drawable.speed);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs2, "今天天气真不错！！！", 89, 23,0));
        ArrayList<String> showImgs3 = new ArrayList<>();
        showImgs3.add(""+R.drawable.runnging);
        showImgs3.add(""+R.drawable.speed);
        showImgs3.add(""+R.drawable.comment);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs3, "今天天气真不错！！！", 89, 23,0));
        ArrayList<String> showImgs4 = new ArrayList<>();
        showImgs4.add(""+R.drawable.runnging);
        showImgs4.add(""+R.drawable.speed);
        showImgs4.add(""+R.drawable.chat);
        showImgs4.add(""+R.drawable.runnging);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs4, "今天天气真不错！！！", 89, 23,0));
        return showItems;
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
