package com.lessask.lesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class LessonsActivity extends Activity implements View.OnClickListener{

    private int CREATE_LESSON = 1;
    private ImageView mBack;
    private Button mCustomize;
    private ListView mLessons;
    private LessonListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        mBack = (ImageView)findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mCustomize = (Button)findViewById(R.id.customize);
        mCustomize.setOnClickListener(this);
        mLessons = (ListView)findViewById(R.id.lessons);
        mAdapter = new LessonListAdapter(this, getData());
        mLessons.setAdapter(mAdapter);

    }
    private ArrayList<LessonInfo> getData(){
        ArrayList<LessonInfo> datas = new ArrayList<>();
        for(int i=0;i<20;i++){
            ArrayList tags = new ArrayList();
            tags.add("持续燃脂"+i);
            tags.add("极限增肌"+i);
            datas.add(new LessonInfo("30天无敌减脂"+i, 30, "家里", tags));
        }
        return  datas;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.customize:
                Intent intent = new Intent(LessonsActivity.this, CreateLessonActivity.class);
                startActivityForResult(intent, CREATE_LESSON);
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
                Button distribute = (Button)convertView.findViewById(R.id.distribute);
                TagView tagView = (TagView)convertView.findViewById(R.id.tags);
                TextView time = (TextView)convertView.findViewById(R.id.time);
                TextView address = (TextView)convertView.findViewById(R.id.address);

                //不变的控件
                holder = new LessonViewHolder();
                holder.edit = edit;
                holder.distribute = distribute;
                holder.name = name;
                holder.time = time;
                holder.address = address;
                holder.tagView= tagView;

                convertView.setTag(holder);
            }

            LessonInfo data = datas.get(position);
            holder.name.setText(data.getName());
            holder.time.setText(data.getTime()+"分钟");
            holder.address.setText(data.getAddress());
            ArrayList<String> tags = data.getTags();
            for(int i=0;i<tags.size();i++){
                holder.tagView.addTag(getTag(tags.get(i)));
            }

            return convertView;
        }
    }

    class LessonViewHolder{
        TextView name;
        Button edit;
        Button distribute;
        TagView tagView;
        TextView time;
        TextView address;
    }
    private Tag getTag(String name){
        Tag tag = new Tag(name);
        tag.tagTextColor = R.color.main_color;
        tag.layoutColor =  Color.parseColor("#DDDDDD");
        //tag.layoutColorPress = Color.parseColor("#555555");
        //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
        tag.radius = 20f;
        tag.tagTextSize = 18f;
        tag.layoutBorderSize = 1f;
        tag.layoutBorderColor = Color.parseColor("#FFFFFF");
        tag.isDeletable = true;
        return tag;
    }
}
