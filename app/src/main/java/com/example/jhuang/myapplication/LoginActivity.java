package com.example.jhuang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {
    private static final String TAG="ActivityDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Log.e(TAG, "onCreate");
        Button bLogin = (Button)findViewById(R.id.bLogin);
        Button bRegister = (Button)findViewById(R.id.bRegister);
        final EditText tUsername = (EditText)findViewById(R.id.tUsername);
        final EditText tPassword = (EditText)findViewById(R.id.tPassword);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "login");
                String username = tUsername.getText().toString().trim();
                String passwd = tPassword.getText().toString().trim();
                Log.i(TAG, username);
                Log.i(TAG, passwd);
                Intent intent = new Intent(LoginActivity.this, TestActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("passwd", passwd);
                startActivity(intent);
            }
        });
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "register");
                Log.i(TAG, tUsername.getText().toString().trim());
                Log.i(TAG, tPassword.getText().toString().trim());
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onStart(){
        super.onStart();
        Log.e(TAG, "onStart");
    }
    protected void onRestart(){
        super.onRestart();
        Log.e(TAG, "onRestart");
    }
    protected void onResume(){
        super.onResume();
        Log.e(TAG, "onResume");
    }
    protected void onPause(){
        super.onPause();
        Log.e(TAG, "onPause");
    }
    protected void onStop(){
        super.onStop();
        Log.e(TAG, "onStop");
    }
    protected void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Bundle bd = data.getExtras();
            Log.i(TAG, bd.getString("backText"));
            Log.i(TAG, String.valueOf(bd.getDouble("backDb")));
            Log.i(TAG, data.getStringExtra("username"));
            Log.i(TAG, data.getStringExtra("passwd"));
        }
    }
}
