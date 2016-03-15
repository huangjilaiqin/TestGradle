package com.lessask.contacts;

import com.lessask.model.ResponseError;

/**
 * Created by laiqin on 16/3/15.
 */
public class AddFriend extends ResponseError {
    int userid;
    int friendid;

    public AddFriend(int userid, int friendid) {
        this.userid = userid;
        this.friendid = friendid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getFriendid() {
        return friendid;
    }

    public void setFriendid(int friendid) {
        this.friendid = friendid;
    }
}
