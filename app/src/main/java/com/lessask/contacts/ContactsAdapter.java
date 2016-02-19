package com.lessask.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lessask.model.User;
import com.lessask.recyclerview.BaseRecyclerAdapter;

/**
 * Created by huangji on 2016/2/19.
 */
public class ContactsAdapter extends BaseRecyclerAdapter<User,ContactsAdapter.ViewHolder>{
    public ContactsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
