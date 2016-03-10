package com.lessask.global;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lessask.chat.ChatGroup;
import com.lessask.model.ChatMessage;
import com.lessask.util.TimeHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laiqin on 16/2/29.
 */
public class DbHelper {

    private String TAG = DbHelper.class.getSimpleName();
    private static Context context;
    private SQLiteDatabase db;

    private Map<String, ArrayList<DbInsertListener>> insertCallbacks;
    private Map<String, ArrayList<DbUpdateListener>> updateCallbacks;
    private Map<String, ArrayList<DbDeleteListener>> deleteCallbacks;

    public SQLiteDatabase getDb() {
        return db;
    }

    private DbHelper() {
        insertCallbacks = new HashMap<>();
        updateCallbacks = new HashMap<>();
        deleteCallbacks = new HashMap<>();
        db = context.openOrCreateDatabase("lesask.db", Context.MODE_PRIVATE, null);
    }

    public static final DbHelper getInstance(Context context){
        DbHelper.context = context;
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final DbHelper INSTANCE = new DbHelper();
    }
    public void appendInsertListener(String table,DbInsertListener listener){
        if(!insertCallbacks.containsKey(table))
            insertCallbacks.put(table, new ArrayList<DbInsertListener>());
        insertCallbacks.get(table).add(listener);

    }
    public void appendUpdateListener(String table,DbUpdateListener listener){
        if(!updateCallbacks.containsKey(table))
            updateCallbacks.put(table, new ArrayList<DbUpdateListener>());
        updateCallbacks.get(table).add(listener);

    }
    public void appendDeleteListener(String table,DbDeleteListener listener){
        if(!deleteCallbacks.containsKey(table))
            deleteCallbacks.put(table, new ArrayList<DbDeleteListener>());
        deleteCallbacks.get(table).add(listener);
    }

    public void insert(String table,String nullColumnHack,ContentValues values){
        Object obj=null;
        switch (table){
            case "t_chatgroup":
                obj = new ChatGroup(values.getAsString("chatgroup_id"),values.getAsString("name"));
                break;
            case "t_chatrecord":
                Log.e(TAG, values.toString());
                int status=ChatMessage.MSG_RECEIVC;
                if(values.get("status")!=null){
                    status = values.getAsInteger("status");
                }
                Date time = TimeHelper.dateParse(values.getAsString("time"));
                obj = new ChatMessage(values.getAsInteger("userid"),values.getAsString("chatgroup_id"),values.getAsInteger("type"),values.getAsString("content"),time,values.getAsInteger("seq"),status);
                break;
        }
        db.insert(table,nullColumnHack,values);

        Log.e(TAG, "DbInsertListener size:"+insertCallbacks.size());
        if(insertCallbacks.size()>0) {
            for (DbInsertListener listener : insertCallbacks.get(table)) {
                listener.callback(obj);
            }
        }
    }
}
