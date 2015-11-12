package com.lessask.model;



/**
 * Created by huangji on 2015/11/12.
 */
public class CommentItem {
    private int id;
    private int commentuid;
    private int becommentuid;
    private String comment;
    private String time;

    public CommentItem(int id, int commentuid, int becommentuid, String comment, String time) {
        this.id = id;
        this.commentuid = commentuid;
        this.becommentuid = becommentuid;
        this.comment = comment;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommentuid() {
        return commentuid;
    }

    public void setCommentuid(int commentuid) {
        this.commentuid = commentuid;
    }

    public int getBecommentuid() {
        return becommentuid;
    }

    public void setBecommentuid(int becommentuid) {
        this.becommentuid = becommentuid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
