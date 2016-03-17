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

import com.lessask.R;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.global.DbHelper;
import com.lessask.global.GlobalInfos;
import com.lessask.model.User;
import com.lessask.util.ImageUtil;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class PersonInfoActivity extends AppCompatActivity {

    private final int REQUEST_HEAD_IMAGE = 1;
    public final int SAVE_NAME = 2;
    private String TAG= PersonInfoActivity.class.getSimpleName();
    private GlobalInfos globalInfos= GlobalInfos.getInstance();
    private CircleImageView headImg;
    private TextView nameView;
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
                            headImg.setImageBitmap(ImageUtil.getOptimizeBitmapFromFile(selectedPhotos.get(0)));
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
