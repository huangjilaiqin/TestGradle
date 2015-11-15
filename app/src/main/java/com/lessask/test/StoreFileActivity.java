package com.lessask.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.lessask.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class StoreFileActivity extends Activity {

    private EditText et;
    private ImageView ivHead;
    private static final String TAG = Activity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_file);

        et = (EditText) findViewById(R.id.store_file);
        ivHead = (ImageView) findViewById(R.id.head_img);
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Android/data/com.lessask/files/head_img/1439627037444.jpg");
        ivHead.setImageBitmap(bitmap);
        String content = load();
        if(content.length()>0) {
            et.setText(content);
            et.setSelection(content.length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        store();
    }

    private String load(){
        StringBuilder content = new StringBuilder();
        FileInputStream in = null;
        BufferedReader reader = null;
        try{
            File f = new File("/data/data/com.lessask/files/");
            //f.mkdir();
            Log.e(TAG, f.getAbsolutePath()+", exists:"+f.exists());
            Log.e(TAG, "dataDir:"+getApplicationContext().getPackageName());
            Log.e(TAG, "getFilesDir:"+getApplicationContext().getFilesDir());
            Log.e(TAG, "getCacheDir:"+getApplicationContext().getCacheDir());
            Log.e(TAG, "getExternalCacheDir:"+getApplicationContext().getExternalCacheDir());
            Log.e(TAG, "getExternalFilesDir:"+getApplicationContext().getExternalFilesDir("head_img"));
            Log.e(TAG, "dataDir:"+Environment.getDataDirectory());
            Log.e(TAG, "dataDir:"+Environment.getDownloadCacheDirectory());
            Log.e(TAG, "dataDir:"+Environment.getExternalStorageState());
            Log.e(TAG, "getExternalStorageDirectory:"+Environment.getExternalStorageDirectory());
            Log.e(TAG, "dataDir:"+Environment.getExternalStoragePublicDirectory("image/*"));
            Log.e(TAG, "dataDir:"+Environment.getRootDirectory());
            f = new File("/data/data/com.lessask/files/testStore");
            if(!f.exists()) {
                Log.e(TAG, "exists:" + f.exists());
                f.createNewFile();
            }
            in = openFileInput("testStore");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null){
                content.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
    private void store(){
        String content = et.getText().toString().trim();
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("testStore", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(content);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
