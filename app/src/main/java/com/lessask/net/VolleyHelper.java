package com.lessask.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by huangji on 2015/12/9.
 */
public class VolleyHelper {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private VolleyHelper() {
        if(mCtx==null)
            throw new NullPointerException();
        mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static void setmCtx(Context mCtx) {
        VolleyHelper.mCtx = mCtx.getApplicationContext();
    }

    public static VolleyHelper getInstance() {
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final VolleyHelper INSTANCE = new VolleyHelper();
    }

    public <T> void addToRequestQueue(Request<T> req) {
        mRequestQueue.add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}