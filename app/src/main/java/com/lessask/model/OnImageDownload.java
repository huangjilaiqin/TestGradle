package com.lessask.model;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by JHuang on 2015/8/19.
 */
public interface OnImageDownload {
    void onDownloadSucc(Bitmap bitmap,ImageView imageView);
}
