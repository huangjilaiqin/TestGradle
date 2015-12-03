package com.lessask.action;

import com.lessask.model.ResponseError;

/**
 * Created by JHuang on 2015/10/18.
 */
public class HandleActionResponse extends ResponseError {
    int videoId;
    private String videoName;

    public HandleActionResponse(int videoId, String videoName) {
        this.videoId = videoId;
        this.videoName = videoName;
    }

    public HandleActionResponse(int errno, String error, int videoId, String videoName) {
        super(errno, error);
        this.videoId = videoId;
        this.videoName = videoName;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
