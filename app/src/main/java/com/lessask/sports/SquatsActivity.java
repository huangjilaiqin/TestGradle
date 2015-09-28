package com.lessask.sports;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lessask.R;

import java.text.DecimalFormat;

public class SquatsActivity extends Activity {
    private final String TAG = SquatsActivity.class.getSimpleName();
    private SensorManager mSensorManager;
    private int size = 0;
    private TextView mDate;
    private TextView mDate1;
    private double[] gValues = new  double[3];
    private double[] directionValues = new  double[3];
    private final int
        motionless = 0,
        up =1 ,
        downAcelerateBegin = 2,
        downAcelerateIng = 3,
        downAcelerateEnd =4,
        downDecelerateBegin = 5,
        downDecelerateIng = 6,
        downDecelerateEnd = 7,
        down = 8,
        upAcelerateBegin = 9,
        upAcelerateIng = 10,
        upAcelerateEnd = 11,
        upDecelerateBegin = 12,
        upDecelerateIng = 13,
        upDecelerateEnd = 14;
    //设置为静止状态
    private int currentState = motionless;
    private int detectMotinless;
    private double lastGravity = 9.8;
    private long lastCheck;
    private long statusCheckTime;
    private long lastChangeTime;
    private long deltaChangeTime;
    private double currentSpeed;
    private double aveG;
    private long statusRemainTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squats);
        mDate = (TextView)findViewById(R.id.data);
        mDate1 = (TextView)findViewById(R.id.data1);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //根据g值判断手机的静止方向
        Sensor gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor laSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(gSensor==null){
            Toast.makeText(this, "重力传感器", Toast.LENGTH_SHORT).show();
        }
        mSensorManager.registerListener(gListener, gSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(laListener, laSensor, SensorManager.SENSOR_DELAY_FASTEST);
        lastCheck = System.currentTimeMillis();
        detectMotinless = 0;
        currentSpeed = 0;
        lastChangeTime = System.currentTimeMillis();
        aveG = 0;
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(gListener);
        super.onDestroy();
    }

    private SensorEventListener gListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            gValues[0] = event.values[0];
            gValues[1] = event.values[1];
            gValues[2] = event.values[2];
            double acelereate = Math.sqrt(Math.pow(gValues[0], 2) + Math.pow(gValues[1], 2) + Math.pow(gValues[2], 2));
            //向下运动的变化过程(向上为逆过程) 9.8 -> 6 -> 14 -> 9.8
            long now = System.currentTimeMillis();
            long deltaChangeTime = now-lastChangeTime;

            DecimalFormat df = new DecimalFormat("0.00");
            String str = df.format(acelereate)+", "+df.format(event.values[0]) + "," + df.format(event.values[1]) + "," + df.format(event.values[2])+
                    ", "+currentState+", "+currentSpeed;
            Log.i(TAG, str);

            boolean statusChange = false;

            switch (currentState){
                case motionless:
                    if(speedIsZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        currentState = downAcelerateBegin;
                    }
                    break;
                case downAcelerateBegin:
                    if(speedBigThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        //依然满足该状态的条件
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>190){
                            currentState = downAcelerateIng;
                            statusRemainTime = 0;
                        }
                    }else {
                        //条件不满足另行处理
                    }
                    break;
                case downAcelerateIng:
                    if(speedBigThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>710){
                            //向下减速太久，置为无效状态
                            currentState = downAcelerateIng;
                            statusRemainTime = 0;
                        }
                    }else if(speedBigThanZero(currentSpeed) && !acelerateLessThanNormal(acelereate)){
                        currentState = downDecelerateBegin;
                        statusRemainTime = 0;
                    }
                    break;
                case downAcelerateEnd:
                    break;

                case downDecelerateBegin:
                    break;
                case downDecelerateIng:
                    break;
                case downDecelerateEnd:
                    break;

                case upAcelerateBegin:
                    break;

            }

            // [9.2,10.4] 为静止状态

            if(re<=9.3 || re>=10.3) {
                //速度向下为正，向上为负
                currentSpeed += 0.5 * (9.8 - re) * Math.sqrt(deltaChangeTime / 1000f);
            }
            if(re<=9.3 ){
                //向下加速过程
                if(currentState==up || currentState==motionless) {
                    //开始向下加速
                    currentState = downAcelerateBegin;
                    statusCheckTime = now;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==downAcelerateBegin && now-statusCheckTime>190){
                    if(now-statusCheckTime>900 && currentSpeed<0){
                        //向下加速超过额定时间,判为无效动作
                        Log.e(TAG, "向下加速超时");
                        return;
                    }else {
                        //向下加速达到额定时间
                        currentState = downAcelerateIng;
                        statusChange = true;
                        Log.e(TAG, ""+currentState);
                    }
                }else if(currentState==upDecelerateIng && currentSpeed>0){
                    //一次有效深蹲计数,因为没检测到最高点暂停的数据又直接向下加速了
                    currentState = downAcelerateBegin;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }

                //向上减速过程
                if(currentState==upAcelerateEnd || currentState==upAcelerateIng){
                    //开始向上减速
                    currentState = upDecelerateBegin;
                    statusCheckTime = now;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==upDecelerateBegin && now-statusCheckTime>190){
                    //向上减速超过额定时间判为无效动作
                    if(currentState==upDecelerateIng && now-statusCheckTime>900){
                        Log.e(TAG, "向上减速超时");
                        return;
                    }
                    //向上减速达到额定时间
                    currentState = upDecelerateIng;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }

            } else if(re>10.3){
                //向下运动的减速过程
                if(currentState==downAcelerateEnd || currentState==downAcelerateIng){
                    //开始向下减速
                    currentState = downDecelerateBegin;
                    statusCheckTime = now;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==downDecelerateBegin && now-statusCheckTime>190){
                    if(now-statusCheckTime>900 && currentSpeed>0){
                        //向下减速超过额定时间,判为无效动作
                        Log.e(TAG, "向下减速超时");
                        return;
                    }else if(currentSpeed>0){
                        //向下减速达到额定时间
                        currentState = downDecelerateIng;
                        statusChange = true;
                        Log.e(TAG, ""+currentState);
                    }
                }else if(currentState==downDecelerateIng && currentSpeed<0){
                    //一次有效深蹲计数,因为没检测到最低点暂停的数据又直接向上加速了
                    currentState = upAcelerateBegin;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }


                if(currentState==down || currentState==motionless){
                    //开始向上加速
                    currentState = upAcelerateBegin;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==upAcelerateBegin && now-statusCheckTime>190){
                    //向上加速超过额定时间,判为无效动作
                    if(currentState == upAcelerateIng && now-statusCheckTime>900){
                        Log.e(TAG, "向上加速超时");
                    }
                    //向上加速达到额定时间
                    currentState = upAcelerateIng;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }

            } else {
                if(currentState==downAcelerateIng){
                    //向下加速结束
                    currentState = downAcelerateEnd;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState== upAcelerateIng){
                    //向上加速结束
                    currentState = upAcelerateEnd;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState== downDecelerateIng){
                    //向下减速结束
                    currentState = downDecelerateEnd;
                    Log.e(TAG, ""+currentState);
                    currentState = down;
                    Log.e(TAG, ""+currentState);
                    statusChange = true;
                }else if(currentState==upDecelerateIng){
                    //向上减速结束
                    currentState=upDecelerateEnd;
                    Log.e(TAG, ""+currentState);
                    currentState = up;
                    Log.e(TAG, ""+currentState);
                    statusChange = true;
                }else {
                    detectMotinless++;
                    if(detectMotinless%4==0) {
                        //currentState = motionless;
                        detectMotinless=0;
                    }
                }
            }
            if(statusChange==false && now-statusCheckTime>2000){
                currentState = motionless;
            }
            if(currentState==down || currentState==up) {
                mDate.setText("" + currentState);
                //currentState = motionless;
            }
            mDate.setText(""+currentState);
            lastGravity = re;
            lastChangeTime = now;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.e(TAG, "accuracy:"+accuracy);
        }
    };
    private SensorEventListener laListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            DecimalFormat df = new DecimalFormat("0");
            double re = Math.sqrt(Math.pow(event.values[0], 2)+Math.pow(event.values[1], 2)+Math.pow(event.values[2], 2));
            String str = df.format(re)+", "+df.format(event.values[0]) + "," + df.format(event.values[1]) + "," + df.format(event.values[2]);
            //Log.e(TAG, str);
            size++;
            if(size%7==0) {
                mDate1.setText(str);
                size=0;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.e(TAG, "accuracy:" + accuracy);
        }
    };
    private boolean speedIsZero(double speed){
        if(speed<0.2 && speed>-0.2)
            return true;
        return false;
    }
    private boolean speedLessThanZero(double speed){
        if(speed<=-0.2)
            return true;
        return false;
    }
    private boolean speedBigThanZero(double speed){
        if(speed>=0.2)
            return true;
        return false;
    }
    private boolean acelerateLessThanNormal(double acelerate){
        if(acelerate<9.3)
            return true;
        return false;
    }
    private boolean acelerateBigThanNormal(double acelerate){
        if(acelerate>10.3)
            return true;
        return false;
    }
    private boolean acelerateIsNormal(double acelerate){
        if(acelerate>9.3 && acelerate<10.3)
            return true;
        return false;
    }
}
