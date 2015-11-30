package com.lessask.model;

import java.util.ArrayList;

/**
 * Created by JHuang on 2015/11/30.
 */
public class GetActionResponse {
    private ArrayList<ActionItem> actionDatas;

    public GetActionResponse(ArrayList<ActionItem> actionDatas) {
        this.actionDatas = actionDatas;
    }

    public ArrayList<ActionItem> getActionDatas() {
        return actionDatas;
    }

    public void setActionDatas(ArrayList<ActionItem> actionDatas) {
        this.actionDatas = actionDatas;
    }
}
