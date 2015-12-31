package com.lessask.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lessask.lesson.LessonActionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laiqin on 15/12/17.
 */
public class Lesson implements Parcelable {
    private int id;
    private String name;
    private String cover;
    private List<String> bodies;
    private String address;
    private String purpose;
    private int costTime;
    private String description;
    private List<LessonActionInfo> lessonActionInfos;
    private int recycleTimes;
    private int fatEffect;
    private int muscleEffect;

    public Lesson(int id, String name, String cover, List<String> bodies, String address, String purpose, int costTime, String description, List<LessonActionInfo> lessonActionInfos,int recycleTimes,int fatEffect,int muscleEffect) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.bodies = bodies;
        this.address = address;
        this.purpose = purpose;
        this.costTime = costTime;
        this.description = description;
        this.lessonActionInfos= lessonActionInfos;
        this.recycleTimes = recycleTimes;
        this.fatEffect = fatEffect;
        this.muscleEffect = muscleEffect;
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
        dest.writeList(lessonActionInfos);
        dest.writeInt(recycleTimes);
        dest.writeInt(fatEffect);
        dest.writeInt(muscleEffect);
    }
    public static final Parcelable.Creator<Lesson> CREATOR
             = new Parcelable.Creator<Lesson>() {
         public Lesson createFromParcel(Parcel in) {
             int id = in.readInt();
             String name = in.readString();
             String cover = in.readString();
             List<String> bodies = in.readArrayList(String.class.getClassLoader());
             String address = in.readString();
             String purpose = in.readString();
             int costTime = in.readInt();
             String description = in.readString();
             List<LessonActionInfo> actionsId = in.readArrayList(LessonActionInfo.class.getClassLoader());
             int recycleTimes = in.readInt();
             int fatEffect = in.readInt();
             int muscleEffect = in.readInt();
             return new Lesson(id,name,cover,bodies,address,purpose,costTime,description,actionsId,recycleTimes,fatEffect,muscleEffect);
         }

         public Lesson[] newArray(int size) {
             return new Lesson[size];
         }
    };

    public int getFatEffect() {
        return fatEffect;
    }

    public void setFatEffect(int fatEffect) {
        this.fatEffect = fatEffect;
    }

    public int getMuscleEffect() {
        return muscleEffect;
    }

    public void setMuscleEffect(int muscleEffect) {
        this.muscleEffect = muscleEffect;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<String> getBodies() {
        return bodies;
    }

    public void setBodies(List<String> bodies) {
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

    public List<LessonActionInfo> getLessonActionInfos() {
        return lessonActionInfos;
    }

    public void setLessonActionInfos(List<LessonActionInfo> lessonActionInfos) {
        this.lessonActionInfos = lessonActionInfos;
    }

    public int getRecycleTimes() {
        return recycleTimes;
    }

    public void setRecycleTimes(int recycleTimes) {
        this.recycleTimes = recycleTimes;
    }
}
