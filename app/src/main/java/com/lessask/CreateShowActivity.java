package com.lessask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lessask.model.Utils;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/*
* 发布状态
* */
public class CreateShowActivity extends Activity implements View.OnClickListener{

    private final String TAG = CreateShowActivity.class.getName();
    private ImageView mBack;
    private Button mSend;
    private TextView mtvContent;
    private GridView mGridView;
    private ArrayList<String> photos;
    private boolean isFull;
    private static final int REQUEST_ADD_IMAGE = 100;
    private static final int REQUEST_DELETE_IMAGE = 101;
    private MyAdapter mGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_show);

        Intent intent = getIntent();
        photos = intent.getStringArrayListExtra("images");

        Log.e(TAG, "onCreate:"+ photos.size());
        //未选满四张图片
        if(photos.size()<4){
            photos.add(""+R.drawable.image_add);
            isFull = false;
        }else {
            isFull = true;
        }

        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mSend = (Button) findViewById(R.id.send);
        mSend.setOnClickListener(this);
        mtvContent = (TextView) findViewById(R.id.content);
        mGridView = (GridView) findViewById(R.id.image_grid);
        mGridViewAdapter = new MyAdapter(this, photos);
        mGridView.setAdapter(mGridViewAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.send:
                Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    //自定义适配器
    class MyAdapter extends BaseAdapter {
        //上下文对象
        private Context context;
        private ArrayList<String> imgs;

        MyAdapter(Context context, ArrayList<String> imgs){
            this.context = context;
            this.imgs = imgs;
        }
        public int getCount() {
            return imgs.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            //加载自定义griview的item布局
            LayoutInflater inflater = LayoutInflater.from(CreateShowActivity.this);

            View view = inflater.inflate(R.layout.image_gridview_item, null);
            ImageView imageView = (ImageView)view.findViewById(R.id.image);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型

            if(isFull || position<imgs.size()-1){
                Bitmap bitmap = Utils.getBitmapFromFile(new File(imgs.get(position)));
                imageView.setImageBitmap(bitmap);
                registerImageEvent(imageView, position);
            }else {
                imageView.setImageResource(Integer.parseInt(imgs.get(position)));//为ImageView设置图片资源
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PhotoPickerIntent intent = new PhotoPickerIntent(CreateShowActivity.this);
                        //不包括最后一个加号的图片
                        intent.setPhotoCount(4-photos.size()+1);
                        intent.setShowCamera(true);
                        intent.setShowGif(true);
                        startActivityForResult(intent, REQUEST_ADD_IMAGE);
                    }
                });
            }
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "requestCode:"+requestCode);
            switch (requestCode) {
                case REQUEST_ADD_IMAGE:
                    if (data != null) {
                        ArrayList<String> selectedPhotos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                        //把最后一个加号的图片去掉
                        Log.e(TAG, "1 "+photos.get(photos.size()-1));
                        photos.remove(photos.size() - 1);
                        for (int i = 0; i < selectedPhotos.size(); i++) {
                            photos.add(selectedPhotos.get(i));
                        }
                        Log.e(TAG, "CreateShowActivity:" + photos);
                        if (photos.size() < 4) {
                            photos.add("" + R.drawable.image_add);
                            isFull = false;
                        } else {
                            isFull = true;
                        }
                        mGridView.setAdapter(new MyAdapter(this, photos));
                    }
                    break;
                case REQUEST_DELETE_IMAGE:
                    if (data != null) {
                        ArrayList<String> remainPhotos = data.getStringArrayListExtra("images");
                        photos = remainPhotos;
                        Log.e(TAG, "back photos:"+photos.size());
                        if (photos.size() < 4) {
                            photos.add("" + R.drawable.image_add);
                            Log.e(TAG, "delete image back:"+photos.size());
                            isFull = false;
                        } else {
                            isFull = true;
                        }
                        mGridView.setAdapter(new MyAdapter(this, photos));
                    }
                    break;
            }
        }
    }
    private void registerImageEvent(ImageView image, final int index){
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateShowActivity.this, "long click image", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateShowActivity.this, ShowSelectedImageActivity.class);
                intent.putExtra("index", index);
                if(!isFull){
                    Log.e(TAG, "2 "+photos.get(photos.size()-1));
                    photos.remove(photos.size()-1);
                }
                Log.e(TAG, "before delete photos:"+photos.size());
                intent.putStringArrayListExtra("images", photos);
                CreateShowActivity.this.startActivityForResult(intent, REQUEST_DELETE_IMAGE);
            }
        });
    }
}
