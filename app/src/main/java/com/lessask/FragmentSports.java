package com.lessask;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lessask.sports.SquatsActivity;
import com.lessask.test.TestMapActivity;

/**
 * Created by huangji on 2015/9/16.
 */
public class FragmentSports extends Fragment implements View.OnClickListener {
    private View view;
    private Button mRun;
    private Button mSquats;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_sports, null);
            mRun = (Button)view.findViewById(R.id.run);
            mRun.setOnClickListener(this);
            mSquats = (Button)view.findViewById(R.id.squats);
            mSquats.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.run:
                intent = new Intent(getActivity(), TestMapActivity.class);
                startActivity(intent);
                break;
            case R.id.squats:
                intent = new Intent(getActivity(), SquatsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
