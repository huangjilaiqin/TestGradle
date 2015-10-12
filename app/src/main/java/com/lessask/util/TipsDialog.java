package com.lessask.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lessask.ProgressView;
import com.lessask.R;

/**
 * Created by huangji on 2015/10/12.
 */
public class TipsDialog extends Dialog{
    private Context context;
    private String tips;
    private int mGravity;
    private int mX;
    private int mY;
    private TextView mTips;

    public TipsDialog(Context context, String tips) {
        super(context, R.style.tips_dialog);
        this.context = context;
        this.tips=tips;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_dialog);
        mTips = (TextView)findViewById(R.id.tips);
        mTips.setText(tips);
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.mGravity = gravity;
        this.mX = xOffset;
        this.mY = yOffset;
    }

    @Override
    public void show() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity=mGravity;
        params.x=mX;
        params.y=mY;
        params.alpha=0.8f;
        window.setAttributes(params);
        super.show();
    }
}
