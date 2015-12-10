package com.lessask.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private String TAG = GsonRequest.class.getSimpleName();
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private PostGsonRequest<T> postGsonRequest;

    public GsonRequest(int method, String url, Class<T> clazz, final PostGsonRequest<T> postGsonRequest) {
        super(method, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                postGsonRequest.onError(error);
            }
        });
        this.postGsonRequest = postGsonRequest;
        this.clazz = clazz;
        this.listener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                postGsonRequest.onResponse(response);
            }
        };
        postGsonRequest.onStart();
    }
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(String url, Class<T> clazz, PostGsonRequest<T> postGsonRequest) {
        this(Method.GET, url, clazz, postGsonRequest);
    }

    public interface PostGsonRequest<T>{
        void onStart();
        void onResponse(T response);
        void onError(VolleyError error);
        void setPostData(Map datas);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        HashMap<String,String> headers = new HashMap<>();
        postGsonRequest.setPostData(headers);
        return headers;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            //Log.e(TAG, "charset:"+HttpHeaderParser.parseCharset(response.headers));
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            //Log.e(TAG, "data:"+json);
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

}