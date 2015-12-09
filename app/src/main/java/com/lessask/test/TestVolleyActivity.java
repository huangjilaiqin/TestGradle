package com.lessask.test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lessask.R;
import com.lessask.action.HandleActionResponse;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.model.GetActionResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestVolleyActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = TestVolleyActivity.class.getSimpleName();
    private Button start;
    private TextView show;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_volley);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.start).setOnClickListener(this);
        show = (TextView)findViewById(R.id.show);
    }

    StringRequest str;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                VolleyHelper.setmCtx(this);
                VolleyHelper volleyHelper = VolleyHelper.getInstance();

                GsonRequest gsonRequest = new GsonRequest<>(Request.Method.POST, config.getActioinsUrl(), GetActionResponse.class, new GsonRequest.PostGsonRequest<GetActionResponse>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(GetActionResponse response) {
                        Log.e(TAG, "onResponse");
                        ArrayList<ActionItem> datas = response.getActionDatas();
                        for(int i=0;i<datas.size();i++){
                            ActionItem data = datas.get(i);
                            Log.e(TAG, ""+data.getName());
                            show.setText("" + data.getName());
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.e(TAG, error.getMessage());

                    }

                    @Override
                    public void setPostData(Map datas) {
                        datas.put("userid", ""+globalInfos.getUserid());
                    }
                });
                volleyHelper.addToRequestQueue(gsonRequest);
                break;
        }
    }
}
