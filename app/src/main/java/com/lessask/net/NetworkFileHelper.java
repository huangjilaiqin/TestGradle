package com.lessask.net;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by JHuang on 2015/12/9.
 */
public class NetworkFileHelper {
    private final String TAG = NetActivity.class.getSimpleName();
    private final int REQUEST_START=1;
    private final int REQUEST_DONE=2;
    private final int REQUEST_ERROR=3;
    private Gson gson = new Gson();
    private Map<Integer, PostFileRequest> postFileRequests = new HashMap<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int tag = msg.arg1;
            PostFileRequest postFileRequest = postFileRequests.get(tag);
            switch (msg.what){
                case REQUEST_START:
                    postFileRequest.onStart();
                    break;
                case REQUEST_DONE:
                    postFileRequest.onResponse(msg.obj);
                    postFileRequests.remove(tag);
                    break;
                case REQUEST_ERROR:
                    postFileRequest.onError((String)msg.obj);
                    postFileRequests.remove(tag);
                    break;
            }

        }
    };
    private NetworkFileHelper(){

    }
    private static class LazyHolder {
        private static final NetworkFileHelper INSTANCE = new NetworkFileHelper();
    }

    public interface PostFileRequest{
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> files = new HashMap<>();
        void onStart();
        void onResponse(Object obj);
        void onError(String error);
        HashMap<String, String> getHeaders();
        HashMap<String, String> getFiles();
        HashMap<String, String> getImages();
    }

    public void startPost(String url, final Class responseClass, final PostFileRequest postFile){
        final int tag = postFileRequests.size();
        postFileRequests.put(tag, postFile);
        PostSingleEvent event = new PostSingleEvent() {
            @Override
            public void onStart() {
                Message msg = new Message();
                msg.what = REQUEST_START;
                msg.arg1 = tag;
                handler.sendMessage(msg);
            }

            @Override
            public void onDone(PostResponse postResponse) {
                Message msg = new Message();
                msg.what = REQUEST_DONE;
                msg.arg1 = tag;
                String body = postResponse.getBody();
                msg.obj  = gson.fromJson(body, responseClass);
                handler.sendMessage(msg);
            }

            @Override
            public void onError(String err) {
                Message msg = new Message();
                msg.what = REQUEST_ERROR;
                msg.arg1 = tag;
                msg.obj = err;
                handler.sendMessage(msg);
            }

            @Override
            public HashMap<String, String> getFiles() {
                return postFile.getFiles();
            }

            @Override
            public HashMap<String, String> getHeaders() {
                return postFile.getHeaders();
            }

            @Override
            public HashMap<String, String> getImages() {
                return postFile.getImages();
            }
        };
        PostSingle postSingle = new PostSingle(url, event);
        postSingle.start();
    }
}
