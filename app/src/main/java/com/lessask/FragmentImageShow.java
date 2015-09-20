package com.lessask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lessask.model.Utils;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by huangji on 2015/9/18.
 */
public class FragmentImageShow extends Fragment{
    private final String TAG = FragmentImageShow.class.getName();
    private List<String> mImages;
     private PhotoViewAttacher mAttacher;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        mImages = bundle.getStringArrayList("images");
        int position = bundle.getInt("position");
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_image_show, null);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "FragmentImageShow click");
                    getActivity().onBackPressed();
                }
            });
            ImageView imageView = (ImageView)rootView.findViewById(R.id.image);
            try {
                imageView.setImageResource(Integer.parseInt(mImages.get(position)));
            }catch (Exception e){
                imageView.setImageBitmap(Utils.getBitmapFromFile(new File(mImages.get(position))));
            }
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
}
