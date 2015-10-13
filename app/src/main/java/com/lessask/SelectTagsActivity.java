package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class SelectTagsActivity extends Activity implements View.OnClickListener{

    private final String TAG = SelectTagsActivity.class.getSimpleName();
    private final int SELECT_TAGS = 1;

    private Button mSave;
    private EditText mSelectContent;
    private TagView mTagView;
    private ListView mAllTags;

    private String mSelecteContentStr;
    private ArrayList<String> allTags;
    private ArrayList<String> filterTags;
    private TagsAdapter mTagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);

        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(this);
        mSelectContent = (EditText) findViewById(R.id.select_content);
        mTagView = (TagView) findViewById(R.id.selected_tags);
        mAllTags = (ListView) findViewById(R.id.tags);
        mSelectContent.addTextChangedListener(new TagFilterWatch());

        allTags = getData();
        filterTags = new ArrayList<>();
        mTagsAdapter = new TagsAdapter(filterTags);
        mAllTags.setAdapter(mTagsAdapter);

        filterTags(allTags, filterTags, "");

        mAllTags.setOnItemClickListener(new OnTagItemClick(filterTags));

        for(int i=0;i<5;i++){
            mTagView.addTag(getTag("深蹲"+i));
        }
    }

    private ArrayList<String> getData(){
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<20;i++){
            list.add("test" + i);
        }
        return list;
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

    private void filterTags(ArrayList<String>allTags, ArrayList<String>filterTags, String filter){
        filterTags.clear();
        int tagSize = allTags.size();
        boolean isEqual = false;
        for(int i=0;i<tagSize;i++){
            String tagName = allTags.get(i);
            if(tagName.contains(filter)){
                filterTags.add(tagName);
                if(filter.equals(tagName)){
                    isEqual=true;
                    break;
                }
            }
        }
        if(!isEqual && filter.length()!=0){
            filterTags.add("添加新标签 \""+filter+"\"");
            mTagsAdapter.setCanCreateTag(true);
        }else {
            mTagsAdapter.setCanCreateTag(false);
        }
        //更新视图
        mTagsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                List<Tag> tags = mTagView.getTags();
                ArrayList<String> tagsName = new ArrayList<>();
                for (int i=0;i<tags.size();i++){
                    tagsName.add(tags.get(i).text);
                }
                Intent intent = getIntent();
                intent.putStringArrayListExtra("tagsName", tagsName);
                this.setResult(SELECT_TAGS, intent);
                finish();

                break;
        }

    }

    class OnTagItemClick implements AdapterView.OnItemClickListener{

        private ArrayList<String> tags;

        public OnTagItemClick(ArrayList<String> tags) {
            this.tags = tags;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position==filterTags.size()-1 && mTagsAdapter.getCanCreateTag()){
                mTagView.addTag(getTag(mSelectContent.getText().toString()));
            }else {
                mTagView.addTag(getTag(tags.get(position)));
            }
            filterTags(allTags, filterTags, "");
            mSelectContent.setText("");
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
        public boolean getCanCreateTag(){
            return canCreateTag;
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
            TagViewHolder tagViewHolder;
            TextView tagItem;
            if(convertView!=null){
                tagViewHolder = (TagViewHolder)convertView.getTag();
            }else {
                convertView = LayoutInflater.from(SelectTagsActivity.this).inflate(R.layout.tag_item, null);

                TextView textView = (TextView)convertView.findViewById(R.id.tag_item_textview);
                tagViewHolder = new TagViewHolder();
                tagViewHolder.tagName = textView;

                convertView.setTag(tagViewHolder);
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
    class TagFilterWatch implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e(TAG, "onTextChanged:"+s);
            filterTags(allTags, filterTags, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
