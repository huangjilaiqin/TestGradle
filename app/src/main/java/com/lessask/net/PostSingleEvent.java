package com.lessask.net;

import java.util.HashMap;

/**
 * Created by JHuang on 2015/10/18.
 */
public interface PostSingleEvent {
    /**
     * key: headers, images, files
     * value: <String, String>, <String, image>, <String, file>与上面一一对应
     */
    void onStart();
    void onDone(boolean success, PostResponse response);
}
