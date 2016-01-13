package com.lessask.crud;

import java.util.List;

/**
 * Created by JHuang on 2016/1/13.
 */
public interface AdapterAction<T> {

    void appendToList(List<T> list);
    void append(T t);
    void appendToTop(T item);
    void appendToTopList(List<T> list);
    void remove(int position);
    void update(int position,T obj);
    void notifyDataSetChanged();
    void notifyItemRemoved(int position);
}
