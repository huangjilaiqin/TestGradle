package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;

/**
 * Created by JHuang on 2015/10/22.
 */
public class FragmentWorkout extends Fragment {
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private String TAG = FragmentWorkout.class.getSimpleName();

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "FragmentWorkout onCreateView");
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_workout, null);
            Log.e(TAG, "FragmentWorkout");
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            Log.e(TAG, "onActivityResult");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}