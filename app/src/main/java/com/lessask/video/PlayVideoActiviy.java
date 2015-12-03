package com.lessask.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lessask.R;
import com.lessask.global.GlobalInfos;
import com.lessask.net.HttpHelper;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.File;
import java.io.IOException;

/**
 * 播放视频页面
 *
 * @author Martin
 */
public class PlayVideoActiviy extends Activity {

    public static final String TAG = "PlayVideoActiviy";

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    public static final String KEY_FILE_PATH = "file_path";

    private String filePath;

    private ScalableVideoView mScalableVideoView;
    private final int HANDLER_GETACTION_START = 1;
    private final int HANDLER_GETACTION_DONE = 2;
    private final int HANDLER_GETACTION_ERROR = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_GETACTION_START:
                    //显示转圈圈
                    break;
                case HANDLER_GETACTION_DONE:
                    //取消转圈圈
                    //播放视频
                    play();
                    break;
                case HANDLER_GETACTION_ERROR:
                    //取消转圈圈
                    Toast.makeText(PlayVideoActiviy.this, "down video error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final File videoFile = new File(intent.getStringExtra("video_path"));
        filePath = videoFile.getAbsolutePath();
        final String videoUrl = intent.getStringExtra("video_url");
        Log.d(TAG, "videoFile:" + videoFile.getAbsolutePath() + ", videoUrl:" + videoUrl);
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(this, "视频路径错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_play_video);
        mScalableVideoView = (ScalableVideoView) findViewById(R.id.video_view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)mScalableVideoView.getLayoutParams();
        layoutParams.width = globalInfos.getScreenWidth();
        //r = 宽/高
        float widthDivideHeightRatio = 320/240f;
        layoutParams.height = (int)(layoutParams.width/widthDivideHeightRatio);
        mScalableVideoView.setLayoutParams(layoutParams);
        try {
            // 这个调用是为了初始化mediaplayer并让它能及时和surface绑定
            mScalableVideoView.setDataSource("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!videoFile.exists()){
            new Thread(new Runnable() {
                Message msg = new Message();
                @Override
                public void run() {
                    msg.what = HANDLER_GETACTION_START;
                    handler.sendMessage(msg);

                    if(HttpHelper.httpDownload(videoUrl, videoFile.getAbsolutePath())) {
                        Log.e(TAG, "download success");
                        msg.what = HANDLER_GETACTION_DONE;
                        handler.sendMessage(msg);
                    }else {
                        msg.what = HANDLER_GETACTION_ERROR;
                        handler.sendMessage(msg);
                        Log.e(TAG, "download failed");
                    }
                }
            }).start();
        }else {
            play();
        }
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    private void play(){
        new Thread(){
            @Override
            public void run() {
                try {
                    mScalableVideoView.setDataSource(filePath);
                    mScalableVideoView.setLooping(true);
                    mScalableVideoView.prepare();
                    mScalableVideoView.start();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    Toast.makeText(PlayVideoActiviy.this, "播放视频异常", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }
}
