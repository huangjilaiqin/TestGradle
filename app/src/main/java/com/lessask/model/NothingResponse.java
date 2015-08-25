package com.lessask.model;

/**
 * Created by JHuang on 2015/8/25.
 */
public class NothingResponse extends ResponseError{
    public NothingResponse(int errno, String error){
        super(errno, error);
    }
}
