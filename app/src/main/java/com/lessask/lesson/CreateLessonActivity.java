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
import android.widget.Toast;

import com.hedgehog.ratingbar.RatingBar;
import com.lessask.R;
import com.lessask.action.SelectActionActivity;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.dialog.TagsPickerDialog;
import com.lessask.recyclerview.ItemTouchHelperAdapter;
import com.lessask.recyclerview.ItemTouchHelperViewHolder;
import com.lessask.recyclerview.RecyclerViewDragHolder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateLessonActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = CreateLessonActivity.class.getSimpleName();

    private EditText mName;
    private ImageView mCover;
    private TextView mPurpose;
    private TextView mBodies;
    private TextView mAddress;
    private TextView mCosttime;
    private RatingBar mFatBar;
    private RatingBar mMuscleBar;

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

        mCover = (ImageView)findViewById(R.id.cover);
        mName = (EditText)findViewById(R.id.name);
        mPurpose  = (EditText)findViewById(R.id.purpose);
        mBodies = (EditText)findViewById(R.id.bodies);
        mAddress = (EditText)findViewById(R.id.address);
        mCosttime = (EditText)findViewById(R.id.costtime);

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        mCover.setOnClickListener(this);
        mPurpose.setOnClickListener(this);
        mBodies.setOnClickListener(this);
        mAddress.setOnClickListener(this);
        mCosttime.setOnClickListener(this);

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
            case R.id.purpose:
                String[] purposeValues = {"增肌", "减脂", "塑形"};
                StringPickerDialog dialog = new StringPickerDialog(CreateLessonActivity.this, purposeValues, new StringPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(String data) {
                        mPurpose.setText(data);
                    }
                });
                dialog.show();
                break;
            case R.id.bodies:

                String[] bodiesValues = {"胸部","背部","腰部","臀部","大腿","小腿"};
                ArrayList<String> values1 = new ArrayList<>();
                for(int i=0;i<bodiesValues.length;i++)
                    values1.add(bodiesValues[i]);
                int[] selected = {2,5,6};
                TagsPickerDialog dialog1 = new TagsPickerDialog(CreateLessonActivity.this, values1, new TagsPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(List data) {
                        String resulte = "";
                        for(int i=0;i<data.size();i++){
                            resulte+=","+data.get(i);
                        }
                        Toast.makeText(CreateLessonActivity.this, resulte, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog1.setSelectedList(selected, 2);
                dialog1.show();
                break;
            case R.id.address:
                String[] addressValues = {"健身房", "家里", "公园"};
                StringPickerDialog addressDialog = new StringPickerDialog(CreateLessonActivity.this, purposeValues, new StringPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(String data) {
                        mAddress.setText(data);
                    }
                });
                addressDialog.show();
                break;
            case R.id.costtime:
                break;
            case R.id.cover:
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
