package com.lessask.global;

/**
 * Created by JHuang on 2015/10/18.
 */
public class Config {
    private String uploadVedioUrl = "http://ws.o-topcy.com/httproute/uploadvedio";
    private String createShowUrl = "http://ws.o-topcy.com/httproute/show";


    public String getUploadVedioUrl() {
        return uploadVedioUrl;
    }

    public void setUploadVedioUrl(String uploadVedioUrl) {
        this.uploadVedioUrl = uploadVedioUrl;
    }

    public String getCreateShowUrl() {
        return createShowUrl;
    }

    public void setCreateShowUrl(String createShowUrl) {
        this.createShowUrl = createShowUrl;
    }
}
