package com.lessask.recyclerview;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import com.lessask.R;

public class ImprovedSwipeLayout extends SwipeRefreshLayout {

    private static final String TAG = ImprovedSwipeLayout.class.getCanonicalName();
    private int mScrollableChildId;
    private View mScrollableChild;

    public ImprovedSwipeLayout(Context context) {
        this(context, null);
    }

    public ImprovedSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ImprovedSwipeLayoutAttrs);
        mScrollableChildId = a.getResourceId(R.styleable.ImprovedSwipeLayoutAttrs_scrollableChildId, 0);
        mScrollableChild = findViewById(mScrollableChildId);
        a.recycle();
    }

    @Override
    public boolean canChildScrollUp() {
        ensureScrollableChild();

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mScrollableChild instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mScrollableChild;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mScrollableChild.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mScrollableChild, -1);
        }
    }

    private void ensureScrollableChild() {
        if (mScrollableChild == null) {
            mScrollableChild = findViewById(mScrollableChildId);
        }
    }
}