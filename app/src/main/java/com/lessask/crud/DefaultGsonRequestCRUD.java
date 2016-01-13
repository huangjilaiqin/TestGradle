package com.lessask.crud;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lessask.dialog.LoadingDialog;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.ResponseError;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by JHuang on 2016/1/12.
 */

public class DefaultGsonRequestCRUD {
    private final String TAG = DefaultGsonRequestCRUD.class.getSimpleName();
    public void create(final Context context,final AdapterAction adapter,String url,Type beanType,final Map headers,final int position){
        GsonRequest gsonRequest = new GsonRequest<ResponseError>(Request.Method.POST,url,beanType,new GsonRequest.PostGsonRequest<ResponseError>(){
            final LoadingDialog loadingDialog = new LoadingDialog(context);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(ResponseError response) {
                loadingDialog.cancel();
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(context, response.getError(), Toast.LENGTH_SHORT).show();
                }else {
                    adapter.update(position, this);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(context, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public Map getPostData() {
                return headers;
            }

            @Override
            public void setPostData(Map datas) {
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);

    }
    /*
    public void read(final Context context,final AdapterAction adapter,final RecyclerViewStatusSupport recyclerView,String url,Type type,final Map headers){
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
    */
    public void read(final Context context,final AdapterAction adapter,final RecyclerViewStatusSupport recyclerView,final String url,Type type,final Map headers){
        GsonRequest gsonRequest = new GsonRequest<ArrayListResponse>(Request.Method.POST,url,type,new GsonRequest.PostGsonRequest<ArrayListResponse>(){
            @Override
            public void onStart() {
                recyclerView.showLoadingView();
            }
            @Override
            public void onResponse(final ArrayListResponse response) {
                if(response.getError()!=null && response.getError()!="" || response.getErrno()!=0){
                    //Log.e(TAG, "onResponse error:" + response.getError() + ", " + response.getErrno());
                    recyclerView.showErrorView(response.getError());
                }else {
                    Log.e(TAG, url+" "+response.getDatas().size());
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
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
    public void update(final Context context,final AdapterAction adapter,String url,Type beanType, final Map headers,final int position){
        GsonRequest gsonRequest = new GsonRequest<ResponseError>(Request.Method.POST,url,beanType,new GsonRequest.PostGsonRequest<ResponseError>(){
            final LoadingDialog loadingDialog = new LoadingDialog(context);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(ResponseError response) {
                loadingDialog.cancel();
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(context, response.getError(), Toast.LENGTH_SHORT).show();
                }else {
                    adapter.update(position, this);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(context, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public Map getPostData() {
                return headers;
            }

            @Override
            public void setPostData(Map datas) {
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);

    }
    public void delete(final Context context, final AdapterAction adapter,String url,Type beanType, final Map headers,final int position){
        GsonRequest gsonRequest = new GsonRequest<ResponseError>(Request.Method.POST,url,beanType,new GsonRequest.PostGsonRequest<ResponseError>(){
            final LoadingDialog loadingDialog = new LoadingDialog(context);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(ResponseError response) {
                loadingDialog.cancel();
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(context, response.getError(), Toast.LENGTH_SHORT).show();
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
            public Map getPostData() {
                return headers;
            }

            @Override
            public void setPostData(Map datas) {
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

}
