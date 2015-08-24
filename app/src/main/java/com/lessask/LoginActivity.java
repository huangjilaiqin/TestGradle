package com.lessask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.chat.GlobalInfos;
import com.lessask.model.Login;
import com.lessask.model.LoginResponse;
import com.lessask.model.User;


public class LoginActivity extends Activity {
    private static final String TAG= LoginActivity.class.getName();
    private Chat chat = Chat.getInstance();
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
                    Log.d(TAG,"login success userid:"+globalInfos.getUserid());
                    //去掉转圈圈
                    loginDialog.cancel();
                    //跳转到首页
                    //Intent intent = new Intent(LoginActivity.this, TestActivity.class);
                    //Intent intent = new Intent(LoginActivity.this, FriendsActivity.class);
                    Intent intent = new Intent(LoginActivity.this, FragmentTestActivity.class);

                    startActivity(intent);
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
                    globalInfos.setUserid(userid);
                    //to do 服务器端返回 昵称,客户端发生 状态(在线)
                    globalInfos.setUser(userid, new User(userid, username, null, 1, null));
                    handler.sendEmptyMessage(HANDLER_LOGING_SUCCESS);
                }
            }
        });

        Log.e(TAG, "onCreate");
        Button bLogin = (Button)findViewById(R.id.bLogin);
        Button bRegister = (Button)findViewById(R.id.bRegister);
        final EditText tUsername = (EditText)findViewById(R.id.tUsername);
        final EditText tPassword = (EditText)findViewById(R.id.tPassword);

        Intent intent = getIntent();
        username = intent.getStringExtra("mail");
        passwd = intent.getStringExtra("passwd");
        if (username != null && passwd != null) {
            tUsername.setText(username);
            tPassword.setText(passwd);
        }

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = tUsername.getText().toString().trim();
                passwd = tPassword.getText().toString().trim();
                if(username.length()==0 || passwd.length()==0){
                    /*
                    username = "1577594730@qq.com";
                    passwd = "19910725";
                    //*/
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
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "register");
                Log.i(TAG, tUsername.getText().toString().trim());
                Log.i(TAG, tPassword.getText().toString().trim());
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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

}