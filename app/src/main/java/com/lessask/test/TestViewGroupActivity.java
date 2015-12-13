package com.lessask.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lessask.R;

public class TestViewGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view_group);
        final Button add = (Button) findViewById(R.id.add);
        final TextView sub = (TextView)findViewById(R.id.sub_view);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = new TextView(TestViewGroupActivity.this);
                t.setText("动态错误");
                ViewGroup viewGroup = (ViewGroup)add.getParent();
                viewGroup.removeAllViews();
                viewGroup.addView(sub);
                //viewGroup.addView(findViewById(R.id.sub_view));
            }
        });

    }
}
