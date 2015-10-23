package com.lessask.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.R;

/**
 * Created by JHuang on 2015/10/22.
 */
public class FragmentStatus extends Fragment{
    private String TAG = FragmentStatus.class.getSimpleName();
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            Log.e(TAG, "onCreateView");
            rootView = inflater.inflate(R.layout.fragment_status, null);
        }
        return rootView;
    }
}
