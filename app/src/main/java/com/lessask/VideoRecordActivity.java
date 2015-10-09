package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class VideoRecordActivity extends Activity  implements SurfaceHolder.Callback {
    private static final String TAG = "CAMERA_TUTORIAL";

    private SurfaceView surfaceView;
    private Button mStart;
    private Button mStop;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private boolean previewRunning;
    private boolean initSuccess;
    private boolean isFirstFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        surfaceView = (SurfaceView) findViewById(R.id.surface_camera);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(VideoRecordActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mStart = (Button) findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        camera = Camera.open();
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            camera.setParameters(params);
            camera.setDisplayOrientation(90);
        } else {
            Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.surfaceHolder = holder;
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
        this.surfaceHolder = null;
        this.mediaRecorder = null;
        this.surfaceView = null;
        camera.stopPreview();
        previewRunning = false;
        camera.release();
    }

    private MediaRecorder mediaRecorder;
    private final int maxDurationInMs = 20000;
    private final long maxFileSizeInBytes = 500000;
    private final int videoFramesPerSecond = 20;
    private File tempFile;

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
            Log.e(TAG, "w:"+surfaceView.getWidth()+", h:"+surfaceView.getHeight());
            mediaRecorder.setVideoSize(640,480);
            mediaRecorder.setVideoFrameRate(videoFramesPerSecond);

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
        mediaRecorder.stop();
        if(tempFile.exists()){
            Log.e(TAG, "tempFile exitsts: "+tempFile.getTotalSpace());
        }
        camera.lock();
        Intent intent = new Intent(this, VedioPlayActivity.class);
        intent.putExtra("path", tempFile.getAbsoluteFile());
        intent.putExtra("imagePath", "");
        startActivity(intent);
    }
}
