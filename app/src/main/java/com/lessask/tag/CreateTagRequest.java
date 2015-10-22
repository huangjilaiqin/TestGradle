package com.lessask.tag;

/**
 * Created by huangji on 2015/10/16.
 */
public class CreateTagRequest {
    private int userid;
    private String name;
    private int seq;

    public CreateTagRequest(int userid, String name, int seq) {
        this.userid = userid;
        this.name = name;
        this.seq = seq;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public int getSeq() {
        return seq;
    }
}
