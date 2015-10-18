package com.lessask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Register;
import com.lessask.model.RegisterResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class RegisterActivity extends Activity {

    private Chat chat = Chat.getInstance();
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();

    private ImageView ivHeadImg;
    private EditText etMail;
    private String mail;
    private EditText etNickname;
    private String nickname;
    private EditText etPasswd;
    private String passwd;
    private EditText etConfirmPasswd;
    private Button bRegister;
    private Uri headImgUri;
    private String headImgContent;
    private static final String TAG = RegisterActivity.class.getName();
    private int outputX = 180;
    private int outputY = 180;

    private ProgressDialog registerDialog;

    private final int HANDLER_REGISTER_ERROR = 0;
    private final int HANDLER_REGISTER_SUCCESS = 1;

    private final int LOGIN_MAIL = 1;
    private final int LOGIN_WEIXIN = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "register handler:"+msg.what);
            switch (msg.what){
                case HANDLER_REGISTER_ERROR:
                    RegisterResponse registerResponse = (RegisterResponse)msg.obj;
                    Log.e(TAG, "register errno:"+registerResponse.getErrno()+", error:"+registerResponse.getError());
                    registerDialog.cancel();
                    Toast.makeText(RegisterActivity.this, "register errno:"+registerResponse.getErrno()+", error:"+registerResponse.getError(), Toast.LENGTH_LONG).show();
                    break;
                case HANDLER_REGISTER_SUCCESS:
                    Log.d(TAG,"register success userid:"+globalInfos.getUserid());
                    File appDir = getApplicationContext().getExternalFilesDir("headImg");
                    String fileName = "myheadImg.jpg";
                    File oldImgFile = new File(appDir, fileName);
                    File newImgFile = new File(appDir, globalInfos.getUserid()+".jpg");
                    if(oldImgFile.exists()) {
                        oldImgFile.renameTo(newImgFile);
                    }
                    //去掉转圈圈
                    registerDialog.cancel();
                    Toast.makeText(RegisterActivity.this, "注册成功, 跳转登录...", Toast.LENGTH_LONG).show();
                    //跳转到首页
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("mail", mail);
                    intent.putExtra("passwd", passwd);

                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    private Bitmap decodeUriAsBitmap(Uri uri, BitmapFactory.Options options){
      Bitmap bitmap = null;
      try {
          Log.e(TAG, "bitmap:"+uri);
          //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
          bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
      return bitmap;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ivHeadImg = (ImageView)findViewById(R.id.head_img);
        etMail = (EditText)findViewById(R.id.mail);
        etNickname = (EditText)findViewById(R.id.nickname);
        etPasswd = (EditText)findViewById(R.id.passwd);
        etConfirmPasswd = (EditText)findViewById(R.id.confirm_passwd);
        bRegister = (Button)findViewById(R.id.register);
        headImgContent = "";

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
                //读取头像
                File headImgFile = new File(headImgUri.getPath());
                if(headImgFile.exists()){
                    BufferedInputStream in = null;
                    try{
                        in = new BufferedInputStream(new FileInputStream(headImgFile));
                        byte[] imgByte = new byte[(int)headImgFile.length()];
                        in.read(imgByte, 0, imgByte.length);
                        headImgContent = Base64.encodeToString(imgByte, Base64.DEFAULT);
                    }catch (FileNotFoundException e){

                    }catch (IOException e){

                    }finally {
                        try{
                            if(in != null)
                                in.close();
                        }catch (IOException e){

                        }
                    }
                }

                //发起注册请求
                Register register = new Register(LOGIN_MAIL, mail, nickname, passwd, headImgContent);
                chat.emit("register", gson.toJson(register));
                registerDialog = new ProgressDialog(RegisterActivity.this, ProgressDialog.STYLE_SPINNER);
                registerDialog.setTitle("注册中...");
                //registerDialog.setCancelable(false);
                registerDialog.show();
            }
        });
        ivHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 File appDir = getApplicationContext().getExternalFilesDir("headImg");
                 String fileName = "myheadImg.jpg";
                 File imageFile = new File(appDir, fileName);
                 Log.e(TAG, imageFile.getAbsolutePath());
                 headImgUri = Uri.fromFile(imageFile);//获取文件的Uri

                 AlertDialog dialog = null;
                 if (dialog == null) {
                     dialog = new AlertDialog.Builder(RegisterActivity.this).setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             if (which == 0) {
                                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                 //不能同时设置输出到文件中 和 从data中返回
                                 //intent.putExtra(MediaStore.EXTRA_OUTPUT, headImgUri);
                                 intent.putExtra("return-data", true);
                                 startActivityForResult(intent, 101);
                             } else {
                                 Intent intent = new Intent(Intent.ACTION_PICK, null);
                                 intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                 intent.putExtra("output", headImgUri);
                                 intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                                 intent.putExtra("crop", "true");
                                 intent.putExtra("aspectX", 1);// 裁剪框比例
                                 intent.putExtra("aspectY", 1);
                                 intent.putExtra("outputX", outputX);// 输出图片大小
                                 intent.putExtra("outputY", outputY);
                                 //intent.putExtra("return-data", true);
                                 startActivityForResult(intent, 100);
                             }
                         }
                     }).create();
                 }
                 if (!dialog.isShowing()) {
                     dialog.show();
                 }
            }
        });

        chat.setRegisterListener(new Chat.RegisterListener() {
            @Override
            public void register(String data) {
                Log.e(TAG, "register:"+data);
                RegisterResponse registerResponse = gson.fromJson(data, RegisterResponse.class);
                Log.d(TAG, ""+registerResponse.getErrno()+", "+registerResponse.getError());
                if (registerResponse.getErrno() != 0 || registerResponse.getError()!=null && registerResponse.getError().length() != 0) {
                    Message msg = new Message();
                    msg.what = HANDLER_REGISTER_ERROR;
                    msg.obj = registerResponse;
                    handler.sendMessage(msg);
                    return;
                }else {
                    globalInfos.setUserid(registerResponse.getUserid());
                    handler.sendEmptyMessage(HANDLER_REGISTER_SUCCESS);
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e(TAG, "requestCode:" + requestCode + ", resultCode:" + resultCode + ", RESULT_OK:" + RESULT_OK);
        if (resultCode == RESULT_OK) {
            Bitmap bmp = null;
            switch (requestCode){
                case 100:
                    //bmp = intent.getParcelableExtra("data");
                    Log.e(TAG, "从相册选取");
                    bmp = decodeUriAsBitmap(headImgUri, null);
                    ivHeadImg.setImageBitmap(bmp);
                    break;
                case 101:
                    Log.e(TAG, "从相机选取");
                    /*从uri中获取需要自己剪裁
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outHeight = 180;
                    options.outWidth = 180;
                    bmp = decodeUriAsBitmap(headImgUri, options);
                    //*/

                    bmp = intent.getParcelableExtra("data");
                    ivHeadImg.setImageBitmap(bmp);
                    break;
                default:
                    break;
            }
        }
    }
}
