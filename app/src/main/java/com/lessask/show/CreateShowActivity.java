package com.lessask.show;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.lessask.ShowSelectedImageActivity;
import com.lessask.dialog.LoadingDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ShowItem;
import com.lessask.model.Utils;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;
import com.lessask.test.UploadImageTogether;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/*
* 发布状态
* */
public class CreateShowActivity extends Activity implements View.OnClickListener{

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
    private static final int ON_CREATESHOW_START= 102;
    private static final int ON_CREATESHOW_DONE= 103;
    private MyAdapter mGridViewAdapter;
    private ProgressDialog uploadDialog;
    private LoadingDialog loadingDialog;

    private Gson gson = new Gson();
    private Config config = globalInfos.getConfig();

    private final int HANDLER_SHOW_SEND = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:" + msg.what);
            switch (msg.what) {
                case ON_CREATESHOW_START:
                    loadingDialog.show();
                    break;
                case ON_CREATESHOW_DONE:
                    CreateShowResponse response = (CreateShowResponse)msg.obj;
                    int success = msg.arg1;
                    int resCode = msg.arg2;
                    int showId = response.getShowid();
                    String time = response.getTime();
                    loadingDialog.cancel();
                    Toast.makeText(CreateShowActivity.this, "create success", Toast.LENGTH_SHORT).show();
                    //跳转到动态
                    break;
            }
        }
    };

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
                createShow();
                break;
        }
    }

    private void createShow(){
        loadingDialog = new LoadingDialog(CreateShowActivity.this);
        PostSingleEvent event = new PostSingleEvent() {

            @Override
            public HashMap<String, HashMap> postData() {
                HashMap<String, HashMap> datas = new HashMap<>();
                HashMap<String, String> headers = new HashMap<>();
                HashMap<String, String> images = new HashMap<>();

                headers.put("userid", globalInfos.getUserid()+"");
                headers.put("address", "深圳市 南山区");
                headers.put("content", mtvContent.getText().toString().trim());
                headers.put("permission", "1");
                headers.put("ats", "");

                StringBuilder builder = new StringBuilder();
                int photosSize=0;
                if(!isFull)
                    photosSize = photos.size()-1;
                int lastIndex = photosSize-1;
                for (int i=0;i<photosSize;i++){
                    File file = new File(photos.get(i));
                    builder.append(file.getName());
                    if(i!=lastIndex){
                        builder.append("##");
                    }
                    images.put(file.getName(), photos.get(i));
                }
                headers.put("pictures", builder.toString());

                datas.put("headers", headers);
                datas.put("images", images);
                return datas;
            }

            @Override
            public void onStart() {
                Message msg = new Message();
                msg.what = ON_CREATESHOW_START;
                handler.sendMessage(msg);
            }

            @Override
            public void onDone(boolean success, PostResponse postResponse) {
                int resCode = postResponse.getCode();
                String body = postResponse.getBody();
                CreateShowResponse response = gson.fromJson(body, CreateShowResponse.class);

                Message msg = new Message();
                msg.arg2 = resCode;
                msg.obj = response;
                msg.what = ON_CREATESHOW_DONE;
                if(success)
                    msg.arg1 = 1;
                else
                    msg.arg1 = 0;
                handler.sendMessage(msg);
            }
        };
        new PostSingle(config.getCreateShowUrl(), event).start();
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
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型

            if(isFull || position<imgs.size()-1){
                //使用缩略图
                Bitmap bitmap = Utils.getThumbnail(new File(imgs.get(position)), getContentResolver());
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
                        //测试图片压缩
                        /*
                        for(int i=0;i<selectedPhotos.size();i++){
                            String originFileStr = selectedPhotos.get(i);
                            File originFile = new File(originFileStr);

                            //获取缩略图
                            ContentResolver cr = getContentResolver();

                            Bitmap thumbnailBitmap = Utils.getThumbnail(originFile, cr);

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
                        */
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
