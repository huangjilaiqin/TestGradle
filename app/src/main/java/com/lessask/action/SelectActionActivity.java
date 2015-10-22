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
    private ImageView mBack = (ImageView)findViewById(R.id.back);
    private ListView mActions = (ListView)findViewById(R.id.actions);
    private ActionListAdapter actionsAdapter;
    private ArrayList<ActionInfo> allActions;
    private ArrayList<ActionInfo> selectedActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
    }

    @Override
    public void onClick(View v) {

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

                TextView name = (TextView)convertView.findViewById(R.id.name);
                Button select = (Button)convertView.findViewById(R.id.select);
                select.setOnClickListener(SelectActionActivity.this);
                TextView tags = (TextView)convertView.findViewById(R.id.tags);

                //不变的控件
                holder = new ActionViewHolder();
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
