package com.lessask.model;

/**
 * Created by JHuang on 2015/8/15.
 */
public class RegisterResponse extends ResponseError {
    private int userid;

    public RegisterResponse(int errno, String error, int userid) {
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
