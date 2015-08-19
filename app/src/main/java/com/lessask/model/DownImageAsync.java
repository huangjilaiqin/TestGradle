package com.lessask.model;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by JHuang on 2015/8/19.
 */
public class DownImageAsync extends AsyncTask<String, Void, Bitmap>{
    private String url;
    private ImageView imageView;
    private OnImageDownload download;
    private Bitmap bitmap;

    public DownImageAsync(String url, ImageView imageView, OnImageDownload download){
        this.url = url;
        this.imageView = imageView;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }

    @Override
    protected void onPostExecute(o) {
        if(download!=null){
            download.onDownloadSucc(bitmap,imageView);
        }
        super.onPostExecute(o);
    }
}
