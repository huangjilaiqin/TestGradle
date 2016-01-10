package com.lessask.model;

/**
 * Created by JHuang on 2016/1/10.
 */
public class Workout {
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
