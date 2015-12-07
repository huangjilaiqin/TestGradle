package com.lessask.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by JHuang on 2015/12/5.
 */
public class RecyclerViewStatusSupport extends RecyclerView{

    private String TAG = RecyclerViewStatusSupport.class.getSimpleName();
    private View emptyView;
    private View loadingView;
    private View errorView;

    public RecyclerViewStatusSupport(Context context) {
        super(context);
    }

    public RecyclerViewStatusSupport(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewStatusSupport(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        this.emptyView.setVisibility(INVISIBLE);
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
        this.loadingView.setVisibility(VISIBLE);
    }

    public void setErrorView(View errorView) {
        this.errorView = errorView;
        this.errorView.setVisibility(INVISIBLE);
    }

    public void showErrorView(){
        if(errorView!=null){
            loadingView.setVisibility(INVISIBLE);
            errorView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(getAdapter()!=null){
            adapter.unregisterAdapterDataObserver(dataObserver);
        }
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(dataObserver);
    }

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter adapter = getAdapter();
            loadingView.setVisibility(INVISIBLE);
            if(adapter!=null && emptyView!=null){
                Log.e(TAG, "onChanged count:"+adapter.getItemCount());
                if(adapter.getItemCount()==0){
                    RecyclerViewStatusSupport.this.setVisibility(INVISIBLE);
                    emptyView.setVisibility(VISIBLE);
                }else {
                    emptyView.setVisibility(INVISIBLE);
                    RecyclerViewStatusSupport.this.setVisibility(VISIBLE);
                }
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            Adapter adapter = getAdapter();
            Log.e(TAG, "onItemRangeRemoved count:"+adapter.getItemCount());
            if(adapter.getItemCount()==0){
                RecyclerViewStatusSupport.this.setVisibility(INVISIBLE);
                emptyView.setVisibility(VISIBLE);
            }else {
                emptyView.setVisibility(INVISIBLE);
                RecyclerViewStatusSupport.this.setVisibility(VISIBLE);
            }
        }
    };
}
