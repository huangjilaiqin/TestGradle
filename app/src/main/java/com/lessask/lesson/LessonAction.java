package com.lessask.lesson;

import android.os.Parcel;
import android.os.Parcelable;

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
    private int groupRestTimes;
    private int actionRestTimes;

    public LessonAction(int actionId, int groups, int times, int groupRestTimes, int actionRestTimes) {
        this.actionId = actionId;
        //组数
        this.groups = groups;
        //每组的次数
        this.times = times;
        this.groupRestTimes = groupRestTimes;
        this.actionRestTimes = actionRestTimes;
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
        dest.writeInt(groupRestTimes);
        dest.writeInt(actionRestTimes);
    }
    public static final Parcelable.Creator<LessonAction> CREATOR
             = new Parcelable.Creator<LessonAction>() {
         public LessonAction createFromParcel(Parcel in) {
             int actionsId = in.readInt();
             int groups = in.readInt();
             int times = in.readInt();
             int groupRestTimes = in.readInt();
             int actionRestTimes = in.readInt();

             return new LessonAction(actionsId,groups,times,groupRestTimes,actionRestTimes);
         }

         public LessonAction[] newArray(int size) {
             return new LessonAction[size];
         }
    };

    public int getGroupRestTimes() {
        return groupRestTimes;
    }

    public void setGroupRestTimes(int groupRestTimes) {
        this.groupRestTimes = groupRestTimes;
    }

    public int getActionRestTimes() {
        return actionRestTimes;
    }

    public void setActionRestTimes(int actionRestTimes) {
        this.actionRestTimes = actionRestTimes;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }
    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }
}
