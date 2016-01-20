package com.lessask.sports;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.R;

/**
 * Created by huangji on 2015/9/16.
 */
public class FragmentSports extends Fragment implements View.OnClickListener {
    private View view;

    private final String TAG = FragmentSports.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        if(view == null){
            view = inflater.inflate(R.layout.fragment_sports, null);

        }
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
        }
    }


}
