package com.lessask.vedio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import me.kaede.tagview.OnTagDeleteListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class SelectTagsActivity extends Activity implements View.OnClickListener{

    private final String TAG = SelectTagsActivity.class.getSimpleName();
    private final int SELECT_TAGS = 1;
    private final int ON_GETTAGS =2;
    private final int ON_CREATETAGS =3;

    private Button mSave;
    private EditText mSelectContent;
    private TagView mTagView;
    private ListView mTagsListView;
    private LoadingDialog loadingDialog;

    private String mSelecteContentStr;
    private ArrayList<TagData> filteredTags;
    private TagsAdapter mTagsAdapter;
    private Intent intent;
    private VedioNet mVedioNet;
    private Gson gson;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private ArrayList<TagData> selectedTagDatas;
    private ArrayList<TagData> originSelectedTagDatas;
    private VedioTagsHolder vedioTagsHolder = globalInfos.getVedioTagsHolder();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ON_CREATETAGS:
                    CreateTagResponse response = (CreateTagResponse) msg.obj;
                    int tagId = response.getId();
                    String tagName = response.getName();
                    //更新总的tags
                    vedioTagsHolder.addVedioTag(new TagData(tagId, tagName));
                    filterTags(filteredTags, "");

                    //更新选中视图
                    mTagView.addTag(getTag(tagId));
                    //更新选中数据
                    selectedTagDatas.add(new TagData(tagId, tagName));
                    loadingDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);

        loadingDialog = new LoadingDialog(this);

        gson = new Gson();
        mVedioNet = VedioNet.getInstance();
        mVedioNet.setCreateTagListener(createTagListener);
        //mVedioNet.setGetTagsListener(getTagsListener);
        intent = getIntent();

        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(this);
        mSelectContent = (EditText) findViewById(R.id.select_content);
        mSelectContent.clearFocus();
        mTagView = (TagView) findViewById(R.id.selected_tags);
        mTagView.setOnTagDeleteListener(new OnTagDeleteListener() {
            @Override
            public void onTagDeleted(Tag tag, int position) {
                String tagName = vedioTagsHolder.getVedioTagNameById(tag.id);
                Log.e(TAG, "tagName "+tagName);
                TagData tagData = new TagData(tag.id, tagName);
                Log.e(TAG, "before "+selectedTagDatas.size());
                for (int i=0;i< selectedTagDatas.size();i++){
                    if(selectedTagDatas.get(i).getName().equals(tagName)){
                        selectedTagDatas.remove(i);
                        break;
                    }
                }
                Log.e(TAG, "after "+selectedTagDatas.size());
                //selectedTagDatas.remove(tagData);
            }
        });
        //初始化tagview
        selectedTagDatas = intent.getParcelableArrayListExtra("tagDatas");
        originSelectedTagDatas = (ArrayList<TagData>)selectedTagDatas.clone();

        for(int i=0;i<selectedTagDatas.size();i++){
            mTagView.addTag(getTag(selectedTagDatas.get(i).getId()));
        }

        mTagsListView = (ListView) findViewById(R.id.tags);
        mSelectContent.addTextChangedListener(new TagFilterWatch());


        filteredTags = new ArrayList<>();
        mTagsAdapter = new TagsAdapter(SelectTagsActivity.this, filteredTags);
        filterTags(filteredTags, "");
        mTagsListView.setAdapter(mTagsAdapter);

        mTagsListView.setOnItemClickListener(new OnTagItemClick(filteredTags));
    }

    private ArrayList<TagData> getData(){
        ArrayList<TagData> list = new ArrayList<>();
        for (int i=0;i<5;i++){
            list.add(new TagData(i, "测试"+i));
        }
        return list;
    }


    private VedioNet.CreateTagListener createTagListener = new VedioNet.CreateTagListener() {
        @Override
        public void createTagResponse(CreateTagResponse response) {
            Log.e(TAG, "createTag resp id:" + response.getId());
            Message msg = new Message();
            msg.what = ON_CREATETAGS;
            msg.obj = response;
            handler.sendMessage(msg);

        }
    };

    private Tag getTag(int id){
        String name = vedioTagsHolder.getVedioTagNameById(id);
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

    private void filterTags(ArrayList<TagData>filteredTags, String filter){
        Log.e(TAG, "filterStr:" + filter);
        filteredTags.clear();
        ArrayList<TagData> nameLikeTagDatas = vedioTagsHolder.getVedioTagsLike(filter);
        filteredTags.addAll(nameLikeTagDatas);
        boolean isEqual = false;
        for(int i=0;i<nameLikeTagDatas.size();i++){
            TagData tagData = nameLikeTagDatas.get(i);
            String tagName = tagData.getName();
            if(filter.equals(tagName)){
                isEqual=true;
                break;
            }
        }
        if(!isEqual && filter.length()!=0){
            filteredTags.add(new TagData(-1,"添加新标签 \""+filter+"\""));
            mTagsAdapter.setCanCreateTag(true);
        }else {
            mTagsAdapter.setCanCreateTag(false);
        }
        //更新视图
        Log.e(TAG, "notifyDataSetChanged");
        mTagsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                intent.putParcelableArrayListExtra("tagDatas", selectedTagDatas);
                Log.e(TAG, "save "+selectedTagDatas);
                this.setResult(SELECT_TAGS, intent);
                finish();
                break;
            case R.id.back:
                intent.putParcelableArrayListExtra("tagDatas", originSelectedTagDatas);
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
            if(position==filteredTags.size()-1 && mTagsAdapter.getCanCreateTag()){
                //create new tag
                String newTagName = mSelectContent.getText().toString();
                CreateTagRequest request = new CreateTagRequest(globalInfos.getUserid(), newTagName, getTagSeq());
                mVedioNet.emit("createtag", gson.toJson(request));
                loadingDialog.show();
            }else {
                TagData tag = tags.get(position);
                mTagView.addTag(getTag(tag.getId()));
                selectedTagDatas.add(new TagData(tag.getId(), tag.getName()));
            }
            filterTags(filteredTags, "");
            mSelectContent.setText("");
        }
    }

    class TagsAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<TagData> tags;
        private boolean canCreateTag;

        public TagsAdapter(Context context, ArrayList<TagData> tags) {
            this.context = context;
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TagViewHolder tagViewHolder;
            if(convertView!=null){
                tagViewHolder = (TagViewHolder)convertView.getTag();
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.tag_item, null);

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
            filterTags(filteredTags, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}