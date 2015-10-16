package com.lessask.vedio;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.chat.GlobalInfos;
import com.lessask.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class SelectTagsActivity extends Activity implements View.OnClickListener{

    private final String TAG = SelectTagsActivity.class.getSimpleName();
    private final int SELECT_TAGS = 1;

    private Button mSave;
    private EditText mSelectContent;
    private TagView mTagView;
    private ListView mTagsListView;
    private LoadingDialog loadingDialog;

    private String mSelecteContentStr;
    private ArrayList<TagData> allTags;
    private ArrayList<TagData> filterTags;
    private TagsAdapter mTagsAdapter;
    private Intent intent;
    private VedioNet mVedioNet;
    private Gson gson;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private HashMap<Integer, String> vedioTags = globalInfos.getVedioTags();
    private ArrayList<TagData> selectedTagDatas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);

        loadingDialog = new LoadingDialog(this);

        gson = new Gson();
        mVedioNet = VedioNet.getInstance();
        mVedioNet.setCreateTagListener(createTagListener);
        intent = getIntent();

        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(this);
        mSelectContent = (EditText) findViewById(R.id.select_content);
        mSelectContent.clearFocus();
        mTagView = (TagView) findViewById(R.id.selected_tags);
        //初始化tagview
        selectedTagDatas = intent.getParcelableArrayListExtra("tagDatas");
        for(int i=0;i<selectedTagDatas.size();i++){
            mTagView.addTag(getTag(selectedTagDatas.get(i).getId()));
        }

        mTagsListView = (ListView) findViewById(R.id.tags);
        mSelectContent.addTextChangedListener(new TagFilterWatch());

        //to do换成网络协议
        allTags = getData();
        filterTags = new ArrayList<>();
        mTagsAdapter = new TagsAdapter(filterTags);
        mTagsListView.setAdapter(mTagsAdapter);

        filterTags(allTags, filterTags, "");

        mTagsListView.setOnItemClickListener(new OnTagItemClick(filterTags));

    }

    private VedioNet.CreateTagListener createTagListener = new VedioNet.CreateTagListener() {
        @Override
        public void createTagResponse(CreateTagResponse response) {
            Log.e(TAG, "createTag resp id:"+response.getId());
            mTagView.addTag(getTag(response.getId()));
            loadingDialog.dismiss();
        }
    };

    private ArrayList<String> getData(){
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<20;i++){
            list.add("test" + i);
        }
        return list;
    }

    private Tag getTag(int id){
        String name = vedioTags.get(id);
        Tag tag = new Tag(name);
        tag.id = id;
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

    private void filterTags(ArrayList<TagData>allTags, ArrayList<TagData>filterTags, String filter){
        filterTags.clear();
        int tagSize = allTags.size();
        boolean isEqual = false;
        for(int i=0;i<tagSize;i++){
            String tagName = allTags.get(i).getName();
            if(tagName.contains(filter)){
                filterTags.add(allTags.get(i));
                if(filter.equals(tagName)){
                    isEqual=true;
                    break;
                }
            }
        }
        if(!isEqual && filter.length()!=0){
            filterTags.add(new TagData(-1,"添加新标签 \""+filter+"\""));
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
                intent.putStringArrayListExtra("tagsName", tagsName);
                this.setResult(SELECT_TAGS, intent);
                finish();

                break;
        }

    }

    private int tagSeq = 0;
    private int getTagSeq(){
        tagSeq++;
        return tagSeq;
    }

    class OnTagItemClick implements AdapterView.OnItemClickListener{

        private ArrayList<TagData> tags;

        public OnTagItemClick(ArrayList<TagData> tags) {
            this.tags = tags;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position==filterTags.size()-1 && mTagsAdapter.getCanCreateTag()){
                //create new tag
                String newTagName = mSelectContent.getText().toString();
                CreateTagRequest request = new CreateTagRequest(globalInfos.getUserid(), newTagName, getTagSeq());
                mVedioNet.emit("createtag", gson.toJson(request));
                loadingDialog.show();
            }else {
                mTagView.addTag(getTag(tags.get(position).getId()));
            }
            filterTags(allTags, filterTags, "");
            mSelectContent.setText("");
        }
    }

    class TagsAdapter extends BaseAdapter{
        private ArrayList<TagData> tags;
        private boolean canCreateTag;

        public TagsAdapter(ArrayList<TagData> tags) {
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
            tagViewHolder.tagName.setText(tags.get(position).getName());
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
