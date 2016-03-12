package com.lessask.util;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by JHuang on 2015/11/15.
 */
public class TimeHelper {
    private static String TAG = TimeHelper.class.getSimpleName();
    public static Date string2Date(String timeStr, String format){
        SimpleDateFormat sdf = new  SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date =null;
        try {
            date = sdf.parse(timeStr);
        }catch (ParseException e){

        }
        return date;
    }

    public static Date utcStr2Date(String timeStr){
        SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =null;
        try {
            date = sdf.parse(timeStr);
        }catch (ParseException e){
            Log.e(TAG, "utcStr2Date:"+timeStr+", e:"+e.toString());
        }
        return date;
    }

    public static String date2Show(Date date) {

        StringBuffer buffer = new StringBuffer();
        if (date != null) {
            GregorianCalendar now = new GregorianCalendar();
            now.setTime(new Date());
            int nowYear = now.get(GregorianCalendar.YEAR);
            int nowMonth = now.get(GregorianCalendar.MONTH) + 1;
            int nowDay = now.get(GregorianCalendar.DAY_OF_MONTH);
            int nowHour = now.get(GregorianCalendar.HOUR_OF_DAY);
            int nowMinute = now.get(GregorianCalendar.MINUTE);

            GregorianCalendar msgDate = new GregorianCalendar();
            msgDate.setTimeZone(TimeZone.getDefault());
            msgDate.setTime(date);
            int dateYear = msgDate.get(GregorianCalendar.YEAR);
            int dateMonth = msgDate.get(GregorianCalendar.MONTH) + 1;
            int dateDay = msgDate.get(GregorianCalendar.DAY_OF_MONTH);
            int dateHour = msgDate.get(GregorianCalendar.HOUR_OF_DAY);
            int dateMinute = msgDate.get(GregorianCalendar.MINUTE);

            int delta = 0;
            if (nowYear == dateYear) {
                if (nowMonth == dateMonth) {
                    if (nowDay == dateDay) {
                        if (nowHour == dateHour) {
                            delta = nowMinute - dateMinute;
                            buffer.append(delta + "分钟前");
                        } else {
                            delta = nowHour - dateHour;
                            buffer.append(delta + "小时前");
                        }
                    } else {
                        delta = nowDay - dateDay;
                        buffer.append(delta + "天前");
                    }
                } else {
                    delta = nowMonth - dateMonth;
                    buffer.append(delta + "个月前");
                }
            } else {
                delta = nowYear - dateYear;
                buffer.append(delta + "年前");
            }
        }

        return buffer.toString();
    }

    public static String dateFormat(Date date,String format){
        SimpleDateFormat dateformat=new SimpleDateFormat(format);
        return dateformat.format(date);
    }
    public static String dateFormat(Date date){
        String format = "yyyy-MM-dd HH:mm:ss";
        return dateFormat(date, format);
    }
    public static String dateFormat(){
        String format = "yyyy-MM-dd HH:mm:ss";
        return dateFormat(new Date(), format);
    }

    public static Date dateParse(String time,String format){
        SimpleDateFormat dateformat=new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = dateformat.parse(time);
        }catch (ParseException e){
            Log.e(TAG, "dateParse error, time:"+time+", format:"+format);
        }
        return date;
    }

    public static Date dateParse(String time){
        String format = "yyyy-MM-dd HH:mm:ss";
        return dateParse(time,format);
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
                    if (dateHour < 5) {
                        buffer.append("凌晨 ");
                    } else if (dateHour < 11) {
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

    @NonNull
    public static Gson gsonWithDate() {
        final GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (final java.text.ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        return builder.create();
    }
    //node 服务器返回的时间, JSON 序列化时间 将时间变成标准时间了
    @NonNull
    public static Gson gsonWithNodeDate() {
        final GsonBuilder builder = new GsonBuilder();

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //按照伦敦时区格式化时间, 返回的时本地时间
        TimeZone timeZon = TimeZone.getTimeZone("GMT+0");
        df.setTimeZone(timeZon);
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (final java.text.ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        return builder.create();
    }
}
