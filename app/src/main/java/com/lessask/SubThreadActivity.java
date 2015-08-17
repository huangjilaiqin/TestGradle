package com.lessask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.Thread.UncaughtExceptionHandler;

public class SubThreadActivity extends Activity {
    final private static String TAG = "SubThreadActivity ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_thread);
        Button bStart = (Button)findViewById(R.id.start);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
        Button bCurrent = (Button)findViewById(R.id.current);
        bCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new NullPointerException();
            }
        });

        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventThread.exec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);

                            Log.d(TAG, "sub thread");
                            TextView a = null;
                            a.setText("fffAacc");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        throw new RuntimeException("Transport not open");
                    }
                });
            }
        });
    }
    class DefaultExceptionHandler implements UncaughtExceptionHandler {
        public DefaultExceptionHandler() {
        }
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if(thread.getId()==1){
                Log.e(TAG, "UI thread:"+ ex.getMessage());
            }else{
                Log.e(TAG, "sub thread:"+ ex.getMessage());
            }
           handleException();
        }
        private void sendCrashReport(Throwable ex) {
           StringBuffer exceptionStr = new StringBuffer();
           exceptionStr.append(ex.getMessage());
           StackTraceElement[] elements = ex.getStackTrace();
           for (int i = 0; i < elements.length; i++) {
               exceptionStr.append(elements[i].toString());
           }
        }
        private void handleException() {
            System.out.println("handleException");
        }
    }
}

