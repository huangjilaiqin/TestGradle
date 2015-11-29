package com.lessask.tag;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by huangji on 2015/10/16.
 */
public class TagData implements Parcelable{
    private int id;
    private String name;

    public TagData(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public TagData(Parcel in){
        id = in.readInt();
        name = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
    public static final Parcelable.Creator<TagData> CREATOR
             = new Parcelable.Creator<TagData>() {
         public TagData createFromParcel(Parcel in) {
             return new TagData(in);
         }

         public TagData[] newArray(int size) {
             return new TagData[size];
         }
    };
}
