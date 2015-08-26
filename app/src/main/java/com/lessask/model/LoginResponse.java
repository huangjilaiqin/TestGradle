package com.lessask.model;

/**
 * Created by huangji on 2015/8/12.
 */
public class LoginResponse extends ResponseError{
    private int userid;
    private String mail;
    private String phone;
    private String nickname;
    private String headimg;
    private int status;
    private String passwd;


    public LoginResponse(int errno, String error, int userid, String mail, String phone, String nickname, String headimg, int status, String passwd) {
        super(errno, error);
        this.userid = userid;
        this.mail = mail;
        this.phone = phone;
        this.nickname = nickname;
        this.headimg = headimg;
        this.status = status;
        this.passwd = passwd;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
