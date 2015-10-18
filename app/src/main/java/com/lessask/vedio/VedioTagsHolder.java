package com.lessask.vedio;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JHuang on 2015/10/17.
 */
public class VedioTagsHolder {
    private String TAG = VedioTagsHolder.class.getSimpleName();

    private ArrayList<TagData> vedioTagsArray;
    private HashMap<Integer, TagData> vedioTagsHash;
    public void setVedioTags(ArrayList<TagData> tags){
        this.vedioTagsArray = tags;
        Log.e(TAG, "tags size:"+tags.size());
        vedioTagsHash = new HashMap<>();
        for (int i=0;i<tags.size();i++){
            TagData tagData = (TagData)tags.get(i);
            vedioTagsHash.put(tagData.getId(), tagData);
        }
    }
    public void addVedioTag(TagData tagData){
        this.vedioTagsArray.add(tagData);
        this.vedioTagsHash.put(tagData.getId(), tagData);
    }
    public void removeVedioTag(TagData tagData){
        this.vedioTagsArray.remove(tagData);
        this.vedioTagsHash.remove(tagData);
    }

    public String getVedioTagNameById(int id){
        TagData tagData = vedioTagsHash.get(id);
        if(tagData!=null)
            return tagData.getName();
        else
            return null;
    }
    public ArrayList getVedioTags(){
        return this.vedioTagsArray;
    }
    public int getVedioTagSize(){
        return vedioTagsArray.size();
    }
    public ArrayList getVedioTagsLike(String name){
        ArrayList<TagData> tagDatas = new ArrayList<>();
        for(int i=0;i<vedioTagsArray.size();i++){
            TagData tagData = vedioTagsArray.get(i);
            String tagName = tagData.getName();
            if(tagName.contains(name)){
                tagDatas.add(new TagData(tagData.getId(), tagData.getName()));
            }
        }
        return tagDatas;
    }
}
