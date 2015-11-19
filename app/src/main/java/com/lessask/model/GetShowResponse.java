package com.lessask.model;

import java.util.ArrayList;

/**
 * Created by huangji on 2015/11/19.
 */
public class GetShowResponse extends ResponseError{
    private ArrayList<ShowItem> showdatas;
    private String direct;


    public GetShowResponse(ArrayList<ShowItem> showdatas, String direct) {
        this.showdatas = showdatas;
        this.direct = direct;
    }

    public GetShowResponse(int errno, String error, ArrayList<ShowItem> showdatas, String direct) {
        super(errno, error);
        this.showdatas = showdatas;
        this.direct = direct;
    }

    public ArrayList<ShowItem> getShowdatas() {
        return showdatas;
    }

    public void setShowdatas(ArrayList<ShowItem> showdatas) {
        this.showdatas = showdatas;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }
}
