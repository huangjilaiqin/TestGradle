package com.lessask.model;

/**
 * Created by JHuang on 2015/8/25.
 */
public class UserInfo {
    private int userid;
    private String mail;
    private String nickname;
    private String passwd;
    private String headImgName;
    private String headImgContent;
    public UserInfo(int userid, String mail, String passwd, String nickname) {
        this.userid = userid;
        this.mail = mail;
        this.nickname = nickname;
        this.passwd = passwd;
    }
    public UserInfo(int userid, String mail, String passwd, String nickname, String headImgName, String headImgContent) {
        this.userid = userid;
        this.mail = mail;
        this.nickname = nickname;
        this.passwd = passwd;
        this.headImgName = headImgName;
        this.headImgContent = headImgContent;
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

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getHeadImgContent() {
        return headImgContent;
    }

    public void setHeadImgContent(String headImgContent) {
        this.headImgContent = headImgContent;
    }

    public String getHeadImgName() {
        return headImgName;
    }

    public void setHeadImgName(String headImgName) {
        this.headImgName = headImgName;
    }
}
