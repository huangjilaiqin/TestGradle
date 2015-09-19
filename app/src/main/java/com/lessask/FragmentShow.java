package com.lessask;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.lessask.model.ShowItem;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/9/16.
 */
public class FragmentShow extends Fragment implements View.OnClickListener {

    private View mRootView;
    private ListView mShowList;
    private ShowListAdapter mShowListAdapter;

    private ImageView ivUp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_show, null);
            mShowList = (ListView) mRootView.findViewById(R.id.show_list);
            //获取数据状态数据
            ArrayList showItems = getData();
            mShowListAdapter = new ShowListAdapter(getActivity(), showItems);
            mShowList.setAdapter(mShowListAdapter);

            ivUp = (ImageView) mRootView.findViewById(R.id.up);
        }
        return mRootView;
    }
    private ArrayList getData(){

        ArrayList<ShowItem> showItems = new ArrayList<>();
        ArrayList<String> showImgs1 = new ArrayList<>();
        showImgs1.add(""+R.drawable.runnging);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs1, "今天天气真不错！！！", 89, 23,0));
        ArrayList<String> showImgs2 = new ArrayList<>();
        showImgs2.add(""+R.drawable.runnging);
        showImgs2.add(""+R.drawable.speed);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs2, "今天天气真不错！！！", 89, 23,0));
        ArrayList<String> showImgs3 = new ArrayList<>();
        showImgs3.add(""+R.drawable.runnging);
        showImgs3.add(""+R.drawable.speed);
        showImgs3.add(""+R.drawable.comment);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs3, "今天天气真不错！！！", 89, 23,0));
        ArrayList<String> showImgs4 = new ArrayList<>();
        showImgs4.add(""+R.drawable.runnging);
        showImgs4.add(""+R.drawable.speed);
        showImgs4.add(""+R.drawable.chat);
        showImgs4.add(""+R.drawable.runnging);
        showItems.add(new ShowItem("唐三炮",null,"晚上 20:35", "深圳市 南山区 塘朗山", showImgs4, "今天天气真不错！！！", 89, 23,0));
        return showItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.up:
                ImageView view = (ImageView)v;
                break;
        }
    }
}
