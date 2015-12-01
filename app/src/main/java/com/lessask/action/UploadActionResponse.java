package com.lessask.action;

/**
 * Created by JHuang on 2015/10/18.
 */
public class UploadActionResponse {
    int vedioid;
    private String vedioName;

    public UploadActionResponse(int vedioid,String vedioName) {
        this.vedioid = vedioid;
        this.vedioName = vedioName;
    }

    public String getVedioName() {
        return vedioName;
    }

    public void setVedioName(String vedioName) {
        this.vedioName = vedioName;
    }

    public int getVedioid() {
        return vedioid;
    }

    public void setVedioid(int vedioid) {
        this.vedioid = vedioid;
    }
}
