package com.lessask.model;

/**
 * Created by JHuang on 2015/9/15.
 */
public class RunDataResponse extends ResponseError{
    private int userid;

    public RunDataResponse(int userid) {
        this.userid = userid;
    }

    public RunDataResponse(int errno, String error, int userid) {
        super(errno, error);
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
