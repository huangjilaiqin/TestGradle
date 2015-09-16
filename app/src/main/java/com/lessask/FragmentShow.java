package com.lessask;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by huangji on 2015/9/16.
 */
public class FragmentShow extends Fragment {

    private View mRootView;
    private ListView mShowList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_show, null);
            mShowList = (ListView) mRootView.findViewById(R.id.show_list);
        }
        return mRootView;
    }
}
