package com.lessask.show;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;
import com.google.gson.reflect.TypeToken;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.model.ArrayListResponse;
import com.lessask.crud.CRUD;
import com.lessask.crud.DefaultGsonRequestCRUD;
import com.lessask.model.CommentItem;
import com.lessask.model.ResponseError;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHuang on 2015/9/16.
 */
public class ShowTime extends ResponseError implements Parcelable {
    private int id;
    private int userid;
    private String nickname;
    private String headimg;
    private String time;
    private String address;
    private String content;
    private ArrayList<String> pictures;
    //格式100,100;200,300;320,480
    private ArrayList<String> picsSize;
    //格式 #767876;#345412;#986745
    private int permission;
    private int likeSize;
    private int commentSize;
    private String ats;
    //点赞的人
    private ArrayList<Integer> liker;
    private int likeStatus;
    //评论
    private ArrayList<CommentItem> comments;

    public ShowTime(){}

    public ShowTime(int id, int userid, String nickname, String headimg, String time, String address, String content, ArrayList<String> pictures, int permission, String ats, int likeSize,int commentSize,int likeStatus,ArrayList<String> picsSize) {
        this.id = id;
        this.userid = userid;
        this.nickname = nickname;
        this.headimg = headimg;
        this.time = time;
        this.address = address;
        this.content = content;
        this.pictures = pictures;
        this.permission = permission;
        this.ats = ats;
        this.likeSize=likeSize;
        this.commentSize=commentSize;
        this.likeStatus = likeStatus;
        this.picsSize=picsSize;
    }

    public int getLikeSize() {
        return likeSize;
    }

    public void setLikeSize(int likeSize) {
        this.likeSize = likeSize;
    }

    public int getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getAts() {
        return ats;
    }

    public void setAts(String ats) {
        this.ats = ats;
    }

    public ArrayList<Integer> getLiker() {
        return liker;
    }

    public void setLiker(ArrayList<Integer> liker) {
        this.liker = liker;
    }

    public void unlike(int userid){
        liker.remove(new Integer(userid));
        setLikeStatus(0);
    }
    public void like(int userid){
        if(liker==null)
            liker = new ArrayList<>();
        liker.add(new Integer(userid));
        setLikeStatus(1);
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    private void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public ArrayList<CommentItem> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentItem> comments) {
        this.comments = comments;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userid);
        parcel.writeString(nickname);
        parcel.writeString(headimg);
        parcel.writeString(time);
        parcel.writeString(address);
        parcel.writeString(content);
        parcel.writeList(pictures);
        parcel.writeInt(permission);
        parcel.writeString(ats);
        parcel.writeInt(likeSize);
        parcel.writeInt(commentSize);
        parcel.writeInt(likeStatus);
        parcel.writeList(picsSize);
    }
    public static final Creator<ShowTime> CREATOR
             = new Creator<ShowTime>() {
         public ShowTime createFromParcel(Parcel in) {
             int id = in.readInt();
             int userid = in.readInt();
             String nickname = in.readString();
             String headimg = in.readString();
             String time = in.readString();
             String adress = in.readString();
             String content = in.readString();
             ArrayList<String> pictures = in.readArrayList(null);
             int permission = in.readInt();
             String ats = in.readString();
             int likeSize = in.readInt();
             int commentSize = in.readInt();
             int likeStatus = in.readInt();
             ArrayList<String> picsSize = in.readArrayList(null);
             return new ShowTime(id,userid,nickname,headimg,time,adress,content,pictures,permission,ats,likeSize,commentSize,likeStatus,picsSize);
         }

         public ShowTime[] newArray(int size) {
             return new ShowTime[size];
         }
    };


}
