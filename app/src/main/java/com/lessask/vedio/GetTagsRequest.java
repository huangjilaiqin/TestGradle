package com.lessask.vedio;

/**
 * Created by JHuang on 2015/10/17.
 */
public class GetTagsRequest {
    private int userid;

    public GetTagsRequest(int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
