package com.lessask.model;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/9/16.
 */
public class ShowItem {
    private String name;
    private String headImg;
    private String time;
    private String address;
    private ArrayList<String> showImgs;
    private String content;
    private int upSize;
    private int commentSize;

    public ShowItem(String name, String headImg, String time, String address, ArrayList<String> showImgs, String content, int upSize, int commentSize) {
        this.name = name;
        this.headImg = headImg;
        this.time = time;
        this.address = address;
        this.showImgs = showImgs;
        this.content = content;
        this.upSize = upSize;
        this.commentSize = commentSize;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setShowImgs(ArrayList<String> showImgs) {
        this.showImgs = showImgs;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUpSize(int upSize) {
        this.upSize = upSize;
    }

    public void setCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }

    public String getName() {
        return name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> getShowImgs() {
        return showImgs;
    }

    public String getContent() {
        return content;
    }

    public int getUpSize() {
        return upSize;
    }

    public int getCommentSize() {
        return commentSize;
    }
}
