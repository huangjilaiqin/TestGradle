package com.example.jhuang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;


public class HeadImgActivity extends Activity {
    private static String TAG = HeadImgActivity.class.getName();
    private ImageView ivHeadImg;
    private Button bGetHead;
    private String imageFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_img);

        ivHeadImg = (ImageView) findViewById(R.id.head_img);
        bGetHead = (Button)findViewById(R.id.get_img);
        bGetHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/head/shiyan.jpg";//设置图片的保存路径
                 Log.e(TAG, imageFilePath);
                 File imageFile = new File(imageFilePath);//通过路径创建保存文件
                 Uri imageFileUri = Uri.fromFile(imageFile);//获取文件的Uri

                 Intent it = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE);//跳转到相机Activity
                 //it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//告诉相机拍摄完毕输出图片到指定的Uri
                 //startActivityForResult(it, 0);
                 startActivity(it);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

           if(requestCode==0) {

               Bitmap bmp = BitmapFactory.decodeFile(imageFilePath);
               ivHeadImg.setImageBitmap(bmp);
           }
    }
}
