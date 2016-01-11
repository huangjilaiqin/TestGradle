package com.lessask.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JHuang on 2016/1/10.
 */
public class Workout implements Parcelable {
    private int id;
    private int lessonId;
    private int userId;
    private int week;
    private Lesson lesson;
    public Workout(){}
    public Workout(int id, int lessonId, int userId, int week) {
        this.id = id;
        this.lessonId = lessonId;
        this.userId = userId;
        this.week = week;
    }
    public Workout(int id, int lessonId, int userId, int week,Lesson lesson) {
        this.id = id;
        this.lessonId = lessonId;
        this.userId = userId;
        this.week = week;
        this.lesson=lesson;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(lessonId);
        dest.writeInt(userId);
        dest.writeInt(week);
        dest.writeParcelable(lesson, 1);
    }

    public static final Parcelable.Creator<Workout> CREATOR
             = new Parcelable.Creator<Workout>() {
         public Workout createFromParcel(Parcel in) {
             int id = in.readInt();
             int lessonId = in.readInt();
             int userId = in.readInt();
             int week = in.readInt();
             Lesson lesson = in.readParcelable(Lesson.class.getClassLoader());

             return new Workout(id,lessonId,userId,week,lesson);
         }
         public Workout[] newArray(int size) {
             return new Workout[size];
         }
    };


    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
