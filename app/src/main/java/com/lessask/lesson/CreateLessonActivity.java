package com.lessask.lesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lessask.R;
import com.lessask.action.SelectActionActivity;
import com.lessask.dialog.LoadingDialog;

import java.util.ArrayList;

import me.kaede.tagview.Tag;

public class CreateLessonActivity extends Activity implements View.OnClickListener{
    private String TAG = CreateLessonActivity.class.getSimpleName();
    private ImageView mBack;
    private Button mSave;
    private EditText mName;
    private ListView mActions;
    private ActionListAdapter actionsAdapter;
    private ArrayList<LessonActionInfo> datas;

    private int SELECT_ACTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        mName = (EditText)findViewById(R.id.name);
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(this);
        mActions = (ListView) findViewById(R.id.actions);
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

    class ActionListAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<LessonActionInfo> datas;

        ActionListAdapter(Context context, ArrayList<LessonActionInfo> datas){
            this.context = context;
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActionViewHolder holder;
            if(convertView!=null){
                holder = (ActionViewHolder)convertView.getTag();
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.action_item_change, null);

                holder = new ActionViewHolder();

                ImageView vedio = (ImageView)convertView.findViewById(R.id.vedio);
                vedio.setBackgroundResource(R.drawable.vedio);
                TextView name = (TextView)convertView.findViewById(R.id.name);
                EditText times = (EditText)convertView.findViewById(R.id.times);
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
            switch (v.getId()){
                case R.id.change:
                    Log.e(TAG, "change action:"+position);
                    intent = new Intent(CreateLessonActivity.this, SelectActionActivity.class);
                    startActivityForResult(intent, SELECT_ACTION);
                    break;
            }
        }
    }
}
