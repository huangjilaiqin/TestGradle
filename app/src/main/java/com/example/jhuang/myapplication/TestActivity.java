package com.example.jhuang.myapplication;

import android.app.Activity;
import android.content.Intent;
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
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        Log.d(TAG, ""+v.getId());
        switch (v.getId()){
            case R.id.chatActivity:
                Intent intent = new Intent(TestActivity.this, ChatActivity.class);
                startActivity(intent);
                break;
            default:
                Log.e(TAG, "click donse't match");
                break;
        }
    }
}
