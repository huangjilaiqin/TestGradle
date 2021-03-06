package com.lessask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Login;
import com.lessask.model.LoginResponse;
import com.lessask.model.User;

import java.io.File;


public class LoginActivity extends MyAppCompatActivity{
    private static final String TAG= LoginActivity.class.getName();
    private Chat chat = Chat.getInstance(getBaseContext());
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private ProgressDialog loginDialog;

    private int userid;
    private String username;
    private String passwd;

    private final int HANDLER_LOGING_ERROR = 0;
    private final int HANDLER_LOGING_SUCCESS = 1;

    private final int LOGIN_MAIL = 1;
    private final int LOGIN_WEIXIN = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:"+msg.what);
            switch (msg.what){
                case HANDLER_LOGING_ERROR:
                    LoginResponse loginResponse = (LoginResponse)msg.obj;
                    Log.e(TAG, "login errno:"+loginResponse.getErrno()+", error:"+loginResponse.getError());
                    loginDialog.cancel();
                    Toast.makeText(LoginActivity.this, "login errno:"+loginResponse.getErrno()+", error:"+loginResponse.getError(), Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_LOGING_SUCCESS:
                    Log.d(TAG, "login success userid:" + globalInfos.getUserId());
                    //去掉转圈圈
                    loginDialog.cancel();
                    //跳转到首页

                    Intent intent = new Intent(LoginActivity.this, NewMainActivity.class);
                    //清除 activity栈中的内容
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

        File headImgDir = getApplicationContext().getExternalFilesDir("headImg");
        globalInfos.setHeadImgDir(headImgDir);

        globalInfos.setHeadImgHost("http://123.59.40.113/myimg/");

        chat.setLoginListener(new Chat.LoginListener() {
            @Override
            public void login(String data) {
                LoginResponse loginResponse = gson.fromJson(data, LoginResponse.class);
                Log.d(TAG, ""+loginResponse.getErrno()+", "+loginResponse.getError());
                if (loginResponse.getErrno() != 0 || loginResponse.getError()!=null && loginResponse.getError().length() != 0) {
                    Message msg = new Message();
                    msg.what = HANDLER_LOGING_ERROR;
                    msg.obj = loginResponse;
                    handler.sendMessage(msg);
                    return;
                }else {
                    userid =loginResponse.getUserid();
                    globalInfos.setUserId(userid);
                    String token = loginResponse.getToken();
                    Log.e(TAG, "login userid:"+userid+", token:"+token);

                    SharedPreferences baseInfo = getSharedPreferences("BaseInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = baseInfo.edit();
                    editor.putString("token", token);
                    editor.putInt("userid", userid);
                    editor.commit();

                    globalInfos.setToken(token);
                    //to do 服务器端返回 昵称,客户端发生 状态(在线)
                    handler.sendEmptyMessage(HANDLER_LOGING_SUCCESS);
                }
            }
        });

        Log.e(TAG, "onCreate");
        Button bLogin = (Button)findViewById(R.id.login);
        final TextInputLayout tUsername = (TextInputLayout)findViewById(R.id.username);
        final TextInputLayout tPassword = (TextInputLayout)findViewById(R.id.passwd);

        Intent intent = getIntent();
        username = intent.getStringExtra("mail");
        passwd = intent.getStringExtra("passwd");
        if (username != null && passwd != null) {
            tUsername.getEditText().setText(username);
            tPassword.getEditText().setText(passwd);
        }


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = tUsername.getEditText().getText().toString().trim();
                passwd = tPassword.getEditText().getText().toString().trim();
                if(username.length()==0 || passwd.length()==0){
                    //*
                    username = "1577594730@qq.com";
                    passwd = "8888";
                    /*/
                    username = "136437945@qq.com";
                    passwd = "19910725";
                    //*/
                }
                chat.emit("login", gson.toJson(new Login(LOGIN_MAIL , username, passwd)));
                //chat.emit("message", "message from android");
                //转圈圈
                loginDialog = new ProgressDialog(LoginActivity.this, ProgressDialog.STYLE_SPINNER);
                loginDialog.setTitle("登录中...");
                //loginDialog.setCancelable(false);
                loginDialog.show();

                /*
                Intent intent = new Intent(LoginActivity.this, TestActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("passwd", passwd);
                startActivity(intent);
                */
            }
        });
    }

    protected void onStart(){
        super.onStart();
        Log.e(TAG, "onStart");
    }
    protected void onRestart(){
        super.onRestart();
        Log.e(TAG, "onRestart");
    }
    protected void onResume(){
        super.onResume();
        Log.e(TAG, "onResume");
    }
    protected void onPause(){
        super.onPause();
        Log.e(TAG, "onPause");
    }
    protected void onStop(){
        super.onStop();
        Log.e(TAG, "onStop");
    }
    protected void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Bundle bd = data.getExtras();
            Log.i(TAG, bd.getString("backText"));
            Log.i(TAG, String.valueOf(bd.getDouble("backDb")));
            Log.i(TAG, data.getStringExtra("username"));
            Log.i(TAG, data.getStringExtra("passwd"));
        }
    }
    class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
        public DefaultExceptionHandler() {
        }
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if(thread.getId()==1){
                Log.e(TAG, "UI thread:"+ ex.getMessage());
                Log.e(TAG, "UI thread:" + ex.toString());
            }else{
                Log.e(TAG, "sub thread:"+ ex.getMessage());
            }
            ex.printStackTrace();
           handleException(ex);
        }
        private void sendCrashReport(Throwable ex) {
           StringBuffer exceptionStr = new StringBuffer();
           exceptionStr.append(ex.getMessage());
           StackTraceElement[] elements = ex.getStackTrace();
           for (int i = 0; i < elements.length; i++) {
               exceptionStr.append(elements[i].toString());
           }
        }
        private void handleException(Throwable ex) {
        }
    }
}
