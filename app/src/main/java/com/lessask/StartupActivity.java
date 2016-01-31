package com.lessask;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class StartupActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.login:
                intent = new Intent(StartupActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.register:
                intent = new Intent(StartupActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
