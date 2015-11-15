package com.lessask.global;

/**
 * Created by JHuang on 2015/10/18.
 */
public class Config {
    //private String wsUrl = "http://123.59.40.113:5002/ws/";
    private String wsUrl = "http://123.59.40.113";
    //private String wsPath = "/ws/socket.io/";
    private String wsPath = "/ws/";
    private String uploadVedioUrl = "http://123.59.40.113/httproute/uploadvedio";
    private String createShowUrl = "http://123.59.40.113/httproute/show";
    private String imgUrl = "http://123.59.40.113/imgs/";
    private String vedioUrl = "http://123.59.40.113/httproute/vedios/";
    private String registerUrl = "http://123.59.40.113/httproute/register/";
    private String getShowUrl = "http://123.59.40.113/httproute/getshow/";
    private String likeUrl = "http://123.59.40.113/httproute/like/";
    private String unlikeUrl = "http://123.59.40.113/httproute/unlike/";

    public String getLikeUrl() {
        return likeUrl;
    }

    public String getUnlikeUrl() {
        return unlikeUrl;
    }

    public String getGetShowUrl() {
        return getShowUrl;
    }

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

    public String getRegisterUrl() {
        return registerUrl;
    }
}
