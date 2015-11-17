package com.lessask.model;

/**
 * Created by JHuang on 2015/11/16.
 */
public class UnlikeResponse extends ResponseError{
    private int showid;
    private int position;

    public UnlikeResponse(int showid, int position) {
        this.showid = showid;
        this.position = position;
    }

    public UnlikeResponse(int errno, String error, int showid, int position) {
        super(errno, error);
        this.showid = showid;
        this.position = position;
    }

    public int getShowid() {
        return showid;
    }

    public void setShowid(int showid) {
        this.showid = showid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

