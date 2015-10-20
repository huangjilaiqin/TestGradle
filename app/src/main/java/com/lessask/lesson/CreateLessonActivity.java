package com.lessask.lesson;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lessask.R;

public class CreateLessonActivity extends Activity implements View.OnClickListener{

    private ImageView mBack;
    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.save:
                break;
        }
    }
}
