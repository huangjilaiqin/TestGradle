package com.lessask.lesson;

/**
 * Created by huangji on 2015/10/21.
 * 创建编辑课程的动作信息
 */
public class LessonActionInfo {
    private String name;
    //根据actionId获取动作名字，图片
    private int actionId;
    private String actionName;
    private String actionPic;
    private int groups;
    private int times;
    private int groupRestTimes;
    private int actionRestTimes;

    public LessonActionInfo(int actionId, String actionName, String actionPic,int groups,int times,int groupRestTimes, int actionRestTimes) {
        this.actionId = actionId;
        this.actionName = actionName;
        this.actionPic = actionPic;
        //组数
        this.groups = groups;
        //每组的次数
        this.times = times;
        this.groupRestTimes = groupRestTimes;
        this.actionRestTimes = actionRestTimes;
    }

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

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionPic() {
        return actionPic;
    }

    public void setActionPic(String actionPic) {
        this.actionPic = actionPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
