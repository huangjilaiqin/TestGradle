package com.lessask.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by JHuang on 2015/8/16.
 */
public class User {
    private int userid;
    private String mail;
    private String nickname;
    private int status;
    private String passwd;
    private File headImg;

    public User(int userid, String mail, String nickname, int status, String passwd) {
        this.userid = userid;
        this.mail = mail;
        this.nickname = nickname;
        this.status = status;
        this.passwd = passwd;
        this.headImg = null;
    }
    public User(int userid, String mail, String nickname, int status, String passwd, File headImg) {
        this.userid = userid;
        this.mail = mail;
        this.nickname = nickname;
        this.status = status;
        this.passwd = passwd;
        this.headImg = headImg;
    }

    @Override
    public String toString() {
        return "userid:"+userid+", mail:"+mail+", nickname:"+nickname+", status:"+status;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public File getHeadImg() {
        return headImg;
    }

    public void setHeadImg(File headImg) {
        this.headImg = headImg;
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
