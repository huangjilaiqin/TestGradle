package com.lessask.model;

/**
 * Created by huangji on 2016/3/8.
 */
public class VerifyToken extends ResponseError{
    private int userid;
    private String token;

    public VerifyToken(int userid, String token) {
        this.userid = userid;
        this.token = token;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
