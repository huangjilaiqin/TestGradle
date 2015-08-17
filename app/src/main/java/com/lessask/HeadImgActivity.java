package com.lessask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;


public class HeadImgActivity extends Activity {
    private static String TAG = HeadImgActivity.class.getName();
    private ImageView ivHeadImg;
    private Button bGetHead;
    private String imageFilePath;
    private int crop = 180;
    private File imageFile;
    private Uri imageFileUri;

    private Bitmap decodeUriAsBitmap(Uri uri, BitmapFactory.Options options){
      Bitmap bitmap = null;
      try {
          Log.e(TAG, "bitmap:"+uri);
          //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
          bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
      return bitmap;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_img);

        ivHeadImg = (ImageView) findViewById(R.id.head_img);
        bGetHead = (Button)findViewById(R.id.get_img);
        bGetHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getApplicationContext().getExternalFilesDir("test");
                 File appDir = getApplicationContext().getExternalFilesDir("head_img");
                 //File appDir = getApplicationContext().getFilesDir();
                 String fileName = System.currentTimeMillis() + ".jpg";
                 imageFile = new File(appDir, fileName);
                 Log.e(TAG, imageFile.getAbsolutePath());
                 imageFileUri = Uri.fromFile(imageFile);//获取文件的Uri

                 AlertDialog dialog = null;
                 if (dialog == null) {
                     dialog = new AlertDialog.Builder(HeadImgActivity.this).setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             if (which == 0) {
                                 //Intent intent = new Intent(Int);
                                 //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                 //不能同时设置输出到文件中
                                 intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                                 //intent.putExtra("output", imageFileUri);
                                 //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                                 //intent.putExtra("crop", "true");
                                 //intent.putExtra("aspectX", 1);// 裁剪框比例
                                 //intent.putExtra("aspectY", 1);
                                 //intent.putExtra("outputX", crop);// 输出图片大小
                                 //intent.putExtra("outputY", crop);
                                 //intent.putExtra("return-data", false);
                                 startActivityForResult(intent, 101);
                             } else {
                                 //Intent intent = new Intent("android.intent.action.PICK");
                                 //Intent intent = new Intent(Intent.ACTION_PICK, null);
                                 Intent intent = new Intent(Intent.ACTION_PICK, null);
                                 intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                 //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                                 intent.putExtra("output", imageFileUri);
                                 //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                                 intent.putExtra("crop", "true");
                                 intent.putExtra("aspectX", 1);// 裁剪框比例
                                 intent.putExtra("aspectY", 1);
                                 intent.putExtra("outputX", crop);// 输出图片大小
                                 intent.putExtra("outputY", crop);
                                 //intent.putExtra("return-data", true);
                                 startActivityForResult(intent, 100);
                             }
                         }
                     }).create();
                 }
                 if (!dialog.isShowing()) {
                     dialog.show();
                 }
            }
        });
    }
protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e(TAG, "requestCode:"+requestCode+", resultCode:"+resultCode+", RESULT_OK:"+RESULT_OK);
        if (resultCode == RESULT_OK) {
            //Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //ivHeadImg.setImageBitmap(bmp);
            //ivHeadImg.setImageBitmap(intent.getData());
            //Bitmap bmp = decodeUriAsBitmap(imageFileUri);
            Bitmap bmp = null;
            switch (requestCode){
                case 100:
                    //bmp = intent.getParcelableExtra("data");
                    Log.e(TAG, "从相册选取");
                    bmp = decodeUriAsBitmap(imageFileUri, null);
                    ivHeadImg.setImageBitmap(bmp);
                    break;
                case 101:
                    Log.e(TAG, "从相机选取");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outHeight = 180;
                    options.outWidth = 180;
                    bmp = decodeUriAsBitmap(imageFileUri, options);

                    //bmp = intent.getParcelableExtra("data");
                    ivHeadImg.setImageBitmap(bmp);
                    break;
                default:
                    break;
            }
        }
    }
}
