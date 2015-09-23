package com.lessask.test;

import android.util.Log;

import com.lessask.model.ShowItem;
import com.lessask.net.MultipartEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by huangji on 2015/9/23.
 */
public class UploadImageSingle extends Thread{

    private final String TAG = UploadImageSingle.class.getSimpleName();
    private  File file;

    public UploadImageSingle(File file){
        this.file = file;
    }

    @Override
    public void run() {

        Log.e(TAG, "begin:"+new Date());
        try {
            MultipartEntity multipartEntity = new MultipartEntity("http://ws.o-topcy.com/httproute/show");
            multipartEntity.addFilePart("file", file);
            multipartEntity.end();
            Log.e(TAG, "end:"+new Date());
        }catch (Exception e){
            Log.e(TAG, "MultipartEntity Exception:"+e.toString());
        }
    }
}
