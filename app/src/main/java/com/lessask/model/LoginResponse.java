package com.lessask.model;

/**
 * Created by huangji on 2015/8/12.
 */
public class LoginResponse extends ResponseError{
    private int userid;
    private String token;

    public LoginResponse(int errno, String error,int userid,String token) {
        super(errno, error);
        this.userid=userid;
        this.token= token;
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
