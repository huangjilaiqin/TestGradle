package com.lessask.me;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.dialog.LoadingDialog;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.GlobalInfos;
import com.lessask.model.User;
import com.lessask.net.NetworkFileHelper;
import com.lessask.net.VolleyHelper;
import com.lessask.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class PersonInfoActivity extends AppCompatActivity {

    private final int REQUEST_HEAD_IMAGE = 1;
    public final int SAVE_NAME = 2;
    private String TAG= PersonInfoActivity.class.getSimpleName();
    private GlobalInfos globalInfos= GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private CircleImageView headImg;
    private TextView nameView;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("个人信息");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });


        User user = globalInfos.getUser();
        headImg = (CircleImageView) findViewById(R.id.head);
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(headImg,0,0);
        String headImgUrl = imageUrlPrefix+globalInfos.getUserId()+".jpg";
        VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);
        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(user.getNickname());

        findViewById(R.id.head_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent intent = new PhotoPickerIntent(PersonInfoActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_HEAD_IMAGE);
            }
        });
        findViewById(R.id.name_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonInfoActivity.this, ChangeNameActivity.class);
                startActivityForResult(intent, SAVE_NAME);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "requestCode:" + requestCode);
            switch (requestCode) {
                case REQUEST_HEAD_IMAGE:
                    if (data != null) {
                        ArrayList<String> selectedPhotos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                        //把最后一个加号的图片去掉
                        if(selectedPhotos.size()>0) {

                            loadingDialog = new LoadingDialog(PersonInfoActivity.this);
                            final File picFile = new File(selectedPhotos.get(0));
                            final String picname = picFile.getName();
                            NetworkFileHelper.getInstance().startPost(config.getUpdateHeadImg(), User.class, new NetworkFileHelper.PostFileRequest() {
                                @Override
                                public void onStart() {
                                    loadingDialog.show();
                                }

                                @Override
                                public void onResponse(Object response) {
                                    loadingDialog.cancel();

                                    User user = (User) response;
                                    if(user.getError()!=null || user.getErrno()!=0){
                                        Toast.makeText(PersonInfoActivity.this, user.getError(),Toast.LENGTH_LONG).show();
                                    }else {
                                        headImg.setImageBitmap(ImageUtil.getOptimizeBitmapFromFile(picFile));
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    loadingDialog.cancel();
                                    Toast.makeText(PersonInfoActivity.this, "上传头像失败,"+error, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public HashMap<String, String> getHeaders() {
                                    HashMap<String, String> headers = new HashMap<>();
                                    headers.put("userid", globalInfos.getUserId()+"");
                                    headers.put("picname" ,picname);
                                    return headers;
                                }

                                @Override
                                public HashMap<String, String> getFiles() {
                                    return null;
                                }

                                @Override
                                public HashMap<String, String> getImages() {
                                    HashMap<String,String> images = new HashMap<>();
                                    images.put(picname, picFile.getPath());
                                    return images;
                                }
                            });
                        }
                    }
                    break;
                case SAVE_NAME:
                    String name = data.getStringExtra("name");
                    //更新数据库
                    String[] whereValues = new String[]{globalInfos.getUserId()+""};
                    ContentValues values = new ContentValues();
                    values.put("nickname", name);
                    DbHelper.getInstance(this).getDb().update("t_user",values,"userid=?",whereValues);

                    nameView.setText(name);
                    break;
            }
        }
    }

}
