package com.lessask.show;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.dialog.LoadingDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ShowItem;
import com.lessask.net.NetworkFileHelper;
import com.lessask.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/*
* 发布状态
* */
public class CreateShowActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = CreateShowActivity.class.getName();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private ImageView mBack;
    private Button mSend;
    private TextView mtvContent;
    private GridView mGridView;
    private ArrayList<String> photos;
    private boolean isFull;
    private static final int REQUEST_ADD_IMAGE = 100;
    private static final int REQUEST_DELETE_IMAGE = 101;
    private MyAdapter mGridViewAdapter;
    private LoadingDialog loadingDialog;
    private Intent mIntent;
    private ShowTime mShowTime;

    private Config config = globalInfos.getConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_show);

        mIntent = getIntent();
        photos = mIntent.getStringArrayListExtra("images");

        Log.e(TAG, "onCreate:"+ photos.size());
        //未选满四张图片
        if(photos.size()<4){
            photos.add(""+R.drawable.image_add);
            isFull = false;
        }else {
            isFull = true;
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.send).setOnClickListener(this);
        mtvContent = (TextView) findViewById(R.id.content);
        mGridView = (GridView) findViewById(R.id.image_grid);
        mGridViewAdapter = new MyAdapter(this, photos);
        mGridView.setAdapter(mGridViewAdapter);

        mShowTime = new ShowTime();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认放弃向世界展示的机会吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.send:
                createShow();
                break;
        }
    }

    private void createShow(){
        loadingDialog = new LoadingDialog(CreateShowActivity.this);

        NetworkFileHelper.getInstance().startPost(config.getAddShowtimeUrl(), ShowTime.class, new NetworkFileHelper.PostFileRequest() {
            @Override
            public void onStart() {
                loadingDialog.show();
                Log.e(TAG, "start create show");
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.cancel();
                ShowTime showTime = (ShowTime) response;
                if(showTime.getError()!=null || showTime.getErrno()!=0){
                    Toast.makeText(CreateShowActivity.this, showTime.getError(),Toast.LENGTH_LONG).show();
                    Log.e(TAG, showTime.getError());
                }else {
                    int showId = showTime.getId();
                    String time = showTime.getTime();
                    ArrayList<String> pictures = showTime.getPictures();
                    //跳转到动态
                    mShowTime.setId(showId);
                    mShowTime.setTime(time);
                    mShowTime.setPictures(pictures);

                    mIntent.putExtra("showTime", mShowTime);
                    setResult(Activity.RESULT_OK, mIntent);
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                loadingDialog.cancel();
                Log.e(TAG, "创建动态:"+error);
                Toast.makeText(CreateShowActivity.this, "创建动态,"+error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();

                mShowTime.setUserId(globalInfos.getUserId());
                mShowTime.setNickname(globalInfos.getUser().getNickname());
                mShowTime.setAddress("深圳市 南山区");
                mShowTime.setContent(mtvContent.getText().toString().trim());
                mShowTime.setPermission(1);

                /*
                headers.put("userId", globalInfos.getUserId()+"");
                headers.put("address", "深圳市 南山区");
                headers.put("content", mtvContent.getText().toString().trim());
                headers.put("permission", "1");
                headers.put("ats", "");
                */


                ArrayList<String> pictures = new ArrayList<>();
                ArrayList<ArrayList<Integer>> picsSize = new ArrayList<>();
                ArrayList<Integer> picsColor = new ArrayList<>();
                int photosSize=0;
                if(!isFull)
                    photosSize = photos.size()-1;
                int lastIndex = photosSize-1;
                for (int i=0;i<photosSize;i++){
                    String picPath = photos.get(i);
                    BitmapFactory.Options options = ImageUtil.getImageSize(picPath);
                    ArrayList<Integer> imgWh = new ArrayList<Integer>();
                    imgWh.add(options.outWidth);
                    imgWh.add(options.outHeight);
                    picsSize.add(imgWh);

                    File file = new File(picPath);
                    pictures.add(file.getName());
                    //获取图片主色
                    picsColor.add(ImageUtil.getImageMainColor(picPath));
                }
                Gson gson = new Gson();
                /*
                headers.put("pictures", gson.toJson(pictures));
                headers.put("picsSize", gson.toJson(picsSize));
                headers.put("picsColor", gson.toJson(picsColor));
                */
                mShowTime.setPictures(pictures);
                mShowTime.setPicsSize(picsSize);
                mShowTime.setPicsColor(picsColor);
                headers.put("showTime", gson.toJson(mShowTime));
                Log.e(TAG, "createShow getHeaders");
                return headers;
            }

            @Override
            public HashMap<String, String> getFiles() {
                return null;
            }

            @Override
            public HashMap<String, String> getImages() {
                HashMap<String,String> images = new HashMap<>();
                int photosSize=0;
                if(!isFull)
                    photosSize = photos.size()-1;
                for (int i=0;i<photosSize;i++){
                    File file = new File(photos.get(i));
                    images.put(file.getName(), photos.get(i));
                }
                return images;
            }
        });
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
        public void setImgs(ArrayList<String> imgs){
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
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型

            if(isFull || position<imgs.size()-1){
                //使用缩略图
                Bitmap bitmap = ImageUtil.getThumbnail(new File(imgs.get(position)), getContentResolver(), 70, 70);
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
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if (resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "requestCode:"+requestCode);
            switch (requestCode) {
                case REQUEST_ADD_IMAGE:
                    if (data != null) {
                        ArrayList<String> selectedPhotos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                        //把最后一个加号的图片去掉
                        Log.e(TAG, "1 "+photos.get(photos.size()-1));
                        photos.remove(photos.size() - 1);
                        for(int i = 0; i < selectedPhotos.size(); i++) {
                            photos.add(selectedPhotos.get(i));
                        }
                        Log.e(TAG, "CreateShowActivity:" + photos);
                        if (photos.size() < 4) {
                            photos.add("" + R.drawable.image_add);
                            isFull = false;
                        } else {
                            isFull = true;
                        }
                        //mGridViewAdapter.setImgs(photos);
                        mGridViewAdapter.notifyDataSetChanged();

                    }
                    break;
                case REQUEST_DELETE_IMAGE:
                    if (data != null) {
                        ArrayList<String> remainPhotos = data.getStringArrayListExtra("images");
                        photos.clear();
                        photos.addAll(remainPhotos);
                        Log.e(TAG, "back photos:"+photos.size());
                        if (photos.size() < 4) {
                            photos.add("" + R.drawable.image_add);
                            Log.e(TAG, "delete image back:"+photos.size());
                            isFull = false;
                        } else {
                            isFull = true;
                        }
                        mGridViewAdapter.notifyDataSetChanged();
                        //mGridView.setAdapter(new MyAdapter(this, photos));
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
                if (!isFull) {
                    Log.e(TAG, "2 " + photos.get(photos.size() - 1));
                    photos.remove(photos.size() - 1);
                }
                intent.putStringArrayListExtra("images", photos);
                CreateShowActivity.this.startActivityForResult(intent, REQUEST_DELETE_IMAGE);
            }
        });
    }
}
