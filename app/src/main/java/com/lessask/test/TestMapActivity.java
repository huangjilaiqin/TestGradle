package com.lessask.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.lessask.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.microedition.khronos.opengles.GL10;

import com.lessask.chat.Chat;
import com.lessask.chat.GlobalInfos;
import com.lessask.model.ResponseError;
import com.lessask.model.RunData;

public class TestMapActivity extends Activity implements BaiduMap.OnMapDrawFrameCallback{
    private final String TAG = TestMapActivity.class.getName();

    private final int CHANGE_TIME = 0;
    private final int CHANGE_MILEAGE = 1;
    private final int CHANGE_MPD = 2;
    private final int CHANGE_SPH = 3;
    private final int UPLOAD_ERROR = 4;
    private final int UPLOAD_SUCCESS = 5;

    private Gson gson;
    private Chat chat;
    private GlobalInfos globalInfos;

    private ProgressDialog uploadDialog;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener myLocationListener = new MyLocationListener();
    private TextView tvCostTime;
    private TextView tvMileage;
    private TextView tvSpeed;
    private TextView tvSpeedHour;

    private boolean isFirstLocate = true;
    private int cacheLocation = 2;
    //上一次计算的时间点
    private long lastCalculateTime;

    //存储所有变化的点
    private List<LatLng> myload;
    private List<Long> mytime;
    private float mTotalMileage;
    private int mCostTime;

     private Handler mHandler = new Handler() {
         @Override
         public void handleMessage (Message msg) {
             super.handleMessage(msg);
             DecimalFormat decimalFormat;
             switch (msg.what) {
             //时间
             case CHANGE_TIME:
                 SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                 formatter.setTimeZone(TimeZone.getTimeZone("utc"));
                 String costTime = formatter.format(mCostTime*1000);
                 tvCostTime.setText(costTime);
                 break;
             //里程
             case CHANGE_MILEAGE:
                 decimalFormat=new DecimalFormat("0.00");
                 tvMileage.setText(decimalFormat.format(mTotalMileage/1000));
                 break;
             //配速
             case CHANGE_MPD:
                 tvSpeed.setText(msg.arg1+"\'"+msg.arg2+"\"");
                 break;
             //时速
             case CHANGE_SPH:
                 decimalFormat=new DecimalFormat("0.00");
                 Log.e(TAG, "sph:"+msg.obj);
                 tvSpeedHour.setText(decimalFormat.format((double) msg.obj));
                 break;
             case UPLOAD_ERROR:
                 log("上传失败");
                 uploadDialog.cancel();
                 break;
             case UPLOAD_SUCCESS:
                 log("上传成功");
                 uploadDialog.cancel();
                 finish();
                 break;
             default:
                 break;
             }
         }
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_test_map);
        init();
        gson = new Gson();
        chat = Chat.getInstance();
        uploadDialog = new ProgressDialog(TestMapActivity.this, ProgressDialog.STYLE_SPINNER);
        chat.setUploadRunListener(new Chat.UploadRunListener(){
            @Override
            public void uploadRun(ResponseError error, int userId) {
                if(error!=null){
                    //log("上传失败,error:"+error.getError()+", errno:"+error.getErrno());
                    Message msg = new Message();
                    msg.what = UPLOAD_ERROR;
                    msg.obj = error;
                    mHandler.sendMessage(msg);
                }else {
                    Message msg = new Message();
                    msg.what = UPLOAD_SUCCESS;
                    mHandler.sendMessage(msg);
                }
            }
        });
        globalInfos = GlobalInfos.getInstance();

        String myloadStr = gson.toJson(myload);
        String mytimeStr = gson.toJson(mytime);
        Log.e(TAG, "myload:"+myloadStr);
        Log.e(TAG, "mytime:"+mytimeStr);
        //开始定位
        mLocationClient.start();
        log("baidu start...");
    }

    private void init(){
        myload = new ArrayList<>();
        mytime = new ArrayList<>();
        mTotalMileage = 0;
        mCostTime = 0;

        tvCostTime = (TextView) findViewById(R.id.time);
        tvMileage = (TextView) findViewById(R.id.mileage);
        tvSpeed = (TextView) findViewById(R.id.speed);
        tvSpeedHour = (TextView) findViewById(R.id.speed_hour);
        mMapView = (MapView) findViewById(R.id.bmapview);
        mMapView.showZoomControls(false);
        //获取地图控制器

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        //回调绘图
        mBaiduMap.setOnMapDrawFrameCallback(this);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //mLocationClient = new LocationClient(this);     //声明LocationClient类
        //位置改变回调 onReceiveLocation, 打印日志
        mLocationClient.registerLocationListener(myLocationListener );    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=3000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            if (location == null || mMapView == null)
				return;

            float radius = location.getRadius();
            int locType = location.getLocType();
            Log.e(TAG, "定位精度:" + radius);
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            LatLng currentNode = new LatLng(lat, lon);;

            //第一个的请求结果为缓存结果, 第三个结果但是精度可能不好
            if(cacheLocation>0){
                //中心点进行移动但是不加入路线中
                navigateTo(lat, lon);
                Log.e(TAG, "move to center");
                cacheLocation--;
                if(cacheLocation==0){
                    new TimeThread().start();
                    lastCalculateTime = System.currentTimeMillis()/1000;
                }
                return;
            }
            long nowTime = System.currentTimeMillis()/1000;
            if (locType == BDLocation.TypeGpsLocation) {// GPS定位结果
                Log.e(TAG, "gps定位");

                if(radius<=60) {
                    myload.add(currentNode);
                    navigateTo(lat, lon);

                    double distanceDelta = getDistanceChange();
                    long timeDelta = nowTime-lastCalculateTime;
                    mytime.add(timeDelta);
                    lastCalculateTime = nowTime;

                    updateMileage(distanceDelta);
                    updateSpeedPerHour(distanceDelta, timeDelta);
                    updateMinutePerDistance(distanceDelta, timeDelta);
                } else {
                    Log.e(TAG, "丢弃gps定位,精度:"+radius);
                }
            } else if (locType == BDLocation.TypeNetWorkLocation) {// 网络定位结
                Log.e(TAG, "网络定位");
                if(radius<=130){
                    myload.add(currentNode);
                    navigateTo(lat, lon);

                    double distanceDelta = getDistanceChange();
                    long timeDelta = nowTime-lastCalculateTime;
                    mytime.add(timeDelta);
                    lastCalculateTime = nowTime;

                    updateMileage(distanceDelta);
                    updateSpeedPerHour(distanceDelta, timeDelta);
                    updateMinutePerDistance(distanceDelta, timeDelta);
                }else {
                    Log.e(TAG, "丢弃网络定位,精度:"+radius);
                }
            } else if (locType == BDLocation.TypeOffLineLocation) {// 离线定位
                Log.e(TAG, "离线定位");
            } else if (locType == BDLocation.TypeServerError) {
                Log.e(TAG,"服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (locType == BDLocation.TypeNetWorkException) {
                Log.e(TAG,"网络不同导致定位失败，请检查网络是否通畅");
            } else if (locType == BDLocation.TypeCriteriaException) {
                Log.e(TAG,"无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            } else if(locType == BDLocation.TypeNone){
                //当打开wifi, 但wifi需要登录验证, 未通过时会产生该错误
                Log.e(TAG, "无效定位结果，一般由于定位SDK内部逻辑异常时出现");
            } else {
                Log.e(TAG, "未知错误定位类型:"+locType);
            }
        }
    }
    private void navigateTo(double latitude, double longitude){
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(latitude);
        locationBuilder.longitude(longitude);
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);

        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate update = null;
        if(isFirstLocate){
            update = MapStatusUpdateFactory.newLatLngZoom(ll, 18);
            isFirstLocate = false;
        }else {
            update = MapStatusUpdateFactory.newLatLng(ll);
        }
        mBaiduMap.animateMapStatus(update);

    }

    private void log(String content){
        Log.e(TAG, content);
        Toast.makeText(TestMapActivity.this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapDrawFrame(GL10 gl10, MapStatus mapStatus) {
        if(mBaiduMap.getProjection()!=null) {
            int myloadSize = myload.size();
            PointF[] pointfs = new PointF[myloadSize];
            float[] vertexs = new float[myload.size() * 3];
            for (int i=0;i<myloadSize;i++) {
                LatLng xy = myload.get(i);
                pointfs[i] = mBaiduMap.getProjection().toOpenGLLocation(xy, mapStatus);

                vertexs[i * 3] = pointfs[i].x;
                vertexs[i * 3 + 1] = pointfs[i].y;
                vertexs[i * 3 + 2] = 0.0f;
            }
            FloatBuffer vertexBuffer = makeFloatBuffer(vertexs);
            gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            for(int i=0;i<myloadSize-1;i++) {
                byte[] line = {(byte)i, (byte)(i+1)};
                drawLine(gl10, Color.argb(255, 255, 0, 0), 10, 3, mapStatus, line);
            }
        }
    }
    private FloatBuffer makeFloatBuffer(float[] fs) {
        ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(fs);
        fb.position(0);
        return fb;
    }
    private void drawLine(GL10 gl, int color,
            float lineWidth, int pointSize, MapStatus drawingMapStatus, byte[] line) {

        gl.glEnable(GL10.GL_BLEND);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        float colorA = Color.alpha(color) / 255f;
        float colorR = Color.red(color) / 255f;
        float colorG = Color.green(color) / 255f;
        float colorB = Color.blue(color) / 255f;

        gl.glColor4f(colorR, colorG, colorB, colorA);
        gl.glLineWidth(lineWidth);
        //gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointSize);
         gl.glDrawElements(GL10.GL_LINE_LOOP, 2, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(line));

        gl.glDisable(GL10.GL_BLEND);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
    public class TimeThread extends Thread {
         @Override
         public void run () {
             do {
                 try {
                     Thread.sleep(1000);
                     mCostTime++;
                     Message msg = new Message();
                     msg.what = CHANGE_TIME;
                     mHandler.sendMessage(msg);
                 }
                 catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             } while(true);
         }
    }
    private double getDistanceChange(){
        int size = myload.size();
        if(size<2){
            return 0;
        }
        double distance = DistanceUtil.getDistance(myload.get(size - 2), myload.get(size - 1));
        if(distance!=-1) {
            return distance;
        }else {
            return 0;
        }
    }
    private void updateMileage(double distanDelta){
        mTotalMileage += distanDelta;
        Message msg = new Message();
        msg.what = CHANGE_MILEAGE;
        mHandler.sendMessage(msg);
    }
    //配速计算
    private void updateMinutePerDistance(double distanceDelta, long timeDelta){
        int totalSecond = (int)(timeDelta/distanceDelta*1000);
        int minute = totalSecond/60;
        if(minute>99)
            minute = 99;
        int second = totalSecond%60;
        Log.e(TAG, "timeDelta:"+timeDelta+",distanceDelta:"+distanceDelta+",totalSecond:"+totalSecond+",minute:"+minute+", second:"+second);
        Message msg = new Message();
        msg.what = CHANGE_MPD;
        msg.arg1 = minute;
        msg.arg2 = second;
        mHandler.sendMessage(msg);
    }
    //时速计算
    private void updateSpeedPerHour(double distanceDelta, long timeDelta){
        double speed = distanceDelta/timeDelta*3.6;
        Message msg = new Message();
        msg.what = CHANGE_SPH;
        msg.obj = speed;
        mHandler.sendMessage(msg);
    }

    private void finishRun(){
        //上传运动数据
        RunData runData = new RunData(globalInfos.getUserid(), myload, mytime);
        String rundataStr = gson.toJson(runData);
        //log("rundataStr:" + rundataStr);
        chat.emit("uploadrun", gson.toJson(runData));
        uploadDialog.setTitle("上传数据...");
        //dialog.setCancelable(false);
        uploadDialog.show();

    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestMapActivity.this);
        builder.setMessage("你真的不行了吗?");
        builder.setPositiveButton("不行了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                TestMapActivity.this.finishRun();
            }
        });
        builder.setNegativeButton("切~~~", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                log("你牛！！！");
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //这里重写返回键
            dialog();
            return true;
        }
        return false;
    }
}

