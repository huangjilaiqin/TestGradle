package com.lessask.model;

/**
 * Created by JHuang on 2015/12/29.
 */
public class HandleLessonResponse extends ResponseError {
    private int id;
    private String cover;

    public HandleLessonResponse(int id, String cover) {
        this.id = id;
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
