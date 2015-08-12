package com.model;

/**
 * Created by JHuang on 2015/8/12.
 */
public class MessageResponse extends ResponseError{
    private int id;

    public MessageResponse(int errno, String error, int id) {
        super(errno, error);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
