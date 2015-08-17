package com.lessask;

import android.app.Application;
import android.content.Intent;

import com.lessask.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by huangji on 2015/8/12.
 */
public class MyApplication extends Application{
    private int userid;
    private Map<Integer, User> friendsinMap;
    private List<User> friends;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public Map<Integer, User> getFriendsinMap() {
        return friendsinMap;
    }

    public void setFriendsinMap(Map<Integer, User> friends) {
        this.friendsinMap = friends;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
