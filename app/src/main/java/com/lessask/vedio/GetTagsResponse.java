package com.lessask.vedio;

import com.lessask.model.ResponseError;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/10/16.
 */
public class GetTagsResponse extends ResponseError{
    private ArrayList<TagData> tagDatas;

    public GetTagsResponse(ArrayList<TagData> tagDatas) {
        this.tagDatas = tagDatas;
    }

    public GetTagsResponse(int errno, String error, ArrayList<TagData> tagDatas) {
        super(errno, error);
        this.tagDatas = tagDatas;
    }

    public ArrayList<TagData> getTagDatas() {
        return tagDatas;
    }

    public void setTagDatas(ArrayList<TagData> tagDatas) {
        this.tagDatas = tagDatas;
    }
}
