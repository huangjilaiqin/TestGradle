package com.lessask;

import android.app.Application;
import android.content.Intent;

import com.lessask.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangji on 2015/8/12.
 */
public class MyApplication extends Application{
    private int userid;
    private HashMap<Integer, User> friendsinMap;
    private ArrayList<User> friends;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public HashMap<Integer, User> getFriendsinMap() {
        return friendsinMap;
    }

    public void setFriendsinMap(HashMap<Integer, User> friends) {
        this.friendsinMap = friends;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
