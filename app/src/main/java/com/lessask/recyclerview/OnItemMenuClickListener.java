package com.lessask.recyclerview;

import android.view.View;

/**
 * Created by huangji on 2015/11/25.
 */
public interface OnItemMenuClickListener {
    public void onItemMenuClick(View view, int position);
    public void onItemMenuClick(View view, Object obj);
}
