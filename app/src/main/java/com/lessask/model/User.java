package com.lessask.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.List;

/**
 * Created by JHuang on 2015/8/16.
 */
public class User implements Parcelable {
    private int userid;
    private String mail;
    private String nickname;
    private int status;
    private String passwd;
    private String headImg;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userid);
        dest.writeString(mail);
        dest.writeString(nickname);
        dest.writeInt(status);
        dest.writeString(passwd);
        dest.writeString(headImg);
    }

    public static final Parcelable.Creator<User> CREATOR
             = new Parcelable.Creator<User>() {
         public User createFromParcel(Parcel in) {
             int userid = in.readInt();
             String mail = in.readString();
             String nickname = in.readString();
             int status = in.readInt();
             String passwd = in.readString();
             String headImg = in.readString();
             return new User(userid,mail,nickname,status,passwd,headImg);
         }

         public User[] newArray(int size) {
             return new User[size];
         }
    };

    public User(int userid, String nickname, String headImg) {
        this.userid = userid;
        this.nickname = nickname;
        this.headImg = headImg;
    }
    public User(int userid, String mail, String nickname, int status, String passwd) {
        this.userid = userid;
        this.mail = mail;
        this.nickname = nickname;
        this.status = status;
        this.passwd = passwd;
        this.headImg = null;
    }
    public User(int userid, String mail, String nickname, int status, String passwd, String headImg) {
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

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
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
