package com.lessask.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHuang on 2015/11/28.
 */
public class ActionItem implements Parcelable {
    private int id;
    private String name;        //动作名字
    private String video;       //视频文件名字
    private ArrayList<Integer> tags;
    private ArrayList<String> notices;

    public ActionItem(int id,String name,String video, ArrayList<Integer> tags, ArrayList<String> notices) {
        this.id = id;
        this.name = name;
        this.video = video;
        this.tags = tags;
        this.notices = notices;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
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
        dest.writeString(video);
        dest.writeList(tags);
        dest.writeList(notices);
    }
    public static final Parcelable.Creator<ActionItem> CREATOR
             = new Parcelable.Creator<ActionItem>() {
         public ActionItem createFromParcel(Parcel in) {
             int id = in.readInt();
             String name = in.readString();
             String video = in.readString();
             ArrayList<Integer> tags = in.readArrayList(null);
             ArrayList<String> notices = in.readArrayList(null);
             return new ActionItem(id,name,video,tags,notices);
         }

         public ActionItem[] newArray(int size) {
             return new ActionItem[size];
         }
    };
}
