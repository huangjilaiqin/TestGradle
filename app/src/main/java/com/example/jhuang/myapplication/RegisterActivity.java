package com.example.jhuang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class RegisterActivity extends Activity {

    private ImageView ivHeadImg;
    private EditText etMail;
    private EditText etPasswd;
    private EditText etConfirmPasswd;
    private Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ivHeadImg = (ImageView)findViewById(R.id.head_img);
        etMail = (EditText)findViewById(R.id.mail);
        etPasswd = (EditText)findViewById(R.id.passwd);
        etConfirmPasswd = (EditText)findViewById(R.id.confirm_passwd);
        bRegister = (Button)findViewById(R.id.register);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = etMail.getText().toString().trim();
                String passwd = etPasswd.getText().toString().trim();
                String confirmPasswd = etConfirmPasswd.getText().toString().trim();

                if(mail==null || passwd==null || confirmPasswd==null){
                    Toast.makeText(getApplicationContext(), "请填写完整信息", Toast.LENGTH_SHORT);
                    return;
                }
                if(!passwd.equals(confirmPasswd)){
                    Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT);
                    etPasswd.setText("");
                    etConfirmPasswd.setText("");
                    return;
                }
                //发起注册请求

            }
        });
        ivHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
