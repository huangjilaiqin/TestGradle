package com.lessask.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.R;

/**
 * Created by JHuang on 2015/11/25.
 */
public class FragmentTest  extends Fragment implements View.OnClickListener{
    private View rootView;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_test, null);
            rootView.findViewById(R.id.slider_menu).setOnClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.slider_menu:
                intent = new Intent(getActivity(), SlideMenuActivity.class);
                startActivity(intent);
                break;
        }
    }
}
