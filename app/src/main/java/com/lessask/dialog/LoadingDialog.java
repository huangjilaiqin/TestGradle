package com.lessask.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lessask.R;

/**
 * Created by huangji on 2015/10/16.
 */
public class LoadingDialog extends Dialog{

    private String TAG = LoadingDialog.class.getSimpleName();
    private SimpleDraweeView mAnimatedGifView;
    public LoadingDialog(Context context) {
        super(context, R.style.nothing_dialog);
        Fresco.initialize(context);
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.loading, null);
        Log.e(TAG, "loadingDialog create");
        setContentView(view);



        mAnimatedGifView = (SimpleDraweeView) findViewById(R.id.loading);
        DraweeController animatedGifController = Fresco.newDraweeControllerBuilder()
        .setAutoPlayAnimations(true).setUri(Uri.parse("res://com.lessask/"+R.drawable.loading)).build();
        mAnimatedGifView.setController(animatedGifController);

    }
}
