package com.lessask.util;

import android.content.Context;

/**
 * Created by JHuang on 2016/1/11.
 */


public class ScreenUtil {
    public static int dp2Px(Context context,int dp){
        return  (int)(dp*context.getResources().getDisplayMetrics().density+0.5f);
    }
    public static int getScreenWidth(Context context){
        return  context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight(Context context){
        return  context.getResources().getDisplayMetrics().heightPixels;
    }
}
