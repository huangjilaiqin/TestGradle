package com.example.jhuang.myapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class FirstServiceActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_service);
        Button bStart = (Button) findViewById(R.id.start_service);
        Button bStop = (Button) findViewById(R.id.stop_service);
        bStart.setOnClickListener(this);
        bStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.start_service:
                intent = new Intent(this, FirstService.class);
                startService(intent);
                break;
            case R.id.stop_service:
                intent = new Intent(this, FirstService.class);
                stopService(intent);
                break;
            default:
                break;
        }
    }
}
