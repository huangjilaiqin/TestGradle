package com.lessask.sports;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
    private TextView mTvSpeed;
    private double[] gAcelerator = new  double[3];
    private double[] linearAcelerator = new double[3];
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
    private double lastGravity = 9.8;
    private long lastChangeTime;
    private double currentSpeed;
    private long statusRemainTime = 0;
    private int ignoreDataSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squats);
        mDate = (TextView)findViewById(R.id.data);
        mDate1 = (TextView)findViewById(R.id.data1);
        mTvSpeed = (TextView)findViewById(R.id.speed);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //根据g值判断手机的静止方向
        Sensor gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor laSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(gSensor==null){
            Toast.makeText(this, "重力传感器", Toast.LENGTH_SHORT).show();
        }
        mSensorManager.registerListener(gListener, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(laListener, laSensor, SensorManager.SENSOR_DELAY_GAME);
        currentSpeed = 0;
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(gListener);
        super.onDestroy();
    }

    private SensorEventListener gListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            DecimalFormat df = new DecimalFormat("0.00");
            double alpha = 0.8;
            for(int i=0;i<3;i++){
                //用低通滤波器分离出重力加速度
                gAcelerator[i] = alpha * gAcelerator[i] + (1-alpha) * event.values[i];
                //用高通滤波器剔除重力干扰
                linearAcelerator[i] = event.values[i]-gAcelerator[i];
            }
            //Log.i(TAG, "g:"+gAcelerator[0]+", "+gAcelerator[1]+", "+gAcelerator[2]);
            String linearStr = "liner:"+df.format(linearAcelerator[0])+", "+df.format(linearAcelerator[1])+", "+df.format(linearAcelerator[2]);
            if(ignoreDataSize>0) {
                Log.i(TAG, "ignore:" + linearStr);
                ignoreDataSize--;
                return;
            }
            Log.i(TAG, linearStr);
            mDate.setText(linearStr);
            double acelereate = Math.sqrt(Math.pow(gAcelerator[0], 2) + Math.pow(gAcelerator[1], 2) + Math.pow(gAcelerator[2], 2));
            double lAcelerator = Math.sqrt(Math.pow(linearAcelerator[0], 2) + Math.pow(linearAcelerator[1], 2) + Math.pow(linearAcelerator[2], 2));

            //向下运动的变化过程(向上为逆过程) 9.8 -> 6 -> 14 -> 9.8
            long now = System.currentTimeMillis();
            long deltaChangeTime = 0;
            if(lastChangeTime != 0)
                deltaChangeTime = now-lastChangeTime;

             //计算重力在Y轴方向的量，即G*cos(α)
            double ratioY = gAcelerator[1]/SensorManager.GRAVITY_EARTH;
            if(ratioY > 1.0)
                ratioY = 1.0;
            if(ratioY < -1.0)
                ratioY = -1.0;
            //获得α的值，根据z轴的方向修正其正负值。
            double angleY = Math.toDegrees(Math.acos(ratioY));
            /*
            if(gAcelerator[2] < 0)
                angleY = - angleY;
                */

            double ratioX = gAcelerator[0]/SensorManager.GRAVITY_EARTH;
            if(ratioX > 1.0)
                ratioX = 1.0;
            if(ratioX < -1.0)
                ratioX = -1.0;
            //获得α的值，根据z轴的方向修正其正负值。
            double angleX = Math.toDegrees(Math.acos(ratioX));
            /*
            if(gAcelerator[2] < 0)
                angleX = - angleX;
                */
            double ratioZ = gAcelerator[2]/SensorManager.GRAVITY_EARTH;
            if(ratioZ > 1.0)
                ratioZ = 1.0;
            if(ratioZ < -1.0)
                ratioZ = -1.0;
            //获得α的值，根据z轴的方向修正其正负值。
            double angleZ = Math.toDegrees(Math.acos(ratioZ));
            /*
            if(gAcelerator[2] < 0)
                angleZ = - angleZ;
                */

            String angleStr = "angle:"+df.format(angleX)+", "+df.format(angleY)+", "+df.format(angleZ);
            Log.e(TAG, angleStr);
            mDate1.setText(angleStr);

            double xA = linearAcelerator[0]*Math.cos(ratioX);
            double yA = linearAcelerator[1]*Math.cos(ratioY);
            double zA = linearAcelerator[2]*Math.cos(ratioZ);
            //合线性加速度
            double aTotal = xA+yA+zA;
            //Log.e(TAG, "linear compare: "+df.format(lAcelerator)+", "+df.format(aTotal));

            double xG = gAcelerator[0]*Math.cos(ratioX);
            double yG = gAcelerator[1]*Math.cos(ratioY);
            double zG = gAcelerator[2]*Math.cos(ratioZ);
            double gTotal = xG+yG+zG;
            //Log.e(TAG, "g compare:"+df.format(acelereate)+", "+df.format(gTotal));

            //Log.e(TAG, "angle:"+df.format(angleX)+", "+df.format(angleY)+", "+df.format(angleZ));
            //Log.e(TAG, "gridy:"+df.format(event.values[0]) + "," + df.format(event.values[1]) + "," + df.format(event.values[2]));

            //currentSpeed = currentState + (9.8-acelereate)*(deltaChangeTime/1000f);
            currentSpeed = currentSpeed + aTotal * (deltaChangeTime / 1000f);
            Log.e(TAG, "test:"+currentSpeed+", "+aTotal+", "+deltaChangeTime);
            mTvSpeed.setText("speed: " + currentSpeed);

            String str = df.format(acelereate)+", "+df.format(currentSpeed)+", "+df.format(event.values[0]) + "," + df.format(event.values[1]) + "," + df.format(event.values[2])+
                    ", "+currentState;
            //Log.i(TAG, str);

            /*
            switch (currentState){
                case motionless:
                    if(speedBigThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        currentState = downAcelerateBegin;
                    }else if(speedLessThanZero(currentSpeed) && acelerateBigThanNormal(acelereate)){
                        currentState = upAcelerateBegin;
                    }
                    mDate.setText("motionless");
                    break;
                case up:
                    if(speedBigThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        currentState = downAcelerateBegin;
                    }
                    mDate.setText("up");
                    break;
                case down:
                    if(speedLessThanZero(currentSpeed) && acelerateBigThanNormal(acelereate)){
                        currentState = upAcelerateBegin;
                    }
                    mDate.setText("down");
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
                        Log.e(TAG, "downAcelerateBegin not match");
                        currentState = motionless;
                    }
                    break;
                case downAcelerateIng:
                    if(speedBigThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>710){
                            //向下减速太久，置为无效状态
                            statusRemainTime = 0;
                            Log.e(TAG, "downAcelerateIng too long");
                        }
                    }else if(speedBigThanZero(currentSpeed) && !acelerateLessThanNormal(acelereate)){
                        currentState = downDecelerateBegin;
                        statusRemainTime = 0;
                    }else {
                        Log.e(TAG, "downAcelerateIng not match");
                        currentState = motionless;
                    }
                    break;
                case downAcelerateEnd:
                    break;

                case downDecelerateBegin:
                    if(speedBigThanZero(currentSpeed) && acelerateBigThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>170){
                            currentState = downDecelerateIng;
                            statusRemainTime = 0;
                        }
                    }else {
                        //数据与该状态不吻合
                        Log.e(TAG, "downDecelerateBegin not match");
                        currentState = motionless;
                    }
                    break;
                case downDecelerateIng:
                    if(speedBigThanZero(currentSpeed) && acelerateBigThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>710){
                            //向下减速太久，置为无效状态
                            Log.e(TAG, "downDecelerateIng too long");
                            statusRemainTime = 0;
                        }
                    }else if(speedIsZero(currentSpeed) && acelerateIsNormal(acelereate)){
                        currentState = down;
                        currentSpeed = 0;
                        statusRemainTime = 0;
                    }else if(speedLessThanZero(currentSpeed) && acelerateBigThanNormal(acelereate)){
                        currentState = upAcelerateBegin;
                        statusRemainTime = 0;
                        mDate.setText("down");
                    }else {
                        //不符合的数据
                        Log.e(TAG, "downDecelerateIng not match");
                        currentState = motionless;
                    }
                    break;
                case downDecelerateEnd:
                    break;

                case upAcelerateBegin:
                    if(speedLessThanZero(currentSpeed) && acelerateBigThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>190){
                            currentState = upAcelerateIng;
                            statusRemainTime = 0;
                        }
                    }else {
                        //不符合条件
                        Log.e(TAG, "upAcelerateBegin not match");
                        currentState = motionless;
                    }
                    break;
                case upAcelerateIng:
                    if(speedLessThanZero(currentSpeed) && acelerateBigThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>790){
                            //加速时间过长
                            statusRemainTime = 0;
                            Log.e(TAG, "upAcelerateIng too long");
                        }
                    }else if(speedLessThanZero(currentSpeed) && !acelerateBigThanNormal(acelereate)){
                        currentState = upDecelerateBegin;
                        statusRemainTime = 0;
                    }else {
                        //不符合条件
                        Log.e(TAG, "upAcelerateIng not match");
                        currentState = motionless;
                    }
                    break;
                case upDecelerateBegin:
                    if(speedLessThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>190){
                            currentState = upDecelerateIng;
                            statusRemainTime = 0;
                        }
                    }else {
                        //不符合条件
                        Log.e(TAG, "upDecelerateBegin not match");
                        currentState = motionless;
                    }
                    break;
                case upDecelerateIng:
                    if(speedLessThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        statusRemainTime += deltaChangeTime;
                        if(statusRemainTime>790){
                            //减速时间过长,置为无效
                            statusRemainTime = 0;
                            Log.e(TAG, "upDecelerateIng too long");
                        }
                    }else if(speedIsZero(currentSpeed) && acelerateIsNormal(acelereate)){
                        currentState = up;
                        currentSpeed = 0;
                        statusRemainTime = 0;
                    }else if(speedBigThanZero(currentSpeed) && acelerateLessThanNormal(acelereate)){
                        currentState = downAcelerateBegin;
                        statusRemainTime = 0;
                        mDate.setText("up");
                    }else {
                        //不符合条件
                        Log.e(TAG, "upDecelerateIng not match");
                        currentState = motionless;
                    }
                    break;
            }
            */
            lastChangeTime = now;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //Log.e(TAG, "accuracy:"+accuracy);
        }
    };
    private double speedZeroLimit = 0.02;
    private boolean speedIsZero(double speed){
        return speed < speedZeroLimit && speed > -speedZeroLimit;
    }
    private boolean speedLessThanZero(double speed){
        return speed <= -speedZeroLimit;
    }
    private boolean speedBigThanZero(double speed){
        return speed >= speedZeroLimit;
    }
    private boolean acelerateLessThanNormal(double acelerate){
        return acelerate < 9.3;
    }
    private boolean acelerateBigThanNormal(double acelerate){
        return acelerate>10.3;
    }
    private boolean acelerateIsNormal(double acelerate){
        return acelerate > 9.3 && acelerate < 10.3;
    }
}
