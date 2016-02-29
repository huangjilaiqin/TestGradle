package com.lessask.global;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lessask.chat.ChatGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laiqin on 16/2/29.
 */
public class DbHelper {

    private static Context context;
    private SQLiteDatabase db;

    private Map<String, ArrayList<DbInsertListener>> insertCallbacks;
    private Map<String, ArrayList<DbUpdateListener>> updateCallbacks;
    private Map<String, ArrayList<DbDeleteListener>> deleteCallbacks;

    public SQLiteDatabase getDb() {
        return db;
    }

    public DbHelper() {
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
        db.insert(table,nullColumnHack,values);

        for(DbInsertListener listener:insertCallbacks.get(table)){
            Object obj=null;
            switch (table){
                case "t_chatgroup":
                    obj = new ChatGroup(values.getAsString("chatgroup_id"),values.getAsString("name"));
                    break;
            }
            listener.callback(obj);
        }
    }
}
