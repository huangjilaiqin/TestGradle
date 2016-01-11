package com.lessask.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lessask.R;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Lesson;
import com.lessask.model.Workout;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class SelectLessonActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = SelectLessonActivity.class.getSimpleName();
    private RecyclerViewStatusSupport mRecyclerView;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    private SelectLessonAdapter mAdapter;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.done).setOnClickListener(this);

        mRecyclerView = (RecyclerViewStatusSupport)findViewById(R.id.lessons);
        mRecyclerView.setStatusViews(findViewById(R.id.loading_view), findViewById(R.id.empty_view), findViewById(R.id.error_view));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new SelectLessonAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        loadLessons();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.done:
                Workout workout = new Workout();
                mIntent.putExtra("workout", workout);
                setResult(RESULT_OK, mIntent);
                finish();
                break;
        }
    }

    private void loadLessons(){
        Type type = new TypeToken<ArrayList<Lesson>>() {}.getType();
        GsonRequest  gsonRequest = new GsonRequest<ArrayList<Lesson>>(Request.Method.POST,config.getLessonsUrl(),type,new GsonRequest.PostGsonRequest<ArrayList<Lesson>>(){
            @Override
            public void onStart() {
                mRecyclerView.showLoadingView();
            }

            @Override
            public void onResponse(ArrayList<Lesson> response) {
                mAdapter.appendToList(response);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                mRecyclerView.showErrorView(error.toString());
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userId", "" + globalInfos.getUserId());
            }
        });
        volleyHelper.addToRequestQueue(gsonRequest);
    }

}
