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

import com.lessask.dialog.StringPickerDialog;
import com.lessask.global.GlobalInfos;
import com.lessask.model.User;


public class StartupActivity extends MyAppCompatActivity implements View.OnClickListener {
    private Button login;
    private Button register;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private String TAG = StartupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        }


        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);

        SharedPreferences baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);

        //判断是否是第一次启动
        if(!baseInfo.getBoolean("initDb", false)){
            initDb(baseInfo);
        }
        int userid = baseInfo.getInt("userid", -1);
        Log.e(TAG, "userid:"+userid);
        if(userid!=-1){
            globalInfos.setUserId(userid);
            String headImg = baseInfo.getString("headImg", "");
            String nickname = baseInfo.getString("nickname", "");
            String mail = baseInfo.getString("mail", "");
            globalInfos.setUser(userid, new User(userid, mail, nickname, -1, "", headImg));

            //获取最新的用户数据
            //to do

            Intent intent = new Intent(StartupActivity.this, MainActivity.class);
            //清除 activity栈中的内容
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void initDb(SharedPreferences baseInfo){
        SharedPreferences.Editor editor = baseInfo.edit();
        //初始化数据库
        SQLiteDatabase db = globalInfos.getDb(getApplicationContext());
        //获取基础信息
        //个人信息
        //聊天列表
        db.execSQL("create table t_chatgroup(chatgroup_id varchar(19) primary key,`name` varchar(200) not null)");
        //通讯录
        db.execSQL("create table t_contact(userid int not null primary key,nickname varchar(20),headimg varchar(150))");
        //消息列表
        db.execSQL("create table t_chatrecord(`id` INTEGER primary key,`chatgroup_id` text not null,`status` tinyint not null,`time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,`userid` integer not null,`type` integer not null,`content` text not null,seq interger not null,view_type interger not null)");

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
