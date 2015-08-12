package com.model;

/**
 * Created by huangji on 2015/8/12.
 */
public class LoginResponse extends ResponseError{
    private int userid;

    public LoginResponse(int errno, String error, int userid) {
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
