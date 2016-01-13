package com.lessask.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.captain_miao.recyclerviewutils.BaseLoadMoreRecyclerAdapter;
import com.google.gson.reflect.TypeToken;
import com.lessask.dialog.LoadingDialog;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.show.ShowTime;

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
public class BeaCRUDHelper{
    private final String TAG = BeaCRUDHelper.class.getSimpleName();
    public void create(Context context,BaseRecyclerAdapter adapter,String url,int position){

    }
    public void read(final Context context,final BaseLoadMoreRecyclerAdapter adapter,final RecyclerViewStatusSupport recyclerView,String url,Type type,final Map headers){
        GsonRequest gsonRequest = new GsonRequest<ArrayListResponse>(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest<ArrayListResponse>(){
            @Override
            public void onStart() {
                recyclerView.showLoadingView();
            }
            @Override
            public void onResponse(final ArrayListResponse response) {
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                    recyclerView.showErrorView(response.getError());
                }else {
                    adapter.appendToList((List)response.getDatas());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                recyclerView.showErrorView(error.getMessage());
            }

            @Override
            public Map getPostData() {
                return headers;
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid","1");
                datas.put("pagenum","10");
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
    public void read(final Context context,final BaseRecyclerAdapter adapter,final RecyclerViewStatusSupport recyclerView,String url,Type type,final Map headers){
        GsonRequest gsonRequest = new GsonRequest<ArrayListResponse>(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest<ArrayListResponse>(){
            @Override
            public void onStart() {
                recyclerView.showLoadingView();
            }
            @Override
            public void onResponse(final ArrayListResponse response) {
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                    recyclerView.showErrorView(response.getError());
                }else {
                    adapter.appendToList((List)response.getDatas());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                recyclerView.showErrorView(error.getMessage());
            }

            @Override
            public Map getPostData() {
                return headers;
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid","1");
                datas.put("pagenum","10");
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
    /*
    public void update(final Context context,final BaseRecyclerAdapter adapter,String url,final int position){
        Type type = new TypeToken() {}.getType();
        GsonRequest gsonRequest = new GsonRequest(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest(){
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
    public void delete(final Context context, final BaseRecyclerAdapter adapter,String url,final int position){
        Type type = new TypeToken() {}.getType();
        GsonRequest gsonRequest = new GsonRequest(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest(){
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
    */
}
