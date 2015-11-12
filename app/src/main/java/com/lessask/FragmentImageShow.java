package com.lessask;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lessask.model.Utils;
import android.support.v4.util.LruCache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by huangji on 2015/9/18.
 * viewpager中展示图片的fragment
 */
public class FragmentImageShow extends Fragment{
    private final String TAG = FragmentImageShow.class.getName();
    private String mImage;
    private PhotoViewAttacher mAttacher;
    private View rootView;
    private ImageView imageView;
    private int position;
    private RequestQueue requestQueue;
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
    private ImageLoader.ImageCache imageCache;
    private ImageLoader imageLoader;


    public void setmImage(String mImage) {
        this.mImage = mImage;
        if(imageView!=null) {
            ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
            imageLoader.get(mImage, listener1);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        //mImage = bundle.getString("image");
        //position = bundle.getInt("position");
        requestQueue = Volley.newRequestQueue(getActivity());
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

        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_image_show, null);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            imageView = (ImageView)rootView.findViewById(R.id.image);
            ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
            imageLoader.get(mImage, listener1);

            //imageView.setImageDrawable(mImages.get(position));
            mAttacher = new PhotoViewAttacher(imageView);
            mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    Log.e(TAG, "FragmentImageShow onPhotoTap");
                    getActivity().finish();
                }
            });
            mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float v, float v1) {
                    Log.e(TAG, "FragmentImageShow onViewTap");
                    getActivity().finish();
                }
            });
        }
        return rootView;

    }
    public void update(String image){
        mImage = image;
        try {
            imageView.setImageResource(Integer.parseInt(mImage));
        }catch (Exception e){
            imageView.setImageBitmap(Utils.optimizeBitmap(mImage));
        }
    }
}
