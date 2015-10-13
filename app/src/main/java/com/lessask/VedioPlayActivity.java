package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


public class VedioPlayActivity extends Activity implements TextureView.SurfaceTextureListener
        ,OnClickListener,OnCompletionListener{

    private final int SELECT_TAGS = 1;
    private final String TAG = VedioPlayActivity.class.getSimpleName();
    private String path;
    private TextureView surfaceView;
    private MediaPlayer mediaPlayer;
    private ImageView imagePlay;
    private ImageView mEditTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_play);

        mEditTags = (ImageView) findViewById(R.id.edit_tags);
        mEditTags.setOnClickListener(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        surfaceView = (TextureView) findViewById(R.id.preview_video);

        //修改控件大小
        RelativeLayout preview_video_parent = (RelativeLayout)findViewById(R.id.preview_video_parent);
        LayoutParams layoutParams = (LayoutParams) preview_video_parent.getLayoutParams();
        layoutParams.width = displaymetrics.widthPixels/2;
        layoutParams.height = displaymetrics.heightPixels/2;
        preview_video_parent.setLayoutParams(layoutParams);

        surfaceView.setSurfaceTextureListener(this);
        surfaceView.setOnClickListener(this);

        path = getIntent().getStringExtra("path");

        imagePlay = (ImageView) findViewById(R.id.preview_play);
        imagePlay.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    protected void onStop() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            imagePlay.setVisibility(View.GONE);
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            mediaPlayer.setDataSource(path);
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            //mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG, "prepared");
                    //mediaPlayer.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(TAG, "error:" + mp + ", what:" + what + ", extra:" + extra);
                    return false;
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.seekTo(0);

        } catch (Exception e) {
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        Log.e(TAG, "onSurfaceTextureAvailable prepare");
        Surface surface = new Surface(arg0);
        prepare(surface);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_play:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                imagePlay.setVisibility(View.GONE);
                break;
            case R.id.preview_video:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    imagePlay.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.edit_tags:
                Intent intent = new Intent(this,SelectTagsActivity.class);
                startActivityForResult(intent, SELECT_TAGS);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void stop(){
        mediaPlayer.stop();
        Intent intent = new Intent(this,VideoRecordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "onCompletion");
        imagePlay.setVisibility(View.VISIBLE);
    }
}