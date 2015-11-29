package com.lessask.action;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lessask.DividerItemDecoration;
import com.lessask.MainActivity;
import com.lessask.OnItemClickListener;
import com.lessask.R;
import com.lessask.model.ActionItem;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/28.
 */
public class FragmentAction extends Fragment{
    private View rootView;
    private final String TAG = FragmentAction.class.getName();
    private ActionAdapter mRecyclerViewAdapter;
    //private LessonAdapter2 mRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<ActionItem> getData(){
        ArrayList<ActionItem> datas = new ArrayList<>();
        for(int i=0;i<10;i++){
            ArrayList<Integer> tags = new ArrayList();
            tags.add(i);
            //Log.e(TAG, "tags size:" + tags.size());

            ArrayList<String> notices = new ArrayList<>();
            notices.add("与肩同宽"+i);
            notices.add("背部挺直"+i);
            datas.add(new ActionItem("臀桥" + i, tags, notices));
        }
        return  datas;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_action, null);
            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.lesson_list);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setClickable(true);

            //mRecyclerViewAdapter = new LessonAdapter(getContext());
            mRecyclerViewAdapter = new ActionAdapter(getContext());
            mRecyclerViewAdapter.setHasMoreData(true);
            mRecyclerViewAdapter.setHasFooter(false);
            //数据
            mRecyclerViewAdapter.appendToList(getData());
            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(getContext(), "onClick:" + position, Toast.LENGTH_SHORT).show();
                    /*
                    Intent intent = new Intent(getActivity(), EditActionActivity.class);
                    startActivityForResult(intent, MainActivity.EDIT_ACTION);
                    */
                }
            });
            mRecyclerView.setAdapter(mRecyclerViewAdapter);


            mRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "click");
                    Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
                }
            });
            mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.e(TAG, "onLongClick");
                    Toast.makeText(getActivity(), "longClick", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        return rootView;
    }
}
