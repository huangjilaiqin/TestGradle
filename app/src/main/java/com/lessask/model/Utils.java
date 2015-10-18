package com.lessask.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import com.lessask.global.GlobalInfos;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by JHuang on 2015/8/19.
 */
public class Utils {
    private static GlobalInfos globalInfos = GlobalInfos.getInstance();

    private static final String TAG = Utils.class.getName();

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

    public static String decodeUriAsBase64(Uri uri) {

        String result = null;
        File headImgFile = new File(uri.getPath());
        if (headImgFile.exists()) {
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(headImgFile));
                byte[] imgByte = new byte[(int) headImgFile.length()];
                in.read(imgByte, 0, imgByte.length);
                result = Base64.encodeToString(imgByte, Base64.DEFAULT);
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {

                }
            }
        }
        return result;
    }

    /*
    * 如果加载原图非常容易内存溢出
    * */
    public static Bitmap getBitmapFromFile(File file) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts=new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inTempStorage = new byte[100 * 1024];
            /*
            BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            int width = opts.outWidth;
            int height = opts.outHeight;

            opts.inJustDecodeBounds = false;
            */
            //opts.inSampleSize = 2;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static void setBitmapToFile(File file, Bitmap bitmap) {
        try {
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            if (file.getName().contains("png") || file.getName().contains("PNG")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedInputStream bitmat2BufferedInputStream(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return is;
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


    public static String date2Chat(Date date) {
        String[] weakName = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

        StringBuffer buffer = new StringBuffer();
        if (date != null) {
            GregorianCalendar now = new GregorianCalendar();
            now.setTime(new Date());
            int nowYear = now.get(GregorianCalendar.YEAR);
            int nowMonth = now.get(GregorianCalendar.MONTH) + 1;
            int nowDay = now.get(GregorianCalendar.DAY_OF_MONTH);
            int nowHour = now.get(GregorianCalendar.HOUR_OF_DAY);
            int nowMinute = now.get(GregorianCalendar.MINUTE);
            int nowWeek = now.get(GregorianCalendar.DAY_OF_WEEK);

            GregorianCalendar msgDate = new GregorianCalendar();
            msgDate.setTimeZone(TimeZone.getDefault());
            msgDate.setTime(date);
            int dateYear = msgDate.get(GregorianCalendar.YEAR);
            int dateMonth = msgDate.get(GregorianCalendar.MONTH) + 1;
            int dateDay = msgDate.get(GregorianCalendar.DAY_OF_MONTH);
            int dateHour = msgDate.get(GregorianCalendar.HOUR_OF_DAY);
            int dateMinute = msgDate.get(GregorianCalendar.MINUTE);
            int dateWeek = msgDate.get(GregorianCalendar.DAY_OF_WEEK);

            if (nowYear == dateYear) {
                if (nowDay == dateDay) {
                    if (dateHour < 6) {
                        buffer.append("凌晨 ");
                    } else if (dateHour < 10) {
                        buffer.append("上午 ");
                    } else if (dateHour < 14) {
                        buffer.append("中午 ");
                    } else if (dateHour < 18) {
                        buffer.append("下午 ");
                    } else if (dateHour < 24) {
                        buffer.append("晚上 ");
                    }
                    SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
                    buffer.append(hourFormat.format(date));
                } else if (nowDay == dateDay + 1) {
                    buffer.append("昨天");
                } else if (dateWeek >= 2 && nowDay - dateDay == nowWeek - dateWeek) {
                    buffer.append(weakName[dateWeek - 1]);
                } else {
                    buffer.append(dateMonth + "月" + dateDay + "日");
                }
            } else {
                buffer.append(dateYear + "年" + dateMonth + "月" + dateDay + "日");
            }
        }

        return buffer.toString();
    }

    public static String formatTime4Chat(String time) {
        if (time == null) {
            return null;
        }
        return formatTime4Chat(time, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String formatTime4Chat(String time, String format) {
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
        //格式化字符串中的Z表示是UTC时间(世界标准时间,而不是北京世界)
        formater.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = formater.parse(time);
            return date2Chat(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Bitmap optimizeBitmap(File file) {
        return optimizeBitmap(file.getAbsolutePath(), globalInfos.getScreenWidth(), globalInfos.getScreenHeight());
    }
    public static Bitmap optimizeBitmap(File file, int maxWidth, int maxHeight) {
        return optimizeBitmap(file.getAbsolutePath(), maxWidth, maxHeight);
    }
    public static Bitmap optimizeBitmap(String pathName) {
        return optimizeBitmap(pathName, globalInfos.getScreenWidth(), globalInfos.getScreenHeight());
    }
    public static Bitmap optimizeBitmap(String pathName, int maxWidth, int maxHeight) {
		Bitmap result = null;
		try {
            // 图片配置对象，该对象可以配置图片加载的像素获取个数
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 表示加载图像的原始宽高
            options.inJustDecodeBounds = true;
            result = BitmapFactory.decodeFile(pathName, options);
            // Math.ceil表示获取与它最近的整数（向上取值 如：4.1->5 4.9->5）
            int widthRatio = (int) Math.ceil(options.outWidth / maxWidth);
            int heightRatio = (int) Math.ceil(options.outHeight / maxHeight);
            // 设置最终加载的像素比例，表示最终显示的像素个数为总个数的
            if (widthRatio > 1 || heightRatio > 1) {
                if (widthRatio > heightRatio) {
                    options.inSampleSize = widthRatio;
                } else {
                    options.inSampleSize = heightRatio;
                }
            }
            // 解码像素的模式，在该模式下可以直接按照option的配置取出像素点
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            result = BitmapFactory.decodeFile(pathName, options);

        } catch (Exception e) {
        }
		return result;
	}

    public static Bitmap getThumbnail(File originFile,ContentResolver cr){

        //获取缩略图
        //获取原图id
        String columns[] = new String[] { MediaStore.Images.Media._ID};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, "_data=?", new String[]{originFile.getAbsolutePath()}, null);
        int originImgId = 0;
        if(cursor.moveToFirst()) {
            originImgId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        }
        //根据原图id查找缩略图
        String[] projection = { MediaStore.Images.Thumbnails.DATA};
        cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, "image_id=?", new String[]{originImgId+""}, null);
        String thumbnailPath = "";
        String thumbData = "";
        Bitmap thumbnailBitmap = null;
        if(cursor.moveToFirst()) {
            thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            thumbnailBitmap = Utils.getBitmapFromFile(new File(thumbnailPath));
        }else {
            //不存在缩略图,自己进行压缩
            thumbnailBitmap = Utils.optimizeBitmap(originFile.getAbsolutePath(), 100, 100);
        }
        return thumbnailBitmap;
    }

}
