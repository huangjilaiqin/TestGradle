package com.lessask.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by JHuang on 2015/12/5.
 */
public class RecyclerViewInSwipeRefreshStatusSupport extends RecyclerView{

    private String TAG = RecyclerViewInSwipeRefreshStatusSupport.class.getSimpleName();
    private View emptyView;
    private View loadingView;
    private View errorView;
    private OnErrorListener onErrorListener;
    private ViewGroup parent;

    public RecyclerViewInSwipeRefreshStatusSupport(Context context) {
        super(context);
    }
    public void setOnErrorListener (OnErrorListener onErrorListener ) {
        this.onErrorListener = onErrorListener;
    }

    public RecyclerViewInSwipeRefreshStatusSupport(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewInSwipeRefreshStatusSupport(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void showErrorView(){
        Log.e(TAG, "showErrorView start");
        if(errorView!=null){
            Log.e(TAG, "showErrorView begin");

            ViewGroup.LayoutParams layoutParams = parent.getChildAt(0).getLayoutParams();
            Log.e(TAG, "h"+layoutParams.height+", w"+layoutParams.width);
            parent.removeAllViews();
            parent.addView(errorView, loadingView.getLayoutParams());
            if(onErrorListener!=null){
                onErrorListener.setErrorText(errorView);
            }
            //showEmptyView();
        }
    }

    public void showEmptyView(){
        if(emptyView!=null){
            ViewGroup.LayoutParams layoutParams = parent.getChildAt(0).getLayoutParams();
            parent.removeAllViews();
            //parent.addView(emptyView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            parent.addView(emptyView, layoutParams);
        }
    }

    public void showLoadingView(){
        if(loadingView!=null){
            ViewGroup.LayoutParams layoutParams = parent.getChildAt(0).getLayoutParams();
            Log.e(TAG, "child count:"+parent.getChildCount());
            Log.e(TAG, "h:"+layoutParams.height+", w:"+layoutParams.width);
            parent.removeAllViews();
            layoutParams.width = 300;
            layoutParams.height = 300;
            parent.addView(loadingView, layoutParams);
        }
    }

    public void setStatusViews(View loadingView, View emptyView, View errorView){
        this.loadingView = loadingView;
        this.emptyView = emptyView;
        this.errorView = errorView;
        parent = (ViewGroup) getParent();

        if(parent.getChildAt(0)== emptyView){
            Log.e(TAG, "equal 0");
        }
        if(parent.getChildAt(1)==emptyView){
            Log.e(TAG, "equal 1");

        }
        if(parent.getChildAt(2)==emptyView){
            Log.e(TAG, "equal 2");
        }


        ViewGroup.LayoutParams layoutParams = parent.getChildAt(0).getLayoutParams();
        Log.e(TAG, "first h"+layoutParams.height+", w"+layoutParams.width);
        layoutParams = parent.getChildAt(1).getLayoutParams();
        Log.e(TAG, "first h"+layoutParams.height+", w"+layoutParams.width);

        showLoadingView();
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
                    parent.removeAllViews();
                    parent.addView(emptyView);
                }else {
                    parent.removeAllViews();
                    parent.addView(RecyclerViewInSwipeRefreshStatusSupport.this);
                }
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            Adapter adapter = getAdapter();
            Log.e(TAG, "onItemRangeRemoved count:"+adapter.getItemCount());
            if(adapter.getItemCount()==0){
                parent.removeAllViews();
                parent.addView(emptyView);
            }else {
                parent.removeAllViews();
                parent.addView(RecyclerViewInSwipeRefreshStatusSupport.this);
            }
        }
    };
    public interface OnErrorListener{
        void setErrorText(View view);
    }
}
