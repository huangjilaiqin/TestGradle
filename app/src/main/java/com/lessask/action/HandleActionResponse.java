package com.lessask.action;

import com.lessask.model.ResponseError;

/**
 * Created by JHuang on 2015/10/18.
 */
public class HandleActionResponse extends ResponseError {
    int actionId;
    private String videoName;

    public HandleActionResponse(int actionId, String videoName) {
        this.actionId = actionId;
        this.videoName = videoName;
    }

    public HandleActionResponse(int errno, String error, int actionId, String videoName) {
        super(errno, error);
        this.actionId = actionId;
        this.videoName = videoName;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
