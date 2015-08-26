package com.lessask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by huangji on 2015/8/26.
 */
public class Utils {
    public static Bitmap decodeUriAsBitmap(Uri uri){
      Bitmap bitmap = null;
      try {
          //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
          bitmap = BitmapFactory.decodeFile(uri.getPath());
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
      return bitmap;
    }
    public static String decodeUriAsBase64(Uri uri){

        String result = null;
        File headImgFile = new File(uri.getPath());
        if(headImgFile.exists()){
            BufferedInputStream in = null;
            try{
                in = new BufferedInputStream(new FileInputStream(headImgFile));
                byte[] imgByte = new byte[(int)headImgFile.length()];
                in.read(imgByte, 0, imgByte.length);
                result = Base64.encodeToString(imgByte, Base64.DEFAULT);
            }catch (FileNotFoundException e){

            }catch (IOException e){

            }finally {
                try{
                    if(in != null)
                        in.close();
                }catch (IOException e){

                }
            }
        }
        return result;
    }
}
