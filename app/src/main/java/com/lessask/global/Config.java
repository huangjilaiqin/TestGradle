package com.lessask.global;

/**
 * Created by JHuang on 2015/10/18.
 */
public class Config {
    //private String wsUrl = "http://123.59.40.113:5002/ws/";
    private String wsUrl = "http://ws.o-topcy.com";
    private String wsPath = "/ws/socket.io/";
    private String uploadVedioUrl = "http://ws.o-topcy.com/httproute/uploadvedio";
    private String createShowUrl = "http://ws.o-topcy.com/httproute/show";
    private String imgUrl = "http://ws.o-topcy.com/imgs/";
    private String vedioUrl = "http://ws.o-topcy.com/vedios/";

    public String getWsPath() {
        return wsPath;
    }

    public String getVedioUrl() {
        return vedioUrl;
    }

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
