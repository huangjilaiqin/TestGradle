package com.lessask.action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.model.GetActionResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.util.ArrayList;
import java.util.Map;

public class SelectActionActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = SelectActionActivity.class.getSimpleName();
    private RecyclerViewStatusSupport mRecyclerView;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private ActionTagsHolder actionTagsHolder = globalInfos.getActionTagsHolder();
    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    private SelecteActionAdapter actionsAdapter;
    private ArrayList<ActionInfo> allActions;
    private ArrayList<ActionInfo> selectedActions;
    private Intent mIntent;

    private ArrayList<Integer> alreadySelected;
    private ArrayList<Integer> newSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        alreadySelected = mIntent.getIntegerArrayListExtra("selected");
        newSelected = new ArrayList<>();

        setContentView(R.layout.activity_select_action);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.done).setOnClickListener(this);

        mRecyclerView = (RecyclerViewStatusSupport)findViewById(R.id.actions);
        mRecyclerView.setStatusViews(findViewById(R.id.loading_view), findViewById(R.id.empty_view), findViewById(R.id.error_view));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        actionsAdapter = new SelecteActionAdapter(this);
        mRecyclerView.setAdapter(actionsAdapter);
        //to do 本地化查询
        ArrayList<ActionItem> actionItems = globalInfos.getActions();
        if(actionItems!=null && actionItems.size()>0){
            actionsAdapter.appendToList(actionItems);
            actionsAdapter.notifyDataSetChanged();
        }else
            loadAtions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.done:
                mIntent.putIntegerArrayListExtra("old_selected", alreadySelected);
                mIntent.putIntegerArrayListExtra("new_selected", newSelected);
                setResult(RESULT_OK, mIntent);
                finish();
                break;
        }
    }

    class SelecteActionAdapter extends BaseRecyclerAdapter<ActionItem, SelecteActionAdapter.MyHolder>{
        private Context context;

        public SelecteActionAdapter(Context context){
            this.context = context;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_item_selecte, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {
            ActionItem actionItem = getItem(position);
            //holder.video.set
            holder.name.setText(actionItem.getName());
            ArrayList<Integer> tags = actionItem.getTags();
            StringBuilder builder = new StringBuilder();

            for(int i=0;i<tags.size();i++){
                Log.e(TAG, "id:"+tags.get(i));
                Log.e(TAG, "id:"+actionTagsHolder.getActionTagNameById(tags.get(i)));
                builder.append(actionTagsHolder.getActionTagNameById(tags.get(i)));
                builder.append(" ");
            }
            holder.tags.setText(builder.toString());
            final int actionId = actionItem.getId();
            if(alreadySelected.contains(actionId) || newSelected.contains(actionId))
                holder.selecte.setChecked(true);
            else
                holder.selecte.setChecked(false);

            holder.selecte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        newSelected.add(actionId);
                    } else {
                        if (alreadySelected.contains(actionId)) {
                            alreadySelected.remove(new Integer(actionId));
                        } else if (newSelected.contains(actionId)) {
                            newSelected.remove(new Integer(actionId));
                        }
                    }
                }
            });
        }
        class MyHolder extends RecyclerView.ViewHolder{
            ImageView video;
            TextView name;
            TextView tags;
            CheckBox selecte;

            public MyHolder(View itemView) {
                super(itemView);
                video = (ImageView)itemView.findViewById(R.id.video);
                name = (TextView)itemView.findViewById(R.id.name);
                tags = (TextView)itemView.findViewById(R.id.tags);
                selecte = (CheckBox)itemView.findViewById(R.id.selecte);
            }
        }
    }
    private void loadAtions(){
        GsonRequest getActionsRequest = new GsonRequest<>(Request.Method.POST, config.getActioinsUrl(), GetActionResponse.class, new GsonRequest.PostGsonRequest<GetActionResponse>() {
            @Override
            public void onStart() {
                Log.e(TAG, "start getactions");
                mRecyclerView.showLoadingView();
            }

            @Override
            public void onResponse(GetActionResponse response) {
                Log.e(TAG, "response:"+response);
                ArrayList<ActionItem> actiondatas = response.getActionDatas();
                Log.e(TAG, "actiondatas length:" + actiondatas.size());
                globalInfos.addActions(actiondatas);
                actionsAdapter.appendToList(actiondatas);
                actionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "getactions err:"+error.toString());
                mRecyclerView.showErrorView(error.toString());
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", "" + globalInfos.getUserId());
            }
        });
        volleyHelper.addToRequestQueue(getActionsRequest);
    }

}
