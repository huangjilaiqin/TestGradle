package com.lessask.lesson;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/10/21.
 * 课程关联动作的简要信息
 */
public class LessonAction implements Parcelable {
    //根据actionId获取动作名字，图片,为了防止动作信息更改,只保存id
    //如果要删除动作则提示使用了该动作的课程
    private int actionId;
    private int groups;
    private int times;
    private int resetTime;
    private String actionName;
    private ArrayList<Integer> tags;
    private ArrayList<String> notices;
    private String videoName;
    private String actionImage;

    public LessonAction(int actionId, int groups, int times, int resetTime, String actionName, ArrayList<Integer> tags, ArrayList<String> notices, String videoName, String actionImage) {
        this.actionId = actionId;
        this.groups = groups;
        this.times = times;
        this.resetTime = resetTime;
        this.actionName = actionName;
        this.tags = tags;
        this.notices = notices;
        this.videoName = videoName;
        this.actionImage = actionImage;
    }

    public LessonAction(int actionId, int groups, int times, int resetTime) {
        this.actionId = actionId;
        this.groups = groups;
        this.times = times;
        this.resetTime = resetTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(actionId);
        dest.writeInt(groups);
        dest.writeInt(times);
        dest.writeInt(resetTime);
        dest.writeString(actionName);
        dest.writeList(tags);
        dest.writeList(notices);
        dest.writeString(videoName);
        dest.writeString(actionImage);
    }
    public static final Creator<LessonAction> CREATOR
             = new Creator<LessonAction>() {
         public LessonAction createFromParcel(Parcel in) {
             int actionId = in.readInt();
             int groups = in.readInt();
             int times = in.readInt();
             int resetTime = in.readInt();
             String actionName = in.readString();
             ArrayList<Integer> tags = in.readArrayList(Integer.class.getClassLoader());
             ArrayList<String> notices = in.readArrayList(String.class.getClassLoader());
             String videoName = in.readString();
             String actionImage = in.readString();

             return new LessonAction(actionId,groups,times,resetTime,actionName,tags,notices,videoName,actionImage);
         }

         public LessonAction[] newArray(int size) {
             return new LessonAction[size];
         }
    };

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
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

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getActionImage() {
        return actionImage;
    }

    public void setActionImage(String actionImage) {
        this.actionImage = actionImage;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getResetTime() {
        return resetTime;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }
}
