package com.lessask.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/28.
 */
public class ActionItem implements Parcelable {
    private String name;
    private ArrayList<Integer> tags;
    private ArrayList<String> notices;

    public ActionItem(String name, ArrayList<Integer> tags, ArrayList<String> notices) {
        this.name = name;
        this.tags = tags;
        this.notices = notices;
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
        dest.writeString(name);
        dest.writeList(tags);
        dest.writeList(notices);
    }
    /*
    public static final Parcelable.Creator<ActionItem> CREATOR
             = new Parcelable.Creator<ActionItem>() {
         public ActionItem createFromParcel(Parcel in) {
             return new ActionItem(in.readString(), in.readArrayList());
         }

         public ActionItem[] newArray(int size) {
             return new ActionItem[size];
         }
    };
    */
}
