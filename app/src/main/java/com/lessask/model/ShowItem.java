package com.lessask.model;


import java.util.ArrayList;

/**
 * Created by JHuang on 2015/9/16.
 */
public class ShowItem {
    private int id;
    private int userid;
    private String nickname;
    private String headimg;
    private String time;
    private String address;
    private String content;
    private ArrayList<String> pictures;
    private int permission;
    private String ats;
    //点赞的人
    private ArrayList<Integer> liker;
    private int likeStatus;
    //评论
    private ArrayList<CommentItem> comments;

    public ShowItem(int id, int userid,String nickname,String headimg, String time, String address, String content, ArrayList<String> pictures, int permission, String ats, ArrayList<Integer> liker, int likeStatus, ArrayList<CommentItem> comments) {
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
        this.liker = liker;
        this.likeStatus = likeStatus;
        this.comments = comments;
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
    }
    public void like(int userid){
        liker.add(new Integer(userid));
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public ArrayList<CommentItem> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentItem> comments) {
        this.comments = comments;
    }
}
