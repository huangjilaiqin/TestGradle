package com.lessask.test;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lessask.R;

public class StorageActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        findViewById(R.id.sd_card).setOnClickListener(this);
        findViewById(R.id.ex_file_dir).setOnClickListener(this);
        findViewById(R.id.app_ex_cache).setOnClickListener(this);
        findViewById(R.id.app_cache).setOnClickListener(this);
        findViewById(R.id.ex_file_dir_custome).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        String path;
        switch (v.getId()){
            case R.id.sd_card:
                path = Environment.getExternalStorageDirectory().getAbsolutePath();
                Toast.makeText(this, "sdCardPath:" + path, Toast.LENGTH_SHORT).show();
                break;
            case R.id.app_cache:
                path = getBaseContext().getCacheDir().getAbsolutePath();
                Toast.makeText(this, "getCacheDir:" + path, Toast.LENGTH_SHORT).show();
                break;
            case R.id.app_ex_cache:
                path = getBaseContext().getExternalCacheDir().getAbsolutePath();
                Toast.makeText(this, "getExternalCacheDir:" + path, Toast.LENGTH_SHORT).show();
                break;
            case R.id.ex_file_dir:
                path = getBaseContext().getExternalFilesDir("").getAbsolutePath();
                Toast.makeText(this, "getExternalFilesDir:" + path, Toast.LENGTH_SHORT).show();
                break;

            case R.id.ex_file_dir_custome:
                path = getBaseContext().getExternalFilesDir("video").getAbsolutePath();
                Toast.makeText(this, "getExternalFilesDir_video:" + path, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
