package com.lessask;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

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
    private ImageView mCreate;

    private int REQUEST_CODE = 100;

    private ImageView ivUp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_show, null);
            mShowList = (ListView) mRootView.findViewById(R.id.show_list);
            //获取数据状态数据
            ArrayList showItems = getData();
            mShowListAdapter = new ShowListAdapter(getActivity(), showItems);
            mShowList.setAdapter(mShowListAdapter);

            ivUp = (ImageView) mRootView.findViewById(R.id.up);
            mCreate = (ImageView) mRootView.findViewById(R.id.create);
            mCreate.setOnClickListener(this);
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
            case R.id.create:
                PhotoPickerIntent intent = new PhotoPickerIntent(getActivity());
                intent.setPhotoCount(4);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                Log.e(TAG, "onActivityResult:"+photos.toString());
                for(int i=0;i<photos.size();i++) {
                    File originFile = new File(photos.get(i));

                    //压缩
                    //Bitmap bitmap = BitmapHelper.imageZoom(originFile);
                    int width = FragmentShow.this.getActivity().getWindowManager().getDefaultDisplay().getWidth();
                    int height = FragmentShow.this.getActivity().getWindowManager().getDefaultDisplay().getHeight();
                    Bitmap bitmap = Utils.optimizeBitmap(originFile.getAbsolutePath(), width, height);
                    Bitmap bp = ThumbnailUtils.extractThumbnail(bitmap, width, height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

                    String fileName = originFile.getName();
                    String name = fileName.substring(0, fileName.indexOf("."));
                    String ex = fileName.substring(fileName.indexOf(".") + 1);
                    String newName = name + "_cmp1." + ex;
                    String bpName = name + "_bp." + ex;

                    File dir = Environment.getExternalStorageDirectory();
                    dir = new File(dir, "testImage");
                    if (!dir.exists())
                        dir.mkdir();

                    Utils.setBitmapToFile(new File(dir, newName), bitmap);
                    Utils.setBitmapToFile(new File(dir, bpName), bp);
                }
                Intent intent = new Intent(getActivity(), CreateShowActivity.class);
                intent.putStringArrayListExtra("images", photos);
                startActivity(intent);
            }
        }
    }
}
