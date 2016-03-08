package com.lessask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Login;
import com.lessask.model.User;
import com.lessask.model.VerifyToken;


public class StartupActivity extends MyAppCompatActivity implements View.OnClickListener {
    private Button login;
    private Button register;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private String TAG = StartupActivity.class.getSimpleName();
    private  Chat chat = Chat.getInstance(getBaseContext());
    private SharedPreferences baseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        }
        baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);

        chat.setVerifyTokenListener(new Chat.VerifyTokenListener(){
            @Override
            public void verify(String data) {
                VerifyToken verifyToken = gson.fromJson(data,VerifyToken.class);
                if (verifyToken.getErrno() != 0 || verifyToken.getError()!=null && verifyToken.getError().length() != 0) {
                    //token 无效
                    login.setVisibility(View.VISIBLE);
                    register.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    //token有效
                    int userid = verifyToken.getUserid();
                    globalInfos.setUserId(userid);
                    globalInfos.setToken(verifyToken.getToken());

                    Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                    //清除 activity栈中的内容
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });


        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        login.setVisibility(View.INVISIBLE);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        register.setVisibility(View.INVISIBLE);

        //判断是否是第一次启动
        if(!baseInfo.getBoolean("initDb", false)){
            initDb(baseInfo);
        }
        int userid = baseInfo.getInt("userid", -1);
        String token = baseInfo.getString("token", "");
        Log.e(TAG, "userid:"+userid+", token:"+token);
        if(token.length()!=0) {
            //验证token有效性
            chat.emit("verifyToken",gson.toJson(new VerifyToken(userid,token)));
        }else {
            login.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
        }
    }

    private void initDb(SharedPreferences baseInfo){
        SharedPreferences.Editor editor = baseInfo.edit();
        //初始化数据库
        SQLiteDatabase db = globalInfos.getDb(getApplicationContext());
        //获取基础信息
        //个人信息
        db.execSQL("create table t_user(userid int primary key,`nickname` text not null,`headimg` text not null)");
        //聊天列表
        db.execSQL("create table t_chatgroup(chatgroup_id varchar(19) primary key,`name` varchar(200) not null)");
        //通讯录
        db.execSQL("create table t_contact(userid int not null primary key,nickname varchar(20),headimg varchar(150))");
        //消息列表
        db.execSQL("create table t_chatrecord(`id` INTEGER primary key,`chatgroup_id` text not null,`status` tinyint not null,`time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,`userid` integer not null,`type` integer not null,`content` text not null,seq interger not null)");

        Log.e(TAG, "create db");

        editor.putBoolean("initDb", true);
        editor.commit();
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
