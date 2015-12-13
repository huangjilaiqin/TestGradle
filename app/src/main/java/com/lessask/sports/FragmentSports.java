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
import android.widget.Button;

import com.lessask.R;
import com.lessask.lesson.LessonsActivity;
import com.lessask.action.RecordActionActivity;
import com.lessask.test.SwipeRefreshAndLoadMoreActivity;
import com.lessask.video.RecordVideoActivity;

/**
 * Created by huangji on 2015/9/16.
 */
public class FragmentSports extends Fragment implements View.OnClickListener {
    private View view;
    private Button mRun;
    private Button mSquats;
    private Button mVideo;
    private Button mLesson;
    private Button mTest;
    private MyButton myButton;
    private final String TAG = FragmentSports.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        if(view == null){
            view = inflater.inflate(R.layout.fragment_sports, null);
            mRun = (Button)view.findViewById(R.id.run);
            mRun.setOnClickListener(this);
            mSquats = (Button)view.findViewById(R.id.squats);
            mSquats.setOnClickListener(this);
            mVideo = (Button)view.findViewById(R.id.vedio);
            mVideo.setOnClickListener(this);
            mLesson = (Button)view.findViewById(R.id.lesson);
            mLesson.setOnClickListener(this);
            mTest = (Button)view.findViewById(R.id.test);
            mTest.setOnClickListener(this);
            myButton = (MyButton)view.findViewById(R.id.mybutton);
            myButton.setOnClickListener(this);
            myButton.setStatusView(mTest, myButton);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.run:
                intent = new Intent(getActivity(), RunActivity.class);
                startActivity(intent);
                break;
            case R.id.squats:
                intent = new Intent(getActivity(), SquatsActivity.class);
                startActivity(intent);
                break;
            case R.id.vedio:
                //intent = new Intent(getActivity(), RecordActionActivity.class);
                intent = new Intent(getActivity(), RecordVideoActivity.class);
                startActivity(intent);
                break;
            case R.id.lesson:
                intent = new Intent(getActivity(), LessonsActivity.class);
                startActivity(intent);
                break;
            case R.id.test:
                myButton.change();
                break;
            case R.id.mybutton:
                myButton.change1();
                break;
        }
    }


}
