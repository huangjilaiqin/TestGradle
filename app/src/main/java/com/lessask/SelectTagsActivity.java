package com.lessask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class SelectTagsActivity extends Activity {

    private Button mSave;
    private EditText mSelectContent;
    private TagView mTagView;
    private ListView mAllTags;

    private String mSelecteContentStr;
    private ArrayList<String> allTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);

        mSave = (Button) findViewById(R.id.save);
        mSelectContent = (EditText) findViewById(R.id.select_content);
        mTagView = (TagView) findViewById(R.id.selected_tags);
        mAllTags = (ListView) findViewById(R.id.tags);


        mAllTags.setAdapter(new ArrayAdapter<String>(SelectTagsActivity.this, R.layout.tag_item));

        for(int i=0;i<5;i++){
            Tag tag = new Tag("深蹲"+i);
            tag.tagTextColor = R.color.main_color;
            tag.layoutColor =  Color.parseColor("#DDDDDD");
            tag.layoutColorPress = Color.parseColor("#555555");
            //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
            tag.radius = 20f;
            tag.tagTextSize = 18f;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = Color.parseColor("#FFFFFF");
            tag.isDeletable = true;
            mTagView.addTag(tag);
        }
    }

    class TagsAdapter extends BaseAdapter{
        private ArrayList<String> tags;
        private boolean canCreateTag;

        public TagsAdapter(ArrayList<String> tags) {
            this.tags = tags;
        }

        public void setCanCreateTag(boolean canCreateTag){
            this.canCreateTag = canCreateTag;
        }

        @Override
        public int getCount() {
            return tags.size();
        }

        @Override
        public Object getItem(int position) {
            return tags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TagViewHolder tagViewHolder = new TagViewHolder();
            TextView tagItem;
            if(convertView!=null){
                tagViewHolder = (TagViewHolder)convertView.getTag();
            }else {
                convertView = LayoutInflater.from(SelectTagsActivity.this).inflate(R.layout.tag_item, null);

            }
            tagViewHolder.tagName.setText(tags.get(position));
            if(position==tags.size()-1 && canCreateTag){
                tagViewHolder.tagName.setTextColor(getResources().getColor(R.color.main_color));
            }else {
                tagViewHolder.tagName.setTextColor(getResources().getColor(R.color.black));
            }
            return convertView;
        }
    }
    class TagViewHolder{
        TextView tagName;
    }


}
