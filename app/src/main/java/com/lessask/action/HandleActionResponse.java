package com.lessask.action;

import com.lessask.model.ResponseError;

/**
 * Created by JHuang on 2015/10/18.
 */
public class HandleActionResponse extends ResponseError {
    int actionId;
    private String videoName;
    private String actionImage;

    public HandleActionResponse(int actionId, String videoName,String actionIamge) {
        this.actionId = actionId;
        this.videoName = videoName;
        this.actionImage = actionIamge;
    }

    public HandleActionResponse(int errno, String error, int actionId, String videoName,String actionIamge) {
        super(errno, error);
        this.actionId = actionId;
        this.videoName = videoName;
        this.actionImage = actionIamge;
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

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
