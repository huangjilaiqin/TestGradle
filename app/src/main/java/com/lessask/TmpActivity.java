package com.lessask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;


public class TmpActivity extends ActionBarActivity {
    private PhotoViewAttacher mAttacher;
    private RequestQueue requestQueue;
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
    private ImageLoader.ImageCache imageCache;
    private ImageLoader imageLoader;
    private String TAG = TmpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);
        Intent mIntent = getIntent();
        ArrayList<String> photos = mIntent.getStringArrayListExtra("images");
        requestQueue = Volley.newRequestQueue(this);
        imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap bitmap) {
                lruCache.put(key, bitmap);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        imageLoader = new ImageLoader(requestQueue, imageCache);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        imageLoader.get("http://123.59.40.113/imgs/"+photos.get(0), listener1);

        //imageView.setImageDrawable(imageUrls.get(position));
        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                Log.e(TAG, "FragmentImageShow onPhotoTap");
            }
        });
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                Log.e(TAG, "FragmentImageShow onViewTap");
            }
        });
    }

}
