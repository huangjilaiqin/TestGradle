package com.lessask.action;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lessask.R;

import java.util.ArrayList;

public class SelectActionActivity extends Activity implements View.OnClickListener{
    private String TAG = SelectActionActivity.class.getSimpleName();
    private ImageView mBack;
    private ListView mActions;
    private ActionListAdapter actionsAdapter;
    private ArrayList<ActionInfo> allActions;
    private ArrayList<ActionInfo> selectedActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
        mBack = (ImageView)findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mActions = (ListView)findViewById(R.id.actions);
        selectedActions = getData();
        actionsAdapter = new ActionListAdapter(this, selectedActions);
        mActions.setAdapter(actionsAdapter);
    }

    private ArrayList<ActionInfo> getData(){
        ArrayList<ActionInfo> datas =new ArrayList<>();
        for(int i=0;i<20;i++){
            ArrayList<Integer> tags = new ArrayList<>();
            tags.add(i+1);
            tags.add(i+2);
            ArrayList<String> notices = new ArrayList<>();
            notices.add("注意事项"+i);
            notices.add("注意事项"+i+1);
            datas.add(new ActionInfo("深蹲"+i, tags, notices, "123.mp4"));
        }
        return  datas;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }

    }

    class ActionListAdapter extends BaseAdapter {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.action_item_selecte, null);
                holder = new ActionViewHolder();

                TextView name = (TextView)convertView.findViewById(R.id.name);
                Button select = (Button)convertView.findViewById(R.id.select);
                select.setOnClickListener(holder);
                TextView tags = (TextView)convertView.findViewById(R.id.tags);

                //不变的控件
                holder.select = select;
                holder.name = name;
                holder.tags = tags;

                convertView.setTag(holder);
            }

            ActionInfo data = datas.get(position);
            holder.name.setText(data.getName());
            ArrayList<Integer> tags = data.getTags();
            StringBuilder builder = new StringBuilder();
            for(int i=0;i<tags.size();i++){
                builder.append(tags.get(i));
                builder.append("  ");
            }
            holder.tags.setText(builder.toString());
            holder.position = position;

            return convertView;
        }
    }


    class ActionViewHolder implements View.OnClickListener{
        int position;
        TextView name;
        Button select;
        TextView tags;

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.select:
                    Log.e(TAG, "selecteAction position:"+position);
                    finish();
                    break;
            }
        }
    }
}
