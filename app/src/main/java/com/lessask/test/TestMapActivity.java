package com.lessask.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.lessask.R;
import com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class TestMapActivity extends Activity implements View.OnClickListener, OnMapDrawFrameCallback {
    private final String TAG = TestMapActivity.class.getName();

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private LocationListener mLocationListener;
    private boolean isFirstLocate = true;
    private LocationManager mLocationManager;
    private String provider;

    private Button bBaidu;
    private Button bSystem;
    private double lat;
    private double lon;
    private List<LatLng> myload;
    private float[] vertexs;
    private FloatBuffer vertexBuffer;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_test_map);
        //setContentView(R.layout.activity_main);
        initUI();
        init();
        myload = new ArrayList<>();
        mBaiduMap.setOnMapDrawFrameCallback(this);
        //PolygonOptions polygonOption = new PolygonOptions().points(myload);

        //mBaiduMap.addOverlay(polygonOption);


        //*/
        /*
        Log.e(TAG, "use provider:"+provider);
        */
        mLocationClient.start();
        log("baidu start...");
    }

    private void initUI(){
        bBaidu = (Button)findViewById(R.id.baidu);
        bSystem = (Button)findViewById(R.id.system);
        mMapView = (MapView) findViewById(R.id.bmapview);
        bBaidu.setOnClickListener(this);
        bSystem.setOnClickListener(this);
    }

    private void init() {
        //获取地图控制器
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        initLocation();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = mLocationManager.getAllProviders();
        Log.e(TAG, "providers:" + providerList.toString());
        if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }else if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        }else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_LONG).show();
            return;
        }

    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1500;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mLocationClient.registerNotifyLocationListener(myListener);

        mLocationListener = new LocationListener() {
            @Override
            public void onProviderEnabled(String provider) {
                log("onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                log("onProviderDisabled");
            }

            @Override
            public void onLocationChanged(Location location) {
                if(location!=null){
                    log("onLocationChanged:" + location.toString());
                    Toast.makeText(TestMapActivity.this, "system onLocationChanged", Toast.LENGTH_LONG).show();
                    navigateTo(location.getLatitude(), location.getLongitude());
                    myload.add(new LatLng(lat, lon));
                    //mMapView.refreshDrawableState();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                log("onStatusChanged");
            }
        };
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
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            myload.add(new LatLng(lat, lon));
            mMapView.invalidate();

            log("receiveLocation," + myload.size());
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

            navigateTo(location.getLatitude(), location.getLongitude());


            /*标注某个位置
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
            //准备 marker option 添加 marker 使用
            MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(center);
            //获取添加的 marker 这样便于后续的操作
            Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
            */


            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
        }
    }
    private void navigateTo(double latitude, double longitude){
        if(isFirstLocate){
            LatLng ll = new LatLng(latitude, longitude);
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 20);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 20);
        mBaiduMap.animateMapStatus(update);
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(latitude);
        locationBuilder.longitude(longitude);
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }

    private void baiduFixed(){
        //百度地图sdk定位
        //*
        mLocationClient.stop();
        log("baidu requestLocation...");
        mLocationClient.start();
    }

    private void systemFixed(){
        Log.e(TAG, "System fixed");
        Location location = mLocationManager.getLastKnownLocation(provider);
        if(location!=null){
            navigateTo(location.getLatitude(), location.getLongitude());
        }
        mLocationManager.requestLocationUpdates(provider, 5000, 1, mLocationListener);
    }
    private void cancleSystemFixed(){
        Log.e(TAG, "cancle System fixed");
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.baidu:
                baiduFixed();
                break;
            case R.id.system:
                Button b = (Button)v;
                String name = b.getText().toString();
                if(name.equals("系统定位")){
                    systemFixed();
                    b.setText("取消系统定位");

                }else if(name.equals("取消系统定位")){
                    b.setText("系统定位");
                    cancleSystemFixed();
                }
                break;
        }
    }
    private void log(String content){
        Log.e(TAG, content);
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapDrawFrame(GL10 gl10, MapStatus mapStatus) {

        if (mBaiduMap.getProjection() != null) {
            calPolylinePoint(mapStatus);
            drawPolyline(gl10, Color.argb(255, 255, 0, 0), vertexBuffer, 10, 3, mapStatus);
            Log.e(TAG, "onMapDrawFrame,size:" + myload.size());
            //drawTexture(gl10, mapStatus);
        }
    }
     public void calPolylinePoint(MapStatus mspStatus) {
        PointF[] polyPoints = new PointF[myload.size()];
        Point[] points = new Point[myload.size()];
        vertexs = new float[3 * myload.size()];
        int i = 0;
        for (LatLng xy : myload) {
            /**
             * public PointF toOpenGLLocation(LatLng location,MapStatus mapStatus)
             * 将地理坐标转换成openGL坐标，在 OnMapDrawFrameCallback 的 onMapDrawFrame 函数中使用。
             * @param location - 地理坐标 如果传入 null 则返回null
             *        mapStatus - 地图每一帧绘制时的地图状态
             * @return openGL坐标
             * */
            points[i] = mBaiduMap.getProjection().toScreenLocation(xy);
            polyPoints[i] = mBaiduMap.getProjection().toOpenGLLocation(xy, mspStatus);

            //*
            vertexs[i * 3] = polyPoints[i].x;
            vertexs[i * 3 + 1] = polyPoints[i].y;
            //*/
            /*
            vertexs[i * 3] = points[i].x;
            vertexs[i * 3 + 1] = points[i].y;
            */
            vertexs[i * 3 + 2] = 0.0f;
            i++;
        }
        for (int j = 0; j < vertexs.length; j++) {
            Log.e(TAG, "vertexs[" + j + "]: " + vertexs[j]);
            if((j+3)%3 == 0){
                Log.e(TAG, myload.get(j).toString());
            }
        }
        vertexBuffer = makeFloatBuffer(vertexs);
    }

    private FloatBuffer makeFloatBuffer(float[] fs) {
        ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(fs);
        fb.position(0);
        return fb;
    }

    private void drawPolyline(GL10 gl, int color, FloatBuffer lineVertexBuffer,
            float lineWidth, int pointSize, MapStatus drawingMapStatus) {

        if(myload.size()>1){
            //gl.glEnable(GL10.GL_BLEND);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            //gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

            float colorA = Color.alpha(color) / 255f;
            float colorR = Color.red(color) / 255f;
            float colorG = Color.green(color) / 255f;
            float colorB = Color.blue(color) / 255f;

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVertexBuffer);
            gl.glColor4f(colorR, colorG, colorB, colorA);
            gl.glLineWidth(lineWidth);
            int size = myload.size();
            for(int i=0;i<size-1;i++){
                byte[] line = {(byte)i, (byte)(i+1)};
                gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(line));
            }

            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointSize);

            gl.glDisable(GL10.GL_BLEND);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

    }
    /*
    int textureId = -1;
    // 使用opengl坐标绘制
    public void drawTexture(GL10 gl, MapStatus drawingMapStatus) {
        PointF p1 = mBaiduMap.getProjection().toOpenGLLocation(latlng2,
                drawingMapStatus);
        PointF p2 = mBaiduMap.getProjection().toOpenGLLocation(latlng3,
                drawingMapStatus);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 3 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = byteBuffer.asFloatBuffer();
        vertices.put(new float[] { p1.x, p1.y, 0.0f, p2.x, p1.y, 0.0f, p1.x,
                p2.y, 0.0f, p2.x, p2.y, 0.0f });

        ByteBuffer indicesBuffer = ByteBuffer.allocateDirect(6 * 2);
        indicesBuffer.order(ByteOrder.nativeOrder());
        ShortBuffer indices = indicesBuffer.asShortBuffer();
        indices.put(new short[] { 0, 1, 2, 1, 2, 3 });

        ByteBuffer textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4);
        textureBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer texture = textureBuffer.asFloatBuffer();
        texture.put(new float[] { 0, 1f, 1f, 1f, 0f, 0f, 1f, 0f });

        indices.position(0);
        vertices.position(0);
        texture.position(0);

        // 生成纹理
        if (textureId == -1) {
            int textureIds[] = new int[1];
            gl.glGenTextures(1, textureIds, 0);
            textureId = textureIds[0];
            Log.d(TAG, "textureId: " + textureId);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_NEAREST);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        }

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        // 绑定纹理ID
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture);

        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 6, GL10.GL_UNSIGNED_SHORT,
                indices);

        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_BLEND);
    }
    */
}


