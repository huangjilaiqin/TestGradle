package com.lessask.model;

/**
 * Created by JHuang on 2015/8/16.
 */
public class User {
    private int userid;
    private String mail;
    private String nickname;
    private int status;

    public User(int userid, String mail, String nickname, int status) {
        this.userid = userid;
        this.mail = mail;
        this.nickname = nickname;
        this.status = status;
    }

    @Override
    public String toString() {
        return "userid:"+userid+", mail:"+mail+", nickname:"+nickname+", status:"+status;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
