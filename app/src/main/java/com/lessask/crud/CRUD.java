package com.lessask.crud;

import com.lessask.recyclerview.RecyclerViewStatusSupport;

/**
 * Created by JHuang on 2016/1/12.
 * 实体类继承CRUD,在增删改查四个方法都是GsonRequest的话使用DefaultGsonRequestCRUD辅助类
 * 比较特别的请求
 */

public interface CRUD<T>{
    void create(T obj,int position);
    void read(RecyclerViewStatusSupport recyclerView);
    void update(T obj,int position);
    void delete(T obj,int position);
}
