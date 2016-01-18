package com.lessask.show;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lessask.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by huangji on 2015/9/18.
 * 查看动态图片的viewpager中展示图片的fragment
 */
public class FragmentLocalImageShow extends Fragment{
    private final String TAG = FragmentLocalImageShow.class.getName();
    private String imageUrl;
    private View rootView;
    private ImageView imageView;
    private int position;
    private PhotoViewAttacher mAttacher;
    private RequestQueue requestQueue;
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
    private ImageLoader.ImageCache imageCache;
    private ImageLoader imageLoader;


    public void setimageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if(imageView!=null) {
            ImageLoader.ImageListener listener1 = ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
            imageLoader.get(imageUrl, listener1);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        //imageUrl = bundle.getString("image");
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
            imageLoader.get(imageUrl, listener1);

            //imageView.setImageDrawable(imageUrls.get(position));
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
}
