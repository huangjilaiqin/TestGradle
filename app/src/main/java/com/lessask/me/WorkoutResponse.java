package com.lessask.me;

import com.lessask.model.ResponseError;

/**
 * Created by JHuang on 2016/1/11.
 */
public class WorkoutResponse extends ResponseError{
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WorkoutResponse(int id) {
        this.id = id;
    }

    public WorkoutResponse(int errno, String error, int id) {
        super(errno, error);
        this.id = id;
    }
}
