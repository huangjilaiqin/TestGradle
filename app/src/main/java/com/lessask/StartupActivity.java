package com.lessask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.chat.ChatResponseListener;
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
    private final int VERIFY_TOKEN_ERROR = 1;
    private final int VERIFY_TOKEN_SUCCESS = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:"+msg.what);
            switch (msg.what){
                case VERIFY_TOKEN_ERROR:
                    VerifyToken response = (VerifyToken) msg.obj;
                    Log.e(TAG, "verifytoken errno:"+response.getErrno()+", error:"+response.getError());
                    login.setVisibility(View.VISIBLE);
                    register.setVisibility(View.VISIBLE);
                    break;
                case VERIFY_TOKEN_SUCCESS:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        }
        baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);

        chat.appendVerifyTokenListener(new ChatResponseListener() {
            @Override
            public void response(String obj) {
                Log.e(TAG, "verify");
                VerifyToken verifyToken = gson.fromJson(obj,VerifyToken.class);
                if (verifyToken.getErrno() != 0 || verifyToken.getError()!=null && verifyToken.getError().length() != 0) {
                    //token 无效
                    Message msg = new Message();
                    msg.what =VERIFY_TOKEN_ERROR ;
                    msg.obj = verifyToken;
                    handler.sendMessage(msg);
                    return;
                }else {
                    //token有效
                    int userid = verifyToken.getUserid();
                    globalInfos.setUserId(userid);
                    globalInfos.setToken(verifyToken.getToken());
                    //移除这个监听器
                    chat.removeVerifyTokenListener(this);
                    //Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                    Intent intent = new Intent(StartupActivity.this, NewMainActivity.class);
                    //清除 activity栈中的内容
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        /*
        chat.setVerifyTokenListener(new Chat.VerifyTokenListener(){
            @Override
            public void verify(String data) {
                Log.e(TAG, "verify");
                VerifyToken verifyToken = gson.fromJson(data,VerifyToken.class);
                if (verifyToken.getErrno() != 0 || verifyToken.getError()!=null && verifyToken.getError().length() != 0) {
                    //token 无效
                    Message msg = new Message();
                    msg.what =VERIFY_TOKEN_ERROR ;
                    msg.obj = verifyToken;
                    handler.sendMessage(msg);
                    return;
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
        */


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
        if(token.length()!=0 && userid!=-1) {
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
        db.execSQL("create table t_chatgroup(chatgroup_id text primary key,`name` text not null,`status` int not null default 0,`unread_count` integer not null default 0)");
        //通讯录
        db.execSQL("create table t_contact(userid int not null primary key,nickname varchar(20),headimg varchar(150))");
        //消息列表
        db.execSQL("create table t_chatrecord(`id` INTEGER primary key,`seq` integer default 0,userid interger not null,`chatgroup_id` text not null,`type` integer not null,content text not null,`time` TIMESTAMP NOT NULL,`status` tinyint not null)");

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
