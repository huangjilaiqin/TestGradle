package com.lessask.net;

import java.util.HashMap;

/**
 * Created by JHuang on 2015/10/18.
 */
public interface PostSingleEvent {
    void onStart();
    void onDone(PostResponse response);
    void onError(String err);
}
