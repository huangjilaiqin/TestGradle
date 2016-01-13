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

import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.crud.CRUD;
import com.lessask.crud.DefaultGsonRequestCRUD;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.Workout;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.show.ShowListAdapter;
import com.lessask.show.ShowTime;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHuang on 2015/10/22.
 */
public class FragmentStatus extends Fragment implements CRUD<Workout>{
    private String TAG = FragmentStatus.class.getSimpleName();
    private View rootView;
    private ShowTimeAdapter myAdapter;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

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
            myAdapter = new ShowTimeAdapter(getContext());
            recyclerView.setAdapter(myAdapter);
            //ShowTime showTime = new ShowTime();
            //showTime.read(getContext(),myAdapter,recyclerView,"http://123.59.40.113/httproute/getshow1/");
            read(recyclerView);
        }
        return rootView;
    }

    @Override
    public void create(Workout obj, int position) {

    }

    @Override
    public void read(RecyclerViewStatusSupport recyclerView) {
        DefaultGsonRequestCRUD  crud = new DefaultGsonRequestCRUD();
        Type type = new TypeToken<ArrayListResponse<ShowTime>>() {}.getType();
        Map datas = new HashMap();
        datas.put("userid", globalInfos.getUserId() + "");
        datas.put("pagenum", "10");
        crud.read(getContext(), myAdapter, recyclerView, "http://123.59.40.113/httproute/getshow1/", type, datas);
    }

    @Override
    public void update(Workout obj, int position) {

    }

    @Override
    public void delete(Workout obj, int position) {

    }
}
