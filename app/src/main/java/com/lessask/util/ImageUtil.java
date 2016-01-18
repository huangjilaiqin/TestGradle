package com.lessask.util;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.lessask.global.GlobalInfos;
import com.lessask.model.DownImageAsync;
import com.lessask.test.FragmentTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by huangji on 2016/1/4.
 */
public class ImageUtil {
    private static String TAG = FragmentTest.class.getSimpleName();
    private static GlobalInfos globalInfos = GlobalInfos.getInstance();

    /*
    * 如果加载原图非常容易内存溢出
    * */
    public static Bitmap getBitmapFromFile(File file) {
        Bitmap bitmap = null;
        try {
            /*
            BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            int width = opts.outWidth;
            int height = opts.outHeight;

            opts.inJustDecodeBounds = false;
            */

            BitmapFactory.Options opts=new BitmapFactory.Options();
            /*
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inTempStorage = new byte[100 * 1024];

            opts.inSampleSize = 2;
            */
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, opts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    public static Bitmap getOptimizeBitmapFromFile(File file) {
        return getOptimizeBitmapFromFile(file.getAbsolutePath(), globalInfos.getScreenWidth(), globalInfos.getScreenHeight());
    }
    public static Bitmap getOptimizeBitmapFromFile(File file, int maxWidth, int maxHeight) {
        return getOptimizeBitmapFromFile(file.getAbsolutePath(), maxWidth, maxHeight);
    }
    public static Bitmap getOptimizeBitmapFromFile(String pathName) {
        return getOptimizeBitmapFromFile(pathName, globalInfos.getScreenWidth(), globalInfos.getScreenHeight());
    }
    /*
    * 限制大小获取bitmap
    * */
    public static Bitmap getOptimizeBitmapFromFile(String pathName, int maxWidth, int maxHeight) {
        File file = new File(pathName);
        if(!file.exists() || !file.isFile()) {
            throw new Resources.NotFoundException();
        }
        Log.e(TAG, "w:" + maxWidth + ", h:" + maxHeight);
		Bitmap result = null;
		try {
            // 图片配置对象，该对象可以配置图片加载的像素获取个数
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 表示加载图像的原始宽高
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            double calSize = 2*options.outWidth*options.outHeight/1024.0;
            Log.e(TAG, "calSize:"+calSize+"Kb");

            // Math.ceil表示获取与它最近的整数（向上取值 如：4.1->5 4.9->5）
            int widthRatio = (int) Math.ceil(((float)options.outWidth) / maxWidth);
            int heightRatio = (int) Math.ceil(((float)options.outHeight) / maxHeight);
            // 设置最终加载的像素比例，表示最终显示的像素个数为总个数的
            if (widthRatio > 1 || heightRatio > 1) {
                if (widthRatio > heightRatio) {
                    options.inSampleSize = widthRatio;
                } else {
                    options.inSampleSize = heightRatio;
                }
            }
            Log.e(TAG, "inSampleSize:"+options.inSampleSize);
            // 解码像素的模式，在该模式下可以直接按照option的配置取出像素点
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            result = BitmapFactory.decodeFile(pathName, options);
            double calOptiSize = 2*options.outWidth*options.outHeight/1024.0;
            Log.e(TAG, "opti calSize:"+calOptiSize+"Kb");
            Log.e(TAG, "bit rate:"+calSize/calOptiSize);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
		return result;
	}

    public static BitmapFactory.Options getImageSize(String pathName) {
        File file = new File(pathName);
        if (!file.exists() || !file.isFile()) {
            throw new Resources.NotFoundException();
        }
        Bitmap result = null;
        // 图片配置对象，该对象可以配置图片加载的像素获取个数
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 表示加载图像的原始宽高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        return  options;
    }

    public static void setBitmap2File(File file,Bitmap mBitmap) throws IOException{
        setBitmap2File(file, mBitmap, 80);
    }
    /*
    * 将压缩bitmap到文件
    * */
    public static void setBitmap2File(File file,Bitmap mBitmap, int quality) throws IOException{
        file.createNewFile();
        FileOutputStream fOut = null;
        fOut = new FileOutputStream(file);

        mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
        fOut.flush();
        fOut.close();
    }

    public static Bitmap getThumbnail(File originFile,ContentResolver cr,int width, int height){

        //获取缩略图
        //获取原图id
        String columns[] = new String[] { MediaStore.Images.Media._ID};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, "_data=?", new String[]{originFile.getAbsolutePath()}, null);
        int originImgId = 0;
        if(cursor.moveToFirst()) {
            originImgId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        }
        cursor.close();
        //根据原图id查找缩略图
        String[] projection = { MediaStore.Images.Thumbnails.DATA};
        cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, "image_id=?", new String[]{originImgId+""}, null);
        String thumbnailPath = "";
        String thumbData = "";
        Bitmap thumbnailBitmap = null;
        if(cursor.moveToFirst()) {
            thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            thumbnailBitmap = getBitmapFromFile(new File(thumbnailPath));
        }
        if(thumbnailBitmap==null){
            //不存在缩略图,自己进行压缩
            //Log.e(TAG, "originFile:"+originFile.getAbsolutePath());
            thumbnailBitmap = getOptimizeBitmapFromFile(originFile.getAbsolutePath(), width, height);
        }
        long oSize = originFile.length()/1024;
        int aSize = thumbnailBitmap.getByteCount()/1024;
        float rate = 1f*(oSize-aSize)/oSize;
        //Log.e(TAG, "getThumbnail:"+oSize+", compressSize:"+aSize+", rate:"+rate);
        cursor.close();
        return thumbnailBitmap;
    }
    public static Bitmap decodeUriAsBitmap(Uri uri) {
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
    public static Bitmap getImgFromLonowOrNet(File file, String url, ImageView view) {
        Bitmap bitmap = null;

        if (file.exists()) {
            bitmap = getBitmapFromFile(file);
            if (bitmap != null) {
                view.setImageBitmap(bitmap);
            }
        } else {
            new DownImageAsync(url, view).execute();
        }

        return bitmap;
    }
    public static int getImageMainColor(String path){
        Bitmap bitmap = ImageUtil.getOptimizeBitmapFromFile(new File(path));
        Palette palette = Palette.from(bitmap).generate();
        int color = palette.getMutedColor(0x000000);
        if(color==0){
            color = palette.getLightMutedColor(0x000000);
            if(color==0){
                color = palette.getDarkMutedColor(0x000000);
                if(color==0){
                    color  = palette.getVibrantColor(0x000000);
                    if(color==0){
                        color = palette.getLightVibrantColor(0x000000);
                        if(color==0){
                            color = palette.getDarkVibrantColor(0x000000);
                        }
                    }
                }
            }
        }
        return color;
    }
    public static String getImageUrlWithWH(String url,int w,int h){
        StringBuilder builder = new StringBuilder(url);
        builder.append("!");
        builder.append(w);
        builder.append("_");
        builder.append(h);
        return builder.toString();
    }
}
