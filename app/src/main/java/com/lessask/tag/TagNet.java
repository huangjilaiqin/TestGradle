package com.lessask.tag;

import android.util.Log;

import com.google.gson.Gson;
import com.lessask.net.LASocketIO;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by huangji on 2015/10/16.
 */
public class TagNet {

    private static String TAG = TagNet.class.getSimpleName();
    private Socket mSocket;
    private Gson gson;
    private CreateTagListener createTagListener;
    private GetTagsListener getTagsListener;
    private TagNet(){
        mSocket = LASocketIO.getSocket();
        //注册事件
        gson = new Gson();
        registerEvent();
    }
    public static TagNet getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final TagNet INSTANCE = new TagNet();
    }
    public void emit(String event, Object... args){
        Log.e(TAG, event + ":" + args[0].toString());
        mSocket.emit(event, args);
    }
    public void emit(String event, Object[] args, Ack ask){
        mSocket.emit(event, args, ask);
    }


    private void registerEvent(){
        mSocket.on("createtagResp", onCreateTagResp);
        mSocket.on("gettagsResp", onGetTagsResp);
    }
    private Emitter.Listener onCreateTagResp = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onCreateTagResp :" + args[0].toString());
            CreateTagResponse response = gson.fromJson(args[0].toString(), CreateTagResponse.class);
            createTagListener.createTagResponse(response);
        }
    };

    public void setCreateTagListener(CreateTagListener listener){
        this.createTagListener = listener;
    }
    public interface CreateTagListener{
        void createTagResponse(CreateTagResponse response);
    }

    private Emitter.Listener onGetTagsResp = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onGetTagsResp:" + args[0].toString());
            GetTagsResponse response = null;
            try {
                response =gson.fromJson(args[0].toString(), GetTagsResponse.class);
            }catch (Exception e){
                Log.e(TAG, "e:"+e.getMessage());
                return;
            }
            getTagsListener.getTagsResponse(response);
        }
    };
    public void setGetTagsListener(GetTagsListener listener){
        this.getTagsListener = listener;
    }
    public interface GetTagsListener{
        void getTagsResponse(GetTagsResponse response);
    }
}
