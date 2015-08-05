package com.example.jhuang.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SharePreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_preferences);

        final EditText etUserName = (EditText) findViewById(R.id.username);
        final EditText etPasswd = (EditText) findViewById(R.id.passwd);
        final Button bStore = (Button) findViewById(R.id.store);

        SharedPreferences pres = getSharedPreferences("data", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pres.edit();

        String username = pres.getString("username", "");
        if(username.length()>0){
            etUserName.setText(username);
        }
        int passwd = pres.getInt("passwd", 0);
        if(passwd != 0){
            etPasswd.setText(""+passwd);
        }

        bStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUserName.getText().toString().trim();
                String passwd = etPasswd.getText().toString().trim();
                Toast.makeText(getApplicationContext(), ""+username+","+passwd, Toast.LENGTH_SHORT).show();
                if(username.length()>0){
                    editor.putString("username", username);
                }
                if(passwd.length()>0){
                    editor.putInt("passwd", Integer.parseInt(passwd));
                }
                editor.commit();
            }
        });
    }
}
