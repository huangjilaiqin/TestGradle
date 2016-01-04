package com.lessask.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;

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


}
