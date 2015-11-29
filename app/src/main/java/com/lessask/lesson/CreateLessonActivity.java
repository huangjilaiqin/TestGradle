package com.lessask.lesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lessask.R;
import com.lessask.action.SelectActionActivity;
import com.lessask.recyclerview.ItemTouchHelperAdapter;
import com.lessask.recyclerview.ItemTouchHelperViewHolder;
import com.lessask.recyclerview.RecyclerViewDragHolder;

import java.util.ArrayList;
import java.util.Collections;

public class CreateLessonActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = CreateLessonActivity.class.getSimpleName();
    private ImageView mBack;
    private ImageView mSave;
    private EditText mName;
    private RecyclerView mActions;
    private ActionListAdapter actionsAdapter;
    private ArrayList<LessonActionInfo> datas;

    private int SELECT_ACTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mName = (EditText)findViewById(R.id.name);
        mSave = (ImageView) findViewById(R.id.save);
        mSave.setOnClickListener(this);
        mActions = (RecyclerView) findViewById(R.id.actions);
        datas = getData();
        actionsAdapter = new ActionListAdapter(this, datas);
        mActions.setAdapter(actionsAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.save:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_ACTION){
            if(resultCode == RESULT_OK){

            }
        }
    }

    private ArrayList<LessonActionInfo> getData(){
        ArrayList<LessonActionInfo> datas = new ArrayList<>();
        for(int i=0;i<20;i++){
            datas.add(new LessonActionInfo("深蹲"+i, 15, 3, 60));
        }
        return  datas;
    }

    class ActionListAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {
        private Context context;
        private ArrayList<LessonActionInfo> datas;

        ActionListAdapter(Context context, ArrayList<LessonActionInfo> datas){
            this.context = context;
            this.datas = datas;
        }


        /* to do 合并到createView
        public View getView(int position, View convertView, ViewGroup parent) {
            ActionViewHolder holder;
            if(convertView!=null){
                holder = (ActionViewHolder)convertView.getTag();
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.create_lesson_action_item, null);

                holder = new ActionViewHolder();

                ImageView vedio = (ImageView)convertView.findViewById(R.id.vedio);
                vedio.setBackgroundResource(R.drawable.vedio);
                TextView name = (TextView)convertView.findViewById(R.id.name);
                EditText tmes = (EditText)convertView.findViewById(R.id.times);
                EditText groups = (EditText)convertView.findViewById(R.id.groups);
                EditText costTime = (EditText)convertView.findViewById(R.id.cost_time);
                Button change = (Button)convertView.findViewById(R.id.change);
                change.setOnClickListener(holder);

                holder.vedio = vedio;
                holder.name = name;
                holder.times = times;
                holder.groups = groups;
                holder.costTime = costTime;

                convertView.setTag(holder);
            }

            LessonActionInfo data = datas.get(position);
            holder.name.setText(data.getName());
            holder.times.setText(data.getTimes()+"");
            holder.groups.setText(data.getGroups()+"");
            holder.costTime.setText(data.getCostTimes()+"");
            holder.position = position;

            return convertView;
        }
        */

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //获取背景菜单
            //这个控件的match_parent的大小就是item大小,如果item边缘使用margin会显示bg_menu的底色
            View mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_lesson_action_menu, null);
            mybg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            //获取item布局
            //item布局要有两层layout在第二层layout里面放控件,背景色设置为不透明，否则bg_menu会显示出来
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_lesson_action_item, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            //生成返回RecyclerView.ViewHolder
            return new MyHolder(context, mybg, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(datas, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {

        }

        class MyHolder extends RecyclerViewDragHolder implements ItemTouchHelperViewHolder {

            TextView deleteItem;

            @Override
            public void onItemSelected() {

            }

            @Override
            public void onItemClear() {

            }

            public MyHolder(Context context, View bgView, View topView,int mTrackingEdges) {
                super(context, bgView, topView,mTrackingEdges);
            }


            @Override
            public void initView(View itemView) {
                deleteItem = (TextView)itemView.findViewById(R.id.delete);

            }
        }
    }

    class ActionViewHolder implements View.OnClickListener{
        int position;
        ImageView vedio;
        TextView name;
        EditText times;
        EditText groups;
        EditText costTime;

        @Override
        public void onClick(View v) {
            Intent intent;

        }
    }
}
