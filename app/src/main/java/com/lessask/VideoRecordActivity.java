package com.lessask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lessask.util.CameraHelper;
import com.lessask.util.TipsDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class VideoRecordActivity extends Activity implements SurfaceHolder.Callback, View.OnTouchListener{
    private static final String TAG = "CAMERA_TUTORIAL";

    private SurfaceView surfaceView;
    private Button mStart;
    private SurfaceHolder surfaceHolder;
    private ProgressView progressView;

    private Camera camera;
    private Camera.Parameters cameraParams;
    private int cameraId = -1, cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;// 默认为后置摄像头

    private DisplayMetrics displayMetrics;
    private int screenWidth, screenHeight;// 竖屏为准
	private int previewWidth, previewHeight;// 横屏为准
    private float screenRate;
    private RelativeLayout surfaceLayout;

    private boolean previewRunning;
    private boolean initSuccess;
    private boolean isFirstFrame;
    //提示松开
    private boolean isTipUp;
    private TipsDialog tipUpDialog;
    //提示上移
    private boolean isTipMove;
    private TipsDialog tipMoveDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        displayMetrics = getResources().getDisplayMetrics();
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
        screenRate = screenWidth/(screenHeight*1f);
        Log.e(TAG, "screenSize w:" + screenWidth + ", h:" + screenHeight);

        tipMoveDialog = new TipsDialog(VideoRecordActivity.this, "上滑取消");
        tipMoveDialog.setGravity(Gravity.TOP|Gravity.CENTER, 0, 17);
        tipUpDialog = new TipsDialog(VideoRecordActivity.this, "松开取消");
        tipUpDialog.setGravity(Gravity.TOP|Gravity.CENTER, 0, 17);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder .setKeepScreenOn(true);
        surfaceHolder .addCallback(this);
        surfaceHolder .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mStart = (Button) findViewById(R.id.btn_recorder_record);
        progressView = (ProgressView) findViewById(R.id.progress_recorder);
        mStart.setOnTouchListener(this);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //录制之前要unlock否则会出错, 到底是lock还是unlock？
                //startRecording();
            }
        });
    }


    private MediaRecorder mediaRecorder;
    private final int maxDurationInMs = 20000;
    private final long maxFileSizeInBytes = 5000000;
    private final int videoFramesPerSecond = 16;
    private File tempFile;

    @Override
    protected void onResume() {
        super.onResume();
        //initCamera();
    }

    public boolean startRecording() {
        try {
            camera.unlock();
            if(mediaRecorder==null)
                mediaRecorder = new MediaRecorder();
            else
                mediaRecorder.reset();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setOrientationHint(90);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            //mediaRecorder.setMaxDuration(maxDurationInMs);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

            //mediaRecorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());
            Log.e(TAG, "w:" + surfaceView.getWidth() + ", h:" + surfaceView.getHeight());
            //mediaRecorder.setVideoSize(480, 480);
            mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
            mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);

            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            tempFile = new File(Environment.getExternalStorageDirectory(), "testVideo.mp4");
            Log.e(TAG, "tmpFile:" + tempFile.getAbsolutePath());
            mediaRecorder.setOutputFile(tempFile.getPath());
            mediaRecorder.setMaxFileSize(maxFileSizeInBytes);

            mediaRecorder.prepare();

            mediaRecorder.start();

            return true;
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void stopRecording() {
        Log.e(TAG, "stopRecording");
        camera.lock();
        mediaRecorder.stop();
        if(tempFile.exists()){
            Log.e(TAG, "tempFile exitsts: "+tempFile.getTotalSpace());
        }
        Intent intent = new Intent(this, VedioPlayActivity.class);
        intent.putExtra("path", tempFile.getAbsolutePath());
        intent.putExtra("imagePath", "");
        startActivity(intent);
    }

    public void cancleRecording() {
        camera.lock();
        mediaRecorder.stop();
        if(tempFile.exists()){
            tempFile.delete();
            Log.e(TAG, "cancleRecording, "+tempFile.exists());
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, ".surfaceCreated() called!");
        surfaceHolder = holder;
        camera = Camera.open();
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Toast.makeText(getApplicationContext(), "focus", Toast.LENGTH_LONG).show();
            }
        });
        if (camera != null) {
            cameraParams = camera.getParameters();
            List<String> list = cameraParams.getSupportedFocusModes();
            Camera.Size previewSize = CameraHelper.getPropPreviewSize(cameraParams.getSupportedPreviewSizes(), screenRate, screenWidth);
            Log.e(TAG, "resolution size:" + previewSize.width+", "+previewSize.height);
            cameraParams.setPreviewSize(previewSize.width, previewSize.height);

            camera.setParameters(cameraParams);
            camera.setDisplayOrientation(90);
        } else {
            Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, ".surfaceChanged() called!");
        //handleSurfaceChanged();
        //camera.startPreview();
        surfaceHolder = holder;
        if (previewRunning) {
            camera.stopPreview();
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            previewRunning = true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, ".surfaceDestroyed() called!");
        this.surfaceHolder = null;
        this.mediaRecorder = null;
        this.surfaceView = null;
        camera.stopPreview();
        previewRunning = false;
        camera.release();
    }

    private float downX;
    private float downY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int viewId = v.getId();
        if(viewId == R.id.btn_recorder_record){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                progressView.setCurrentState(ProgressView.State.START);
                downX = event.getRawX();
                downY = event.getRawY();
                isTipUp=false;
                tipMoveDialog.show();
                startRecording();
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                progressView.setCurrentState(ProgressView.State.PAUSE);
                float moveX = event.getRawX()-downX;
                float moveY = event.getRawY()-downY;
                if(tipUpDialog.isShowing())
                    tipUpDialog.dismiss();
                if(tipMoveDialog.isShowing())
                    tipMoveDialog.dismiss();
                if(moveY<-100) {
                    //取消录像
                    cancleRecording();
                }else {
                    //录像完成
                    stopRecording();
                }
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                float moveX = event.getRawX()-downX;
                float moveY = event.getRawY()-downY;
                Log.e(TAG, "moveY:" + moveY + ", moveX:" + moveX);
                if(moveY<-100){
                    if(!isTipUp){
                        tipMoveDialog.dismiss();
                        tipUpDialog.show();
                        isTipUp=true;
                    }
                }else if(moveY<-30) {
                    if(tipUpDialog.isShowing()){
                        tipUpDialog.dismiss();
                    }
                }
            }
        }
        return false;
    }
}
