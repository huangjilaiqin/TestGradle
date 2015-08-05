package com.example.jhuang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;


public class TestActivity extends Activity implements View.OnClickListener {
    private static final String TAG="TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Log.d(TAG, "onCreate");
        Button chat = (Button)findViewById(R.id.chatActivity);
        chat.setOnClickListener(this);
        Button storeFile = (Button)findViewById(R.id.storeFileActivity);
        storeFile.setOnClickListener(this);
        Button bSharePre= (Button)findViewById(R.id.sharePreferencesActivity);
        bSharePre.setOnClickListener(this);

        /*
        Button chat = (Button)findViewById(R.id.chatActivity);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        Log.d(TAG, ""+v.getId());
        Intent intent = null;
        switch (v.getId()){
            case R.id.chatActivity:
                intent = new Intent(TestActivity.this, ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.storeFileActivity:
                intent = new Intent(TestActivity.this, StoreFileActivity.class);
                startActivity(intent);
                break;
            case R.id.sharePreferencesActivity:
                intent = new Intent(TestActivity.this, SharePreferencesActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e(TAG, "click donse't match");
                break;
        }
    }
}
