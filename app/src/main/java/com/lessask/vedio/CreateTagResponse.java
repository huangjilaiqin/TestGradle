package com.lessask.vedio;

import com.lessask.model.ResponseError;

/**
 * Created by huangji on 2015/10/16.
 */
public class CreateTagResponse extends ResponseError {
    private int id;
    private String name;
    private int seq;

    public CreateTagResponse(int errno, String error, int id, String name, int seq) {
        super(errno, error);
        this.id = id;
        this.name = name;
        this.seq = seq;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }

    public int getSeq() {
        return seq;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
