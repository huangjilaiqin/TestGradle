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

import java.util.ArrayList;

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

            RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycle);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLinearLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            MyAdapter myAdapter = new MyAdapter();
            myAdapter.appendToList(getData());
            recyclerView.setAdapter(myAdapter);
        }
        return rootView;
    }

    public ArrayList<String> getData(){
        ArrayList<String> datas = new ArrayList<>();
        for(int i=0;i<20;i++){
            datas.add(i+"");
        }
        return datas;
    }

    public class MyAdapter extends BaseRecyclerAdapter<String, MyAdapter.MyHolder>{

        @Override
        public MyAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_lesson_action_item, null);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyHolder holder, int position) {
            holder.name.setText(getItem(position));
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView name;

            public MyHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
            }
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }
    }

}
