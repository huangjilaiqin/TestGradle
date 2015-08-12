package com.model;

/**
 * Created by huangji on 2015/8/12.
 */
public class Login {
    private int type;
    private String mail;
    private String passwd;

    public Login(int type, String mail, String passwd){
        this.type = type;
        this.mail= mail;
        this.passwd = passwd;
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
