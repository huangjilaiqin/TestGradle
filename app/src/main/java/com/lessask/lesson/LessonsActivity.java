package com.lessask.lesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.lessask.R;

import java.util.ArrayList;

public class LessonsActivity extends Activity implements View.OnClickListener{
    private String TAG = LessonsActivity.class.getSimpleName();
    private int CREATE_LESSON = 1;
    private ImageView mBack;
    private Button mCustomize;
    private ListView mLessons;
    private LessonListAdapter mAdapter;
    private EditText mSearchContent;
    private ArrayList<LessonInfo> allLessons;
    private ArrayList<LessonInfo> selectedLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        allLessons = new ArrayList<>();
        selectedLessons = new ArrayList<>();

        mBack = (ImageView)findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mCustomize = (Button)findViewById(R.id.customize);
        mCustomize.setOnClickListener(this);
        mLessons = (ListView)findViewById(R.id.lessons);
        selectedLessons = getData();
        mAdapter = new LessonListAdapter(this, selectedLessons);
        mLessons.setAdapter(mAdapter);

        mSearchContent = (EditText)findViewById(R.id.search_content);
        mSearchContent.addTextChangedListener(searchContentWatcher);

    }

    private TextWatcher searchContentWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            filterLesson(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void filterLesson(String filter){

    }

    private ArrayList<LessonInfo> getData(){
        ArrayList<LessonInfo> datas = new ArrayList<>();
        for(int i=0;i<20;i++){
            ArrayList tags = new ArrayList();
            tags.add("持续燃脂"+i);
            tags.add("极限增肌"+i);
            Log.e(TAG, "tags size:" + tags.size());
            datas.add(new LessonInfo("30天无敌减脂" + i, 30, "家里", tags));
        }
        return  datas;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.customize:
                intent = new Intent(LessonsActivity.this, CreateLessonActivity.class);
                startActivityForResult(intent, CREATE_LESSON);
                break;
            case R.id.edit:
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    class LessonListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<LessonInfo> datas;

        LessonListAdapter(Context context, ArrayList<LessonInfo> datas){
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
            LessonViewHolder holder;
            if(convertView!=null){
                holder = (LessonViewHolder)convertView.getTag();
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.lesson_item, null);

                TextView name = (TextView)convertView.findViewById(R.id.name);
                Button edit = (Button)convertView.findViewById(R.id.edit);
                edit.setOnClickListener(LessonsActivity.this);
                Button distribute = (Button)convertView.findViewById(R.id.distribute);
                TextView tags = (TextView)convertView.findViewById(R.id.tags);
                TextView time = (TextView)convertView.findViewById(R.id.time);
                TextView address = (TextView)convertView.findViewById(R.id.address);

                //不变的控件
                holder = new LessonViewHolder();
                holder.edit = edit;
                holder.distribute = distribute;
                holder.name = name;
                holder.time = time;
                holder.address = address;
                holder.tags = tags;

                convertView.setTag(holder);
            }

            LessonInfo data = datas.get(position);
            holder.name.setText(data.getName());
            holder.time.setText(data.getTime()+"分钟");
            holder.address.setText(data.getAddress());
            ArrayList<String> tags = data.getTags();
            StringBuilder builder = new StringBuilder();
            for(int i=0;i<tags.size();i++){
                builder.append(tags.get(i));
                builder.append("  ");
            }
            holder.tags.setText(builder.toString());

            return convertView;
        }
    }

    class LessonViewHolder{
        TextView name;
        Button edit;
        Button distribute;
        TextView tags;
        TextView time;
        TextView address;
    }
}
