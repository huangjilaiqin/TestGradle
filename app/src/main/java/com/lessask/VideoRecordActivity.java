package com.lessask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lessask.util.CameraHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class VideoRecordActivity extends Activity  {
    private static final String TAG = "CAMERA_TUTORIAL";

    private SurfaceView surfaceView;
    private Button mStart;
    private Button mStop;
    private SurfaceHolder surfaceHolder;

    private Camera camera;
    private Camera.Parameters cameraParams;
    private CameraView cameraView;
    private int cameraId = -1, cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;// 默认为后置摄像头

    private DisplayMetrics displayMetrics;
    private int screenWidth, screenHeight;// 竖屏为准
	private int previewWidth, previewHeight;// 横屏为准
    private RelativeLayout surfaceLayout;

    private boolean previewRunning;
    private boolean initSuccess;
    private boolean isFirstFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        displayMetrics = getResources().getDisplayMetrics();
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
        Log.e(TAG, "screenSize w:"+screenWidth+", h:"+screenHeight);

        mStart = (Button) findViewById(R.id.btn_recorder_record);
        mStop = (Button) findViewById(R.id.btn_recorder_finish);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //录制之前要unlock否则会出错, 到底是lock还是unlock？
                camera.unlock();
                mediaRecorder.start();
            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
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
        initCamera();
    }

    /*
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
            mediaRecorder.setVideoSize(640, 480);
            mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
            mediaRecorder.setVideoEncodingBitRate(5*1024*1024);

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
    */

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

    class CameraView extends SurfaceView implements SurfaceHolder.Callback,
			Camera.PreviewCallback {
		private SurfaceHolder mHolder;

		public CameraView(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(CameraView.this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			camera.setPreviewCallback(CameraView.this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.e(TAG, ".surfaceCreated() called!");
			try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.e(TAG, ".surfaceChanged() called!");
			handleSurfaceChanged();
			camera.startPreview();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.e(TAG, ".surfaceDestroyed() called!");
			// if (null != camera) {
			// camera.stopPreview();
			// camera.release();
			// camera = null;
			// }
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

		}

	}
    private void handleSurfaceChanged() {
        if (null == camera) {
            return;
        }
        cameraParams.setPreviewFrameRate(30);
        // 根据预设宽高获取相机支持的预览尺寸
        Camera.Size previewSize = CameraHelper.getOptimalPreviewSize(camera,
                previewWidth, previewHeight);
        if (null != previewSize) {
            previewWidth = previewSize.width;
            previewHeight = previewSize.height;
            cameraParams.setPreviewSize(previewWidth, previewHeight);
        }
        Log.e(TAG, "preivewSize w:"+previewWidth+", h:"+previewHeight);
        camera.setDisplayOrientation(90);
        /*
		// 摄像头自动对焦,SDK2.2以上不支持
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            List<String> focusModes = cameraParams.getSupportedFocusModes();
            if (focusModes != null) {
                if (((Build.MODEL.startsWith("GT-I950"))
                        || (Build.MODEL.endsWith("SCH-I959")) || (Build.MODEL
                        .endsWith("MEIZU MX3")))
                        && focusModes
                        .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    cameraParams
                            .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (focusModes
                        .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    cameraParams
                            .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else
                    cameraParams
                            .setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            }
        }
    */
		camera.setParameters(cameraParams);
	}
    private void initCamera() {
		new AsyncTask<Void, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
                try {
                    // 对于SDK2.2以上的，可能有多个摄像头
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                        int numberOfCameras = Camera.getNumberOfCameras();
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int i = 0; i < numberOfCameras; i++) {
                            Camera.getCameraInfo(i, cameraInfo);
                            if (cameraInfo.facing == cameraFacing) {
                                cameraId = i;
                            }
                        }
                    }
                    if (cameraId >= 0) {
                        camera = Camera.open(cameraId);
                    } else {
                        camera = Camera.open();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                initRecorder();
                return true;
            }

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (!result) {
                    finish();
                    return;
                }
				cameraParams = camera.getParameters();
				cameraView = new CameraView(VideoRecordActivity.this);
				handleSurfaceChanged();
				surfaceLayout = (RelativeLayout) findViewById(R.id.layout_recorder_surface);
				if (null != surfaceLayout && surfaceLayout.getChildCount() > 0)
					surfaceLayout.removeAllViews();
                int layoutHeight = (int) (screenWidth * (previewWidth / (previewHeight * 1f)));
				RelativeLayout.LayoutParams lpCameraView = new RelativeLayout.LayoutParams(
						screenWidth,layoutHeight);
                Log.e(TAG, "layoutSize w:"+screenWidth+", "+layoutHeight);
				lpCameraView.addRule(RelativeLayout.ALIGN_PARENT_TOP,
						RelativeLayout.TRUE);
				surfaceLayout.addView(cameraView, lpCameraView);
			}
		}.execute();
	}
    private void initRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        //mediaRecorder.setMaxDuration(maxDurationInMs);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        //mediaRecorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());
        mediaRecorder.setVideoSize(640, 480);
        mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);

        tempFile = new File(Environment.getExternalStorageDirectory(), "testVideo.mp4");
        Log.e(TAG, "tmpFile:" + tempFile.getAbsolutePath());
        mediaRecorder.setOutputFile(tempFile.getPath());
        mediaRecorder.setMaxFileSize(maxFileSizeInBytes);

        try{
            mediaRecorder.prepare();
            //mediaRecorder.start();
        }catch (Exception e){
            Log.e(TAG, "prepare Exception:"+e.toString());
        }

	}
}
