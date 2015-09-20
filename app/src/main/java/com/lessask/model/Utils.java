package com.lessask.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.lessask.chat.GlobalInfos;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static Bitmap getBitmapFromFile(File file) {
        Bitmap bitmap = null;
        try {
            Log.e(TAG, "get bitmap:" + file+", size:"+file.length()/1024/1024);
            //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            BitmapFactory.Options opts=new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inSampleSize = 10;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null,opts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static void setBitmapToFile(File file, Bitmap bitmap) {
        try {
            Log.e(TAG, "set bitmap:" + file);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            if (file.getName().contains("png") || file.getName().contains("PNG")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, out);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImgFromLonowOrNet(File file, String url, ImageView view) {
        Bitmap bitmap = null;

        if (file.exists()) {
            Log.e(TAG, "exists file:" + file.getAbsolutePath());
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

        Log.e(TAG, "date2Chat:" + buffer.toString());
        return buffer.toString();
    }

    public static String formatTime4Chat(String time) {
        if (time == null) {
            Log.e(TAG, "formatTime4Chat time is null");
            return null;
        }
        return formatTime4Chat(time, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String formatTime4Chat(String time, String format) {
        Date date = null;
        Log.e(TAG, "formatTime4Chat time:"+time);
        SimpleDateFormat formater = new SimpleDateFormat(format);
        //格式化字符串中的Z表示是UTC时间(世界标准时间,而不是北京世界)
        formater.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = formater.parse(time);
            Log.e(TAG, "time parse:"+date.toString());
            return date2Chat(date);
        } catch (ParseException e) {
            Log.e(TAG, "ParseException time:" + time);
            return null;
        }
    }
}
