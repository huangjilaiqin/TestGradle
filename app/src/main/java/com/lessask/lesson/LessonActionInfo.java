package com.lessask.lesson;

/**
 * Created by huangji on 2015/10/21.
 */
public class LessonActionInfo {
    private String name;
    private int times;
    private int groups;
    private int costTimes;

    public LessonActionInfo(String name, int times, int groups, int costTimes) {
        this.groups = groups;
        this.times = times;
        this.name = name;
        this.costTimes = costTimes;
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

    public int getCostTimes() {
        return costTimes;
    }

    public void setCostTimes(int costTimes) {
        this.costTimes = costTimes;
    }
}
