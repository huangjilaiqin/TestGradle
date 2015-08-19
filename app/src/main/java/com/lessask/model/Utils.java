package com.lessask.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by JHuang on 2015/8/19.
 */
public class Utils {
    private static final String TAG = Utils.class.getName();

    public static Bitmap getBitmapFromFile(File file){
      Bitmap bitmap = null;
      try {
          Log.e(TAG, "get bitmap:" + file);
          //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
          bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
      return bitmap;
    }
    public static void setBitmapToFile(File file, Bitmap bitmap){
      try {
          Log.e(TAG, "set bitmap:" + file);
          BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
          if(file.getName().contains("png")||file.getName().contains("PNG")) {
              bitmap.compress(Bitmap.CompressFormat.JPEG, 0, out);
          }else {
              bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
    }
}
