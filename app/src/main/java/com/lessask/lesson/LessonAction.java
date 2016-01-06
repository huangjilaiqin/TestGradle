package com.lessask.lesson;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/10/21.
 * 创建编辑课程的动作信息
 */
public class LessonAction implements Parcelable {
    //根据actionId获取动作名字，图片,为了防止动作信息更改,只保存id
    //如果要删除动作则提示使用了该动作的课程
    private int actionId;
    private int groups;
    private int times;
    private int restTimes;
    private String actionName;
    private ArrayList<Integer> tags;
    private ArrayList<String> notices;
    private String videoName;
    private String actionImage;

    public LessonAction(int actionId, int groups, int times, int restTimes, String actionName, ArrayList<Integer> tags, ArrayList<String> notices, String videoName, String actionImage) {
        this.actionId = actionId;
        this.groups = groups;
        this.times = times;
        this.restTimes = restTimes;
        this.actionName = actionName;
        this.tags = tags;
        this.notices = notices;
        this.videoName = videoName;
        this.actionImage = actionImage;
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
        dest.writeInt(restTimes);
        dest.writeString(actionName);
        dest.writeList(tags);
        dest.writeList(notices);
        dest.writeString(videoName);
        dest.writeString(actionName);
    }
    public static final Parcelable.Creator<LessonAction> CREATOR
             = new Parcelable.Creator<LessonAction>() {
         public LessonAction createFromParcel(Parcel in) {
             int actionsId = in.readInt();
             int groups = in.readInt();
             int times = in.readInt();
             int restTimes = in.readInt();
             String actionName = in.readString();
             ArrayList<Integer> tags = in.readArrayList(Integer.class.getClassLoader());
             ArrayList<String> notices = in.readArrayList(String.class.getClassLoader());
             String videoName = in.readString();
             String actionImage = in.readString();

             return new LessonAction(actionsId,groups,times,restTimes,actionName,tags,notices,videoName,actionImage);
         }

         public LessonAction[] newArray(int size) {
             return new LessonAction[size];
         }
    };

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

    public int getRestTimes() {
        return restTimes;
    }

    public void setRestTimes(int restTimes) {
        this.restTimes = restTimes;
    }

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
}
