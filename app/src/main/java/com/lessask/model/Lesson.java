package com.lessask.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by laiqin on 15/12/17.
 */
public class Lesson implements Parcelable {
    private int id;
    private String name;
    private String cover;
    private ArrayList<String> bodies;
    private String address;
    private String purpose;
    private int costTime;
    private String description;
    private ArrayList<Integer> actionsId;

    public Lesson(int id, String name, String cover, ArrayList<String> bodies, String address, String purpose, int costTime, String description, ArrayList<Integer> actionsId) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.bodies = bodies;
        this.address = address;
        this.purpose = purpose;
        this.costTime = costTime;
        this.description = description;
        this.actionsId = actionsId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(cover);
        dest.writeList(bodies);
        dest.writeString(address);
        dest.writeString(purpose);
        dest.writeInt(costTime);
        dest.writeString(description);
        dest.writeList(actionsId);
    }
    public static final Parcelable.Creator<Lesson> CREATOR
             = new Parcelable.Creator<Lesson>() {
         public Lesson createFromParcel(Parcel in) {
             int id = in.readInt();
             String name = in.readString();
             String cover = in.readString();
             ArrayList<String> bodies = in.readArrayList(null);
             String address = in.readString();
             String purpose = in.readString();
             int costTime = in.readInt();
             String description = in.readString();
             ArrayList<Integer> actionsId = in.readArrayList(null);
             return new Lesson(id,name,cover,bodies,address,purpose,costTime,description,actionsId);
         }

         public Lesson[] newArray(int size) {
             return new Lesson[size];
         }
    };

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public ArrayList<String> getBodies() {
        return bodies;
    }

    public void setBodies(ArrayList<String> bodies) {
        this.bodies = bodies;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Integer> getActionsId() {
        return actionsId;
    }

    public void setActionsId(ArrayList<Integer> actionsId) {
        this.actionsId = actionsId;
    }


}
