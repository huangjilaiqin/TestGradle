package com.lessask.lesson;

import android.app.Activity;
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

import com.github.captain_miao.recyclerviewutils.EndlessRecyclerOnScrollListener;
import com.lessask.DividerItemDecoration;
import com.lessask.OnItemClickListener;
import com.lessask.R;
import com.lessask.model.LessonItem;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/11/24.
 */
public class FragmentLesson extends Fragment{
    private View rootView;
    private final String TAG = FragmentLesson.class.getName();
    private LessonAdapter mRecyclerViewAdapter;
    //private LessonAdapter2 mRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loadBackward;
    private int EDIT_LESSON = 0;
    private ArrayList<LessonItem> getData(){
        ArrayList<LessonItem> datas = new ArrayList<>();
        for(int i=0;i<20;i++){
            ArrayList tags = new ArrayList();
            tags.add("持续燃脂"+i);
            tags.add("极限增肌"+i);
            //Log.e(TAG, "tags size:" + tags.size());
            datas.add(new LessonItem("30天无敌减脂" + i, 30, "家里", tags));
        }
        return  datas;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_lesson, null);
            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.lesson_list);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setClickable(true);

            //mRecyclerViewAdapter = new LessonAdapter(getContext());
            mRecyclerViewAdapter = new LessonAdapter(getContext());
            mRecyclerViewAdapter.setHasMoreData(true);
            mRecyclerViewAdapter.setHasFooter(false);
            //数据
            mRecyclerViewAdapter.appendToList(getData());
            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //Toast.makeText(getContext(), "onClick:" + position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), CreateLessonActivity.class);
                    startActivityForResult(intent, EDIT_LESSON);
                }
            });
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
                @Override
                public void onLoadMore(int current_page) {
                    //不用将footer隐藏, 因为这个控件是通过item个数来判断是否进行下一次加载,目前发现好像不是很可靠
                    //不用担心footer被看见，因为加载成功后footer就在后面了,当它出现时就是进行下一次加载的时候了
                    //to do 加载失败怎么办
                    if (loadBackward) {
                        Log.e(TAG, "loadBackward ing");
                        //return;
                    } else {
                        loadBackward = true;
                        mRecyclerViewAdapter.setHasFooter(true);
                    }
                }
            });


            mRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "click");
                    Toast.makeText(getActivity().getBaseContext(), "click", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == EDIT_LESSON) {
            Toast.makeText(getContext(), "edit lesson done", Toast.LENGTH_SHORT).show();
        }
    }
}
