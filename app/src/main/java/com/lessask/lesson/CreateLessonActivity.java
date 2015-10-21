package com.lessask.lesson;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;

public class CreateLessonActivity extends Activity implements View.OnClickListener{

    private ImageView mBack;
    private Button mSave;
    private EditText mName;
    private ListView mActions;
    private ActionListAdapter actionsAdapter;
    private ArrayList<ActionInfo> datas;

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
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.save:
                break;
        }
    }

    private ArrayList<ActionInfo> getData(){
        ArrayList<ActionInfo> datas = new ArrayList<>();
        for(int i=0;i<20;i++){
            datas.add(new ActionInfo("深蹲"+i, 15, 3, 60));
        }
        return  datas;
    }

    class ActionListAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<ActionInfo> datas;

        ActionListAdapter(Context context, ArrayList<ActionInfo> datas){
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
                convertView = LayoutInflater.from(context).inflate(R.layout.action_item_edit, null);

                ImageView vedio = (ImageView)convertView.findViewById(R.id.vedio);
                vedio.setBackgroundResource(R.drawable.vedio);
                TextView name = (TextView)convertView.findViewById(R.id.name);
                EditText times = (EditText)convertView.findViewById(R.id.times);
                EditText groups = (EditText)convertView.findViewById(R.id.groups);
                EditText costTime = (EditText)convertView.findViewById(R.id.cost_time);

                //不变的控件
                holder = new ActionViewHolder();
                holder.vedio = vedio;
                holder.name = name;
                holder.times = times;
                holder.groups = groups;
                holder.costTime = costTime;

                convertView.setTag(holder);
            }

            ActionInfo data = datas.get(position);
            holder.name.setText(data.getName());
            holder.times.setText(data.getTimes()+"");
            holder.groups.setText(data.getGroups()+"");
            holder.costTime.setText(data.getCostTimes()+"");

            return convertView;
        }
    }

    class ActionViewHolder{
        ImageView vedio;
        TextView name;
        EditText times;
        EditText groups;
        EditText costTime;
    }
}
