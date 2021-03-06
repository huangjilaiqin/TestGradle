package com.lessask.model;

/**
 * Created by JHuang on 2015/8/15.
 */
public class Register {
    private int type;
    private String mail;
    private String nickname;
    private String passwd;
    private String headImg;

    public Register(int type, String mail, String nickname, String passwd, String headImg){
        this.type = type;
        this.mail= mail;
        this.nickname = nickname;
        this.passwd = passwd;
        this.headImg = headImg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
