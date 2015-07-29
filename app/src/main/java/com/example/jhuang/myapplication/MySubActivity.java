package com.example.jhuang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MySubActivity extends Activity {
    private static final String TAG="MySubActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sub);

        final Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String passwd = intent.getStringExtra("passwd");
        //double db = intent.getDoubleExtra("db");
        Log.i(TAG, username);
        Log.i(TAG, passwd);
        Log.i(TAG, "哈哈哈");

        final EditText et = (EditText)findViewById(R.id.editText);
        final TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText("username: "+username+", passwd: "+passwd);

        Button back = (Button)findViewById(R.id.confirm);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String backText = et.getText().toString().trim();
                Bundle bundle = new Bundle();
                bundle.putString("backText", backText);
                bundle.putDouble("backDb", 34.56);
                intent.putExtras(bundle);

                MySubActivity.this.setResult(RESULT_OK, intent);
                MySubActivity.this.finish();
            }
        });
    }
}
