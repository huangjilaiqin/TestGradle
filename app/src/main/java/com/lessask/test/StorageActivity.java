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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sd_card:

                String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                Toast.makeText(this, "sdCardPath:" + sdCardPath, Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
