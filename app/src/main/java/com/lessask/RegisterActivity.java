package com.lessask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.RegisterResponse;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;

import java.util.HashMap;


public class RegisterActivity extends Activity {

    private Chat chat = Chat.getInstance();
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private EditText etMail;
    private String mail;
    private EditText etNickname;
    private String nickname;
    private EditText etPasswd;
    private String passwd;
    private EditText etConfirmPasswd;
    private Button bRegister;
    private static final String TAG = RegisterActivity.class.getName();

    private ProgressDialog registerDialog;

    private final int HANDLER_REGISTER_ERROR = 0;
    private final int HANDLER_REGISTER_SUCCESS = 1;
    private final int HANDLER_REGISTER_START = 2;
    private final int HANDLER_REGISTER_DONE = 3;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "register handler:" + msg.what);
            switch (msg.what){
                case HANDLER_REGISTER_START:
                    registerDialog = new ProgressDialog(RegisterActivity.this, ProgressDialog.STYLE_SPINNER);
                    registerDialog.setTitle("注册中...");
                    registerDialog.show();
                    break;
                case HANDLER_REGISTER_DONE:
                    int statusCode = msg.arg1;
                    RegisterResponse registerResponse = (RegisterResponse)msg.obj;
                    if(statusCode==200){
                        globalInfos.setUserId(registerResponse.getUserid());
                        registerDialog.cancel();
                        Toast.makeText(RegisterActivity.this, "注册成功, 跳转登录...", Toast.LENGTH_LONG).show();
                        //跳转到首页
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("mail", mail);
                        intent.putExtra("passwd", passwd);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                        finish();

                    }else {
                        Log.e(TAG, "register errno:"+registerResponse.getErrno()+", error:"+registerResponse.getError());
                        registerDialog.cancel();
                        Toast.makeText(RegisterActivity.this, "error:"+registerResponse.getError(), Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private HashMap<String, String> requestArgs = new HashMap<>();
    private PostSingleEvent postSingleEvent = new PostSingleEvent() {

        @Override
        public void onStart() {
            Message msg = new Message();
            msg.what = HANDLER_REGISTER_START;
            handler.sendMessage(msg);
        }
        @Override
        public void onDone(PostResponse response) {
            Message msg = new Message();
            msg.what = HANDLER_REGISTER_DONE;
            msg.arg1 = response.getCode();
            msg.obj = gson.fromJson(response.getBody(), RegisterResponse.class);
            handler.sendMessage(msg);
        }

        @Override
        public HashMap<String, String> getHeaders() {
            return null;
        }

        @Override
        public HashMap<String, String> getFiles() {
            return null;
        }

        @Override
        public HashMap<String, String> getImages() {
            return null;
        }

        @Override
        public void onError(String err) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etMail = (EditText)findViewById(R.id.mail);
        etNickname = (EditText)findViewById(R.id.nickname);
        etPasswd = (EditText)findViewById(R.id.passwd);
        etConfirmPasswd = (EditText)findViewById(R.id.confirm_passwd);
        bRegister = (Button)findViewById(R.id.register);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = etMail.getText().toString().trim();
                nickname = etNickname.getText().toString().trim();
                passwd = etPasswd.getText().toString().trim();
                String confirmPasswd = etConfirmPasswd.getText().toString().trim();

                if(mail==null || passwd==null || confirmPasswd==null || nickname==null){
                    Toast.makeText(getApplicationContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!passwd.equals(confirmPasswd)){
                    Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
                    etPasswd.setText("");
                    etConfirmPasswd.setText("");
                    return;
                }
                requestArgs.put("mail", mail);
                requestArgs.put("nickname", nickname);
                requestArgs.put("passwd", passwd);
                PostSingle postSingle = new PostSingle(config.getRegisterUrl(), postSingleEvent);
                //postSingle.setHeaders(requestArgs);
                postSingle.start();
            }
        });

        chat.setRegisterListener(new Chat.RegisterListener() {
            @Override
            public void register(String data) {
                Log.e(TAG, "register:" + data);
                RegisterResponse registerResponse = gson.fromJson(data, RegisterResponse.class);
                Log.d(TAG, "" + registerResponse.getErrno() + ", " + registerResponse.getError());
                if (registerResponse.getErrno() != 0 || registerResponse.getError() != null && registerResponse.getError().length() != 0) {
                    Message msg = new Message();
                    msg.what = HANDLER_REGISTER_ERROR;
                    msg.obj = registerResponse;
                    handler.sendMessage(msg);
                    return;
                } else {
                    globalInfos.setUserId(registerResponse.getUserid());
                    handler.sendEmptyMessage(HANDLER_REGISTER_SUCCESS);
                }
            }
        });
    }
}
