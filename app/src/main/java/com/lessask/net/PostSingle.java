package com.lessask.net;

import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by JHuang on 2015/10/18.
 */
public class PostSingle extends Thread{
    private String TAG = PostSingle.class.getSimpleName();
    private String host;
    private PostSingleEvent postSingleEvent;
    private PostResponse postResponse;

    public PostSingle(String host, PostSingleEvent postSingleEvent) {
        this.host = host;
        this.postSingleEvent = postSingleEvent;
    }

    @Override
    public void run() {
        HashMap<String, HashMap> datas = postSingleEvent.postData();
        HashMap<String, String> headers = datas.get("headers");
        HashMap<String, String> files= datas.get("files");
        HashMap<String, String> images = datas.get("images");
        boolean isSuccess = true;
        postSingleEvent.onStart();
        try {
            MultipartEntity multipartEntity = new MultipartEntity(host);
            if(headers!=null){
                Iterator iterator = headers.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry)iterator.next();
                    String name = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    Log.e(TAG, "headers: "+name+", "+value);
                    multipartEntity.addStringPart(name, value);
                }
            }
            if(files!=null){
                Iterator iterator = files.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry)iterator.next();
                    String name = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    Log.e(TAG, "files: " + name + ", " + value);
                    multipartEntity.addFilePart(name, new File(value));
                }
            }
            if(images!=null){
                Iterator iterator = images.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry)iterator.next();
                    String name = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    Log.e(TAG, "images: "+name+", "+value);
                    multipartEntity.addOptimizeImagePart(name, new File(value));
                }
            }
            postResponse = multipartEntity.end();
        }catch (Exception e){
            Log.e(TAG, "MultipartEntity Exception:" + e.toString());
            isSuccess = false;
        }finally {
            postSingleEvent.onDone(isSuccess, postResponse);
        }
    }
}
