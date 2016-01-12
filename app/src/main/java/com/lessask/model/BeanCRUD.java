package com.lessask.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.dialog.LoadingDialog;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.test.TestActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JHuang on 2016/1/12.
 */
/*
* to do 一个抽象
* */
public abstract class BeanCRUD<T> extends ResponseError{
    private final String TAG = BeanCRUD.class.getSimpleName();
    public void create(Context context,BaseRecyclerAdapter adapter,String url,int position){

    }

    public abstract void readOnPostData(Map datas);
    public void read(final Context context,final BaseRecyclerAdapter adapter,final RecyclerViewStatusSupport recyclerView,String url){
        Type type = new TypeToken<ArrayList<T>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<ArrayList<T>>(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest<ArrayList<T>>(){
            @Override
            public void onStart() {
                Log.e(TAG, "onstarttttttttttttttttttttt");
                recyclerView.showLoadingView();
            }

            @Override
            public void onResponse(final ArrayList<T> response) {

                if(getError()!=null && getError()!="" || getErrno()!=0){
                    Log.e(TAG, "onResponse error:"+getError()+", "+getErrno());
                    recyclerView.showErrorView(getError());
                    Toast.makeText(context, getError(), Toast.LENGTH_SHORT).show();
                    if(response==null){
                        Log.e(TAG, "response is null");
                    }else
                        Log.e(TAG, "read size: "+response.size());
                }else {
                    if(response==null){
                        Log.e(TAG, "response is null");
                    }else
                        Log.e(TAG, "read size: " + response.size());
                    /*
                    for (int i=0;i<response.size();i++) {
                        ShowItem item = (ShowItem)response.get(0);
                        Log.e(TAG, "item:" + item.getAddress() + "," + item.getTime() + ", " + item.getContent()+","+item.getPictures());
                    }
                    */
                    adapter.appendToList((List) response);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "onErrorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"+error.getMessage());
                recyclerView.showErrorView(error.getMessage());
            }

            @Override
            public void setPostData(Map datas) {
                readOnPostData(datas);
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);

    }
    public void update(final Context context,final BaseRecyclerAdapter adapter,String url,final int position){
        Type type = new TypeToken<T>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<T>(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest<T>(){
            final LoadingDialog loadingDialog = new LoadingDialog(context);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(T response) {
                loadingDialog.cancel();
                if(getError()!=null || getErrno()!=0){
                    Toast.makeText(context, getError(), Toast.LENGTH_SHORT).show();
                }else {
                    adapter.update(position, this);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(context, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setPostData(Map datas) {
                deleteOnPostData(datas);
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);

    }
    public abstract void deleteOnPostData(Map datas);
    public void delete(final Context context, final BaseRecyclerAdapter adapter,String url,final int position){
        Type type = new TypeToken<T>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<T>(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest<T>(){
            final LoadingDialog loadingDialog = new LoadingDialog(context);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(T response) {
                loadingDialog.cancel();
                if(getError()!=null || getErrno()!=0){
                    Toast.makeText(context, getError(), Toast.LENGTH_SHORT).show();
                }else {
                    adapter.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(context, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setPostData(Map datas) {
                deleteOnPostData(datas);
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
}
