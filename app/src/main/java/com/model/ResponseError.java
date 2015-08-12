package com.model;

/**
 * Created by huangji on 2015/8/12.
 */
public class ResponseError {
    private String error = "";
    private int errno = 0;

    public ResponseError(){}
    public ResponseError(int errno, String error){
        this.errno = errno;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }
}
