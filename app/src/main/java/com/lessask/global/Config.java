package com.lessask.global;

import java.io.File;

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
    private String vedioUrl = "http://123.59.40.113/vedios/";
    private String vedioPath = "";
    private String registerUrl = "http://123.59.40.113/httproute/register/";
    private String getShowUrl = "http://123.59.40.113/httproute/getshow/";
    private String likeUrl = "http://123.59.40.113/httproute/like/";
    private String unlikeUrl = "http://123.59.40.113/httproute/unlike/";
    private String getActioinsUrl = "http://123.59.40.113/httproute/getactions/";
    private String deleteActionUrl = "http://123.59.40.113/httproute/action/delete/";
    private String saveActionUrl = "http://123.59.40.113/httproute/action/save/";

    private File videoCachePath;

    public String getSaveActionUrl() {
        return saveActionUrl;
    }

    public String getDeleteActionUrl() {
        return deleteActionUrl;
    }

    public void setDeleteActionUrl(String deleteActionUrl) {
        this.deleteActionUrl = deleteActionUrl;
    }

    public File getVideoCachePath() {
        return videoCachePath;
    }

    public void setVideoCachePath(File videoCachePath) {
        if(!videoCachePath.exists()){
            videoCachePath.mkdirs();
        }
        this.videoCachePath = videoCachePath;
    }

    public String getGetActioinsUrl() {
        return getActioinsUrl;
    }

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
