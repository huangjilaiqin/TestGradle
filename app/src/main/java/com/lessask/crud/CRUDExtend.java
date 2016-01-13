package com.lessask.crud;

/**
 * Created by JHuang on 2016/1/13.
 */
public interface CRUDExtend<T> extends CRUD<T>{
    void deleteAndAdd(T obj,int position);
}
