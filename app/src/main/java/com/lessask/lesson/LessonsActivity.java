package com.lessask.lesson;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lessask.R;

public class LessonsActivity extends Activity implements View.OnClickListener{

    private int CREATE_LESSON = 1;
    private ImageView mBack;
    private Button mCustomize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        mBack = (ImageView)findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mCustomize = (Button)findViewById(R.id.customize);
        mCustomize.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.customize:
                Intent intent = new Intent(LessonsActivity.this, CreateLessonActivity.class);
                startActivityForResult(intent, CREATE_LESSON);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
