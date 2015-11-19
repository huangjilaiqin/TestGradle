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
    private HashMap<String, String> headers;
    private HashMap<String, String> files;
    private HashMap<String, String> images;

    public PostSingle(String host, PostSingleEvent postSingleEvent) {
        this.host = host;
        this.postSingleEvent = postSingleEvent;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }

    public void setFiles(HashMap<String, String> files) {
        this.files = files;
    }

    @Override
    public void run() {
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
                    //Log.e(TAG, "headers: "+name+", "+value);
                    multipartEntity.addStringPart(name, value);
                }
            }
            if(files!=null){
                Iterator iterator = files.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry)iterator.next();
                    String name = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    //Log.e(TAG, "files: " + name + ", " + value);
                    multipartEntity.addFilePart(name, new File(value));
                }
            }
            if(images!=null){
                Iterator iterator = images.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry)iterator.next();
                    String name = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    //Log.e(TAG, "images: "+name+", "+value);
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
