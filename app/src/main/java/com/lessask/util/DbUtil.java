package com.lessask.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lessask.global.GlobalInfos;
import com.lessask.model.User;

/**
 * Created by laiqin on 16/3/8.
 */
public class DbUtil {
    private static GlobalInfos globalInfos = GlobalInfos.getInstance();

    public static User loadUserFromDb(Context context,int userid){
        SQLiteDatabase db = globalInfos.getDb(context);
        Cursor cursor = db.rawQuery("select * from t_contact where userid=?", new String[]{"" + userid});
        Log.e("cursor", "curcor count:"+cursor.getCount());
        User user=null;
        if(cursor.moveToNext()){
            int uid = cursor.getInt(0);
            String nickname = cursor.getString(1);
            String headImg = cursor.getString(2);
            user = new User(userid,nickname,headImg);
        }
        cursor.close();
        return user;
    }
}
