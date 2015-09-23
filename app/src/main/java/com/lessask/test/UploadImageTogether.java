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
public class UploadImageTogether extends Thread{
    private final String TAG = UploadImageTogether.class.getSimpleName();
    private ShowItem showItem;
    private boolean isFull;

    public UploadImageTogether(ShowItem showItem, boolean isFull){
        this.showItem = showItem;
        this.isFull = isFull;
    }
    @Override
    public void run() {

        try {
            Log.e(TAG, "begin:"+new Date());
            MultipartEntity multipartEntity = new MultipartEntity("http://ws.o-topcy.com/httproute/show");
            multipartEntity.addStringPart("userid", ""+showItem.getName());
            multipartEntity.addStringPart("content", ""+showItem.getContent());
            ArrayList<String> imgs = showItem.getShowImgs();
            for(int i=0;i<imgs.size();i++){
                Log.e(TAG, imgs.get(i));
                if(i!=imgs.size()-1) {
                    multipartEntity.addFilePart("file" + i, new File(imgs.get(i)));
                }else if(isFull){
                    multipartEntity.addFilePart("file" + i, new File(imgs.get(i)));
                }
            }
            multipartEntity.end();
            Log.e(TAG, "end:"+new Date());
        }catch (Exception e){
            Log.e(TAG, "MultipartEntity Exception:"+e.toString());
        }
    }
}
