package com.lessask.action;

import android.util.Log;

import com.lessask.tag.TagData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JHuang on 2015/10/17.
 */
public class ActionTagsHolder {
    private String TAG = ActionTagsHolder.class.getSimpleName();

    private ArrayList<TagData> actionTagsArray;
    private HashMap<Integer, TagData> actionTagsHash;
    public void setActionTags(ArrayList<TagData> tags){
        this.actionTagsArray = tags;
        Log.e(TAG, "tags size:"+tags.size());
        actionTagsHash = new HashMap<>();
        for (int i=0;i<tags.size();i++){
            TagData tagData = (TagData)tags.get(i);
            actionTagsHash.put(tagData.getId(), tagData);
        }
    }
    public void addActionTag(TagData tagData){
        this.actionTagsArray.add(tagData);
        this.actionTagsHash.put(tagData.getId(), tagData);
    }
    public void removeActionTag(TagData tagData){
        this.actionTagsArray.remove(tagData);
        this.actionTagsHash.remove(tagData);
    }

    public String getActionTagNameById(int id){
        TagData tagData = actionTagsHash.get(id);
        if(tagData!=null)
            return tagData.getName();
        else
            return null;
    }
    public ArrayList getActionTags(){
        return this.actionTagsArray;
    }
    public int getActionTagSize(){
        return actionTagsArray.size();
    }
    public ArrayList getActionTagsLike(String name){
        ArrayList<TagData> tagDatas = new ArrayList<>();
        for(int i=0;i<actionTagsArray.size();i++){
            TagData tagData = actionTagsArray.get(i);
            String tagName = tagData.getName();
            if(tagName.contains(name)){
                tagDatas.add(new TagData(tagData.getId(), tagData.getName()));
            }
        }
        return tagDatas;
    }
}
