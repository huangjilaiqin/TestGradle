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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.lesson.LessonAction;
import com.lessask.model.ActionItem;
import com.lessask.model.GetActionResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.video.PlayVideoActiviy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SelectActionActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = SelectActionActivity.class.getSimpleName();
    private RecyclerViewStatusSupport mRecyclerView;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private ActionTagsHolder actionTagsHolder = globalInfos.getActionTagsHolder();
    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    private SelecteActionAdapter actionsAdapter;
    private Intent mIntent;

    private ArrayList<LessonAction> alreadySelected;
    private ArrayList<LessonAction> newSelected;
    private ArrayList<Integer> alreadySelectedId;
    private ArrayList<Integer> newSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getIntent();
        alreadySelectedId = new ArrayList<>();
        newSelectedId = new ArrayList<>();
        newSelected = new ArrayList<>();

        alreadySelected = mIntent.getParcelableArrayListExtra("selected");
        for (int i=0;i<alreadySelected.size();i++){
            alreadySelectedId.add(alreadySelected.get(i).getActionId());
        }

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
                Iterator<LessonAction> iterator = alreadySelected.iterator();
                while (iterator.hasNext()){
                    LessonAction lessonAction = iterator.next();
                    int actionId = lessonAction.getActionId();
                    if(!alreadySelectedId.contains(actionId)){
                        alreadySelected.remove(lessonAction);
                        iterator = alreadySelected.iterator();
                    }
                }
                for(int i=0;i<newSelectedId.size();i++){
                    int actionId = newSelectedId.get(i);
                    LessonAction lessonAction = new LessonAction(actionId,1,10,60);
                    ActionItem actionItem = globalInfos.getActionById(actionId);
                    lessonAction.setActionName(actionItem.getName());
                    lessonAction.setActionImage(actionItem.getActionImage());
                    alreadySelected.add(lessonAction);
                }
                mIntent.putParcelableArrayListExtra("selected", alreadySelected);
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
            final ActionItem actionItem = getItem(position);
            //holder.video.set
            holder.video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlayVideoActiviy.class);
                    if(actionItem.getVideoName()==null){
                        Toast.makeText(context, "file is not exist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File videoFile = new File(config.getVideoCachePath(), actionItem.getVideoName());

                    intent.putExtra("video_path", videoFile.getAbsolutePath());
                    intent.putExtra("video_url", config.getVideoUrl()+actionItem.getVideoName());
                    context.startActivity(intent);
                }
            });

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.video, R.drawable.man, R.drawable.women);
            VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + actionItem.getActionImage(), listener);

            holder.name.setText(actionItem.getName());
            ArrayList<Integer> tags = actionItem.getTags();
            StringBuilder builder = new StringBuilder();



            for(int i=0;i<tags.size();i++){
                builder.append(actionTagsHolder.getActionTagNameById(tags.get(i)));
                builder.append(" ");
            }
            holder.tags.setText(builder.toString());
            final int actionId = actionItem.getId();
            if(alreadySelectedId.contains(actionId) || newSelectedId.contains(actionId))
                holder.selecte.setChecked(true);
            else
                holder.selecte.setChecked(false);

            holder.selecte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        newSelectedId.add(actionId);
                    } else {
                        if (alreadySelectedId.contains(actionId)) {
                            alreadySelectedId.remove(new Integer(actionId));
                        } else if (newSelectedId.contains(actionId)) {
                            newSelectedId.remove(new Integer(actionId));
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
                mRecyclerView.showLoadingView();
            }

            @Override
            public void onResponse(GetActionResponse response) {
                //Log.e(TAG, "response:"+response);
                ArrayList<ActionItem> actiondatas = response.getActionDatas();
                //Log.e(TAG, "actiondatas length:" + actiondatas.size());
                globalInfos.addActions(actiondatas);
                actionsAdapter.appendToList(actiondatas);
                actionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "getactions err:" + error.toString());
                mRecyclerView.showErrorView(error.toString());
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", "" + globalInfos.getUserId());
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", "" + globalInfos.getUserId());
                return datas;
            }
        });
        volleyHelper.addToRequestQueue(getActionsRequest);
    }

}
