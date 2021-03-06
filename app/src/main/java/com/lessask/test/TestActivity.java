package com.lessask.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;

import com.lessask.R;
import com.lessask.chat.ChatActivity;


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
        Button bChangeText = (Button)findViewById(R.id.handlerActivity);
        bChangeText.setOnClickListener(this);
        Button bService = (Button) findViewById(R.id.firstServiceActivity);
        bService.setOnClickListener(this);
        Button bSocketIO = (Button)findViewById(R.id.socketIOActivity);
        bSocketIO.setOnClickListener(this);
        Button bSubThread = (Button)findViewById(R.id.subThreadActivity);
        bSubThread.setOnClickListener(this);
        Button bGetImg = (Button)findViewById(R.id.headImageActivity);
        bGetImg.setOnClickListener(this);
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
            case R.id.firstServiceActivity:
                intent = new Intent(TestActivity.this, FirstServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.socketIOActivity:
                intent = new Intent(TestActivity.this, SocketIOActivity.class);
                startActivity(intent);
                break;
            case R.id.subThreadActivity:
                intent = new Intent(TestActivity.this, SubThreadActivity.class);
                startActivity(intent);
                break;
            case R.id.headImageActivity:
                intent = new Intent(TestActivity.this, HeadImgActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e(TAG, "click donse't match");
                break;
        }
    }
}
