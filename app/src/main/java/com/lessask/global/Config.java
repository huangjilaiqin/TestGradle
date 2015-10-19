package com.lessask.global;

/**
 * Created by JHuang on 2015/10/18.
 */
public class Config {
    private String wsUrl = "http://ws.o-topcy.com/ws/";
    private String uploadVedioUrl = "http://ws.o-topcy.com/httproute/uploadvedio";
    private String createShowUrl = "http://ws.o-topcy.com/httproute/show";
    private String imgUrl = "http://ws.o-topcy.com/imgs/";


    public String getUploadVedioUrl() {
        return uploadVedioUrl;
    }

    public String getCreateShowUrl() {
        return createShowUrl;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
