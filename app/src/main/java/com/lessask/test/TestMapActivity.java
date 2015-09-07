package com.lessask.test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.lessask.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class TestMapActivity extends Activity implements View.OnClickListener,BaiduMap.OnMapDrawFrameCallback {
    private final String TAG = TestMapActivity.class.getName();

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener myLocationListener = new MyLocationListener();

    private boolean isFirstLocate = true;

    private Button bBaidu;
    //存储所有变化的点
    private List<LatLng> myload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_test_map);
        init();
        //开始定位
        mLocationClient.start();
        log("baidu start...");
    }

    private void init(){
        myload = new ArrayList<>();

        bBaidu = (Button)findViewById(R.id.baidu);
        mMapView = (MapView) findViewById(R.id.bmapview);
        bBaidu.setOnClickListener(this);
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
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            myload.add(new LatLng(lat, lon));
            navigateTo(location.getLatitude(), location.getLongitude());
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

    private void baiduFixed(){
        //手动请求定位
        log("baidu requestLocation...");
        mLocationClient.requestLocation();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.baidu:
                baiduFixed();
                break;
        }
    }
    private void log(String content){
        Log.e(TAG, content);
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapDrawFrame(GL10 gl10, MapStatus mapStatus) {
        if(mBaiduMap.getProjection()!=null) {
            int myloadSize = myload.size();
            PointF[] pointfs = new PointF[myloadSize];
            float[] vertexs = new float[myload.size() * 3];
            Log.e(TAG, "" + myload.size());
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
}


