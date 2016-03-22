package com.lessask.global;

import java.io.File;

/**
 * Created by JHuang on 2015/10/18.
 */
public class Config {
    //private String wsUrl = "http://123.59.40.113:5002/ws/";
    private String wsUrl = "http://123.59.40.113";
    //private String wsPath = "/ws/socket.io/";
    private String wsPath = "/ws/";
    private String addShowtimeUrl = "http://123.59.40.113/httproute/showtime/add";
    private String imgUrl = "http://123.59.40.113/imgs/";
    private String registerUrl = "http://123.59.40.113/httproute/register/";
    private String getShowUrl = "http://123.59.40.113/httproute/getshow/";
    private String getShowByUseridUrl = "http://123.59.40.113/httproute/getshowbyuserid/";
    private String likeUrl = "http://123.59.40.113/httproute/like/";
    private String unlikeUrl = "http://123.59.40.113/httproute/unlike/";
    private String videoUrl = "http://123.59.40.113/videos/";

    private String addActionUrl = "http://123.59.40.113/httproute/action/add/";
    private String deleteActionUrl = "http://123.59.40.113/httproute/action/delete/";
    private String updateActionUrl = "http://123.59.40.113/httproute/action/update/";
    private String actioinsUrl = "http://123.59.40.113/httproute/actions/";

    private String addLessonUrl = "http://123.59.40.113/httproute/lesson/add/";
    private String deleteLessonUrl = "http://123.59.40.113/httproute/lesson/delete/";
    private String updateLessonUrl = "http://123.59.40.113/httproute/lesson/update/";
    private String lessonsUrl = "http://123.59.40.113/httproute/lessons/";

    private String lessonActionsUrl = "http://123.59.40.113/httproute/lesson_actions/";

    private String addWorkoutUrl = "http://123.59.40.113/httproute/workout/add";
    private String deleteWorkoutUrl = "http://123.59.40.113/httproute/workout/delete";
    private String updateWorkoutUrl = "http://123.59.40.113/httproute/workout/update";
    private String workoutsUrl = "http://123.59.40.113/httproute/workouts";
    private String workoutUrl = "http://123.59.40.113/httproute/workout";

    private String recommendFriendsUrl = "http://123.59.40.113/httproute/recommendFriends/";
    private String friendsUrl = "http://123.59.40.113/httproute/friends/";
    private String userUrl = "http://123.59.40.113/httproute/user/";
    private String chatGroupUrl = "http://123.59.40.113/httproute/chatgroup";
    private String updateHeadImg = "http://123.59.40.113/httproute/headimg/update";

    public String getWorkoutUrl() {
        return workoutUrl;
    }

    public String getUpdateHeadImg() {
        return updateHeadImg;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public String getFriendsUrl() {
        return friendsUrl;
    }

    public String getRecommendFriendsUrl() {
        return recommendFriendsUrl;
    }

    public String getChatGroupUrl() {
        return chatGroupUrl;
    }

    public String getAddShowtimeUrl() {
        return addShowtimeUrl;
    }

    public String getAddWorkoutUrl() {
        return addWorkoutUrl;
    }

    public String getDeleteWorkoutUrl() {
        return deleteWorkoutUrl;
    }

    public String getUpdateWorkoutUrl() {
        return updateWorkoutUrl;
    }

    public String getWorkoutsUrl() {
        return workoutsUrl;
    }

    public String getLessonActionsUrl() {
        return lessonActionsUrl;
    }

    public String getAddLessonUrl() {
        return addLessonUrl;
    }

    public String getDeleteLessonUrl() {
        return deleteLessonUrl;
    }

    public String getUpdateLessonUrl() {
        return updateLessonUrl;
    }

    public String getLessonsUrl() {
        return lessonsUrl;
    }

    private File videoCachePath;

    public String getAddActionUrl() {
        return addActionUrl;
    }

    public String getUpdateActionUrl() {
        return updateActionUrl;
    }

    public String getActioinsUrl() {
        return actioinsUrl;
    }

    public String getDeleteActionUrl() {
        return deleteActionUrl;
    }

    public File getVideoCachePath() {
        return videoCachePath;
    }

    public void setVideoCachePath(File videoCachePath) {
        if(!videoCachePath.exists()){
            videoCachePath.mkdirs();
        }
        this.videoCachePath = videoCachePath;
    }


    public String getLikeUrl() {
        return likeUrl;
    }

    public String getUnlikeUrl() {
        return unlikeUrl;
    }

    public String getGetShowUrl() {
        return getShowUrl;
    }

    public String getWsPath() {
        return wsPath;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public String getGetShowByUseridUrl() {
        return getShowByUseridUrl;
    }
}
