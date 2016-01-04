package com.lessask.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHuang on 2015/11/28.
 */
public class ActionItem implements Parcelable {
    private String TAG = ActionItem.class.getSimpleName();
    private int id;
    private String name;        //动作名字
    private String videoName;       //视频文件名字
    private String actionImage;     //视频缩略图
    private ArrayList<Integer> tags;
    private ArrayList<String> notices;

    public ActionItem(int id,String name,String videoName,String actionImage, ArrayList<Integer> tags, ArrayList<String> notices) {
        this.id = id;
        this.name = name;
        this.videoName = videoName;
        this.actionImage = actionImage;
        this.tags = tags;
        this.notices = notices;
    }

    //只比较action的内容,不比较id
    @Override
    public boolean equals(Object o) {
        ActionItem item = (ActionItem)o;

        if(!name.equals(item.getName()) || !videoName.equals(item.getVideoName()) || tags.size()!=item.getTags().size() || notices.size()!=item.getNotices().size())
            return false;
        ArrayList<Integer> itemTags = item.getTags();
        for(int i=0;i<tags.size();i++)
            if(tags.get(i)!=itemTags.get(i))
                return false;

        ArrayList<String> itemNotices = item.getNotices();
        for (int i=0;i<notices.size();i++)
            if(!notices.get(i).equals(itemNotices.get(i)))
                return false;
        return true;
    }

    public String getActionImage() {
        return actionImage;
    }

    public void setActionImage(String actionImage) {
        this.actionImage = actionImage;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Integer> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getNotices() {
        return notices;
    }

    public void setNotices(ArrayList<String> notices) {
        this.notices = notices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(videoName);
        dest.writeString(actionImage);
        dest.writeList(tags);
        dest.writeList(notices);
    }
    public static final Parcelable.Creator<ActionItem> CREATOR
             = new Parcelable.Creator<ActionItem>() {
         public ActionItem createFromParcel(Parcel in) {
             int id = in.readInt();
             String name = in.readString();
             String videoName = in.readString();
             String actionImage = in.readString();
             ArrayList<Integer> tags = in.readArrayList(null);
             ArrayList<String> notices = in.readArrayList(null);
             return new ActionItem(id,name,videoName,actionImage,tags,notices);
         }

         public ActionItem[] newArray(int size) {
             return new ActionItem[size];
         }
    };
}
