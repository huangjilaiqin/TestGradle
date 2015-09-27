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
    private double[] gValues = new  double[3];
    private double[] directionValues = new  double[3];
    private enum MoveState {
        motionless,
        up,
        downAcelerateBegin,downAcelerateIng, downAcelerateEnd,
        downDecelerateBegin, downDecelerateIng, downDecelerateEnd,
        down,
        upAcelerateBegin, upAcelerateIng, upAcelerateEnd,
        upDecelerateBegin, upDecelerateIng, upDecelerateEnd
    };
    //设置为静止状态
    private MoveState currentState = MoveState.motionless;
    private int detectMotinless;
    private double lastGravity = 9.8;
    private long lastCheck;
    private long statusCheckTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squats);
        mDate = (TextView)findViewById(R.id.data);
        mDate1 = (TextView)findViewById(R.id.data1);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //根据g值判断手机的静止方向
        Sensor gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(gSensor==null){
            Toast.makeText(this, "重力传感器", Toast.LENGTH_SHORT).show();
        }
        mSensorManager.registerListener(gListener, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
        lastCheck = System.currentTimeMillis();
        detectMotinless = 0;
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
            double re = Math.sqrt(Math.pow(gValues[0], 2)+Math.pow(gValues[1], 2)+Math.pow(gValues[2], 2));
            //向下运动的变化过程(向上为逆过程) 9.8 -> 6 -> 14 -> 9.8
            long now = System.currentTimeMillis();
            long deltime = now-statusCheckTime;
            Log.i(TAG, now+", "+statusCheckTime+", "+deltime+", "+currentState);
            DecimalFormat df = new DecimalFormat("0.00");
            String str = df.format(re)+", "+df.format(event.values[0]) + "," + df.format(event.values[1]) + "," + df.format(event.values[2]);
            Log.i(TAG, str);

            boolean statusChange = false;

            // [9.2,10.4] 为静止状态
            if(re<=9.3 ){
                if(currentState==MoveState.up || currentState==MoveState.motionless) {
                    //开始向下加速
                    currentState = MoveState.downAcelerateBegin;
                    statusCheckTime = now;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==MoveState.downAcelerateBegin && now-statusCheckTime>190){
                    //向下加速超过额定时间,判为无效动作
                    if(currentState == MoveState.downAcelerateIng && now-statusCheckTime>900){
                        Log.e(TAG, "向下加速超时");
                        return;
                    }
                    //向下加速达到额定时间
                    currentState = MoveState.downAcelerateIng;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }
                /* 最高点前和最高点后加速度都小于9.8无法区分
                else if(currentState==MoveState.upDecelerateIng){
                    //一次有效深蹲计数,因为没检测到最低点暂停的数据又直接向上加速了
                    currentState = MoveState.downAcelerateBegin;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }
                */


                if(currentState==MoveState.upAcelerateEnd || currentState==MoveState.upAcelerateIng){
                    //开始向上减速
                    currentState = MoveState.upDecelerateBegin;
                    statusCheckTime = now;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==MoveState.upDecelerateBegin && now-statusCheckTime>190){
                    //向上减速超过额定时间判为无效动作
                    if(currentState==MoveState.upDecelerateIng && now-statusCheckTime>900){
                        Log.e(TAG, "向上减速超时");
                        return;
                    }
                    //向上减速达到额定时间
                    currentState = MoveState.upDecelerateIng;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }

            } else if(re>10.3){
                if(currentState==MoveState.downAcelerateEnd || currentState==MoveState.downAcelerateIng){
                    //开始向下减速
                    currentState = MoveState.downDecelerateBegin;
                    statusCheckTime = now;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==MoveState.downDecelerateBegin && now-statusCheckTime>190){
                    //向下减速超过额定时间,判为无效动作
                    if(currentState == MoveState.downDecelerateIng && now-statusCheckTime>900){
                        Log.e(TAG, "向下减速超时");
                    }
                    //向下减速达到额定时间
                    currentState = MoveState.downDecelerateIng;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }
                /* 最低点前和最低点后加速度都大于9.8无法区分
                else if(currentState==MoveState.downDecelerateIng){
                    //一次有效深蹲计数,因为没检测到最低点暂停的数据又直接向上加速了
                    currentState = MoveState.upAcelerateBegin;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }
                */


                if(currentState==MoveState.down || currentState==MoveState.motionless){
                    //开始向上加速
                    currentState = MoveState.upAcelerateBegin;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState==MoveState.upAcelerateBegin && now-statusCheckTime>190){
                    //向上加速超过额定时间,判为无效动作
                    if(currentState == MoveState.upAcelerateIng && now-statusCheckTime>900){
                        Log.e(TAG, "向上加速超时");
                    }
                    //向上加速达到额定时间
                    currentState = MoveState.upAcelerateIng;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }

            } else {
                if(currentState==MoveState.downAcelerateIng){
                    //向下加速结束
                    currentState = MoveState.downAcelerateEnd;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState== MoveState.upAcelerateIng){
                    //向上加速结束
                    currentState = MoveState.upAcelerateEnd;
                    statusChange = true;
                    Log.e(TAG, ""+currentState);
                }else if(currentState== MoveState.downDecelerateIng){
                    //向下减速结束
                    currentState = MoveState.downDecelerateEnd;
                    Log.e(TAG, ""+currentState);
                    currentState = MoveState.down;
                    Log.e(TAG, ""+currentState);
                    statusChange = true;
                }else if(currentState==MoveState.upDecelerateIng){
                    //向上减速结束
                    currentState=MoveState.upDecelerateEnd;
                    Log.e(TAG, ""+currentState);
                    currentState = MoveState.up;
                    Log.e(TAG, ""+currentState);
                    statusChange = true;
                }else {
                    detectMotinless++;
                    if(detectMotinless%4==0) {
                        //currentState = MoveState.motionless;
                        detectMotinless=0;
                    }
                }
            }
            if(statusChange==false && now-statusCheckTime>2000){
                currentState = MoveState.motionless;
            }
            if(currentState==MoveState.down || currentState==MoveState.up) {
                mDate.setText("" + currentState);
                //currentState = MoveState.motionless;
            }
            mDate.setText(""+currentState);
            lastGravity = re;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.e(TAG, "accuracy:"+accuracy);
        }
    };
    private SensorEventListener directionListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(Math.abs(directionValues[0]-event.values[0])>1 || Math.abs(directionValues[1]-event.values[1])>1 || Math.abs(directionValues[2]-event.values[2])>1){
                directionValues[0] = event.values[0];
                directionValues[1] = event.values[1];
                directionValues[2] = event.values[2];
                DecimalFormat df = new DecimalFormat("0.00");
                String str = df.format(event.values[0]) + "," + df.format(event.values[1]) + "," + df.format(event.values[2]);
                Log.e(TAG, str);
                mDate1.setText(str);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.e(TAG, "accuracy:"+accuracy);
        }
    };
}
