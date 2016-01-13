package com.lessask.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.show.ShowListAdapter;
import com.lessask.show.ShowTime;

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
            rootView = inflater.inflate(R.layout.fragment_status, null);

            RecyclerViewStatusSupport recyclerView = (RecyclerViewStatusSupport)rootView.findViewById(R.id.list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLinearLayoutManager);
            recyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            //MyAdapter myAdapter = new MyAdapter();
            ShowTimeAdapter myAdapter = new ShowTimeAdapter(getContext());
            recyclerView.setAdapter(myAdapter);
            ShowTime showTime = new ShowTime();
            showTime.read(getContext(),myAdapter,recyclerView,"http://123.59.40.113/httproute/getshow1/");
        }
        return rootView;
    }

    public class MyAdapter extends BaseRecyclerAdapter<ShowTime, MyAdapter.MyHolder>{

        @Override
        public MyAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_lesson_action_item, null);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyHolder holder, int position) {
            ShowTime item = getItem(position);
            Log.e(TAG, "item:" + item.getAddress() + "," + item.getTime() + ", " + item.getContent()+","+item.getPictures());
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView name;

            public MyHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
            }
        }

    }
}
