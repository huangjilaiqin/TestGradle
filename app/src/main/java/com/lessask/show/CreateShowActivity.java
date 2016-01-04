package com.lessask.show;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

        NetworkFileHelper.getInstance().startPost(config.getCreateShowUrl(), CreateShowResponse.class, new NetworkFileHelper.PostFileRequest() {
            @Override
            public void onStart() {
                loadingDialog.show();
                Log.e(TAG, "start create show");
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.cancel();
                CreateShowResponse createShowResponse = (CreateShowResponse)response;
                int showId = createShowResponse.getShowid();
                String time = createShowResponse.getTime();
                ArrayList<String> pictures = createShowResponse.getPictures();
                Toast.makeText(CreateShowActivity.this, "create success", Toast.LENGTH_SHORT).show();
                //跳转到动态

                ShowItem showItem = new ShowItem();
                showItem.setId(showId);
                showItem.setAddress("深圳 南山");
                showItem.setContent(mtvContent.getText().toString().trim());
                showItem.setHeadimg(globalInfos.getUser().getHeadImg());
                showItem.setPictures(pictures);
                showItem.setTime(time);
                mIntent.putExtra("showItem", showItem);
                Log.e(TAG, "create show success:" + mIntent.getIntExtra("forResultCode", -1));
                //setResult(mIntent.getIntExtra("forResultCode", -1), intent);
                setResult(Activity.RESULT_OK, mIntent);
                finish();
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
                headers.put("userid", globalInfos.getUserId()+"");
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
                }
                headers.put("pictures", builder.toString());
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
