package com.lessask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.chat.Chat;
import com.lessask.chat.GlobalInfos;
import com.lessask.model.NothingResponse;
import com.lessask.model.RegisterResponse;
import com.lessask.model.User;
import com.lessask.model.UserInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by JHuang on 2015/8/23.
 */
public class FragmentMe extends Fragment{
    private Chat chat = Chat.getInstance();
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();

    private ImageView ivHeadImg;
    private TextView etMail;
    private String mail;
    private EditText etNickname;
    private String nickname;
    private EditText etPasswd;
    private String passwd;
    private EditText etConfirmPasswd;
    private Button bSave;
    private Uri headImgUri;
    private Uri headImgUriTmp;
    private Bitmap headImgBitmap;
    private String headImgContent;
    private static final String TAG = RegisterActivity.class.getName();
    private int outputX = 180;
    private int outputY = 180;

    private int userId;
    private User user;

    private ProgressDialog changeuserinfoDialog;

    private final int HANDLER_CHANGEUSERINFO_ERROR= 0;
    private final int HANDLER_CHANGEUSERINFO_SUCCESS = 1;

    private final int LOGIN_MAIL = 1;
    private final int LOGIN_WEIXIN = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "changeuserinfo handler:" + msg.what);
            switch (msg.what){
                case HANDLER_CHANGEUSERINFO_ERROR:
                    RegisterResponse nothingResponse = (RegisterResponse)msg.obj;
                    Log.e(TAG, "changeuserinfo errno:"+nothingResponse.getErrno()+", error:"+nothingResponse.getError());
                    changeuserinfoDialog.cancel();
                    Toast.makeText(getActivity().getApplicationContext(), "changeuserinfo errno:" + nothingResponse.getErrno() + ", error:" + nothingResponse.getError(), Toast.LENGTH_LONG).show();
                    break;
                case HANDLER_CHANGEUSERINFO_SUCCESS :
                    //to do 保存用户信息成功, 将头像信息写到本地文件中
                    if(headImgBitmap!=null){
                        try {
                            File file = new File(headImgUri.getPath());
                            FileOutputStream os = new FileOutputStream(file);
                            boolean result = headImgBitmap.compress(Bitmap.CompressFormat.JPEG, 0, os);
                            Log.e(TAG, "write headImg:"+file.getName()+", result:"+result);
                        }catch (Exception e){

                        }
                    }

                    //去掉转圈圈
                    changeuserinfoDialog.cancel();
                    Toast.makeText(getActivity().getApplicationContext(), "注册成功, 跳转登录...", Toast.LENGTH_LONG).show();
                    //跳转到首页
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        userId = globalInfos.getUserid();
        user = globalInfos.getUser();

        File appDir = getActivity().getApplicationContext().getExternalFilesDir("headImg");
        String fileName = userId+".jpg";
        File imageFile = new File(appDir, fileName);
        Log.e(TAG, imageFile.getAbsolutePath());
        headImgUri = Uri.fromFile(imageFile);//获取文件的Uri

        appDir = getActivity().getApplicationContext().getExternalFilesDir("tmp");
        fileName = userId+".jpg";
        imageFile = new File(appDir, fileName);
        headImgUriTmp = Uri.fromFile(imageFile);//获取文件的Uri

        ivHeadImg = (ImageView)view.findViewById(R.id.head_img);
        Bitmap bitmap = user.getHeadImg();
        if(bitmap != null){
            ivHeadImg.setImageBitmap(bitmap);
        }
        etMail = (TextView)view.findViewById(R.id.mail);
        etMail.setText(user.getMail());
        etNickname = (EditText)view.findViewById(R.id.nickname);
        etNickname.setText(user.getNickname());
        etPasswd = (EditText)view.findViewById(R.id.passwd);
        etConfirmPasswd = (EditText)view.findViewById(R.id.confirm_passwd);
        bSave = (Button)view.findViewById(R.id.save);
        headImgContent = "";

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = etMail.getText().toString().trim();
                nickname = etNickname.getText().toString().trim();
                passwd = etPasswd.getText().toString().trim();
                String confirmPasswd = etConfirmPasswd.getText().toString().trim();

                if(mail==null || passwd==null || confirmPasswd==null || nickname==null){
                    Toast.makeText(getActivity().getApplicationContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!passwd.equals(confirmPasswd)){
                    Toast.makeText(getActivity().getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
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

                //修改用户信息
                String headImgName = userId+".jpg";
                UserInfo userInfo = new UserInfo(userId, mail, passwd, nickname, headImgName, headImgContent);
                chat.emit("changeUserInfo", gson.toJson(userInfo));
                changeuserinfoDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
                changeuserinfoDialog.setTitle("保存中...");
                //changeuserinfoDialog.setCancelable(false);
                changeuserinfoDialog.show();
            }
        });
        ivHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = null;
                if (dialog == null) {
                    dialog = new AlertDialog.Builder(getActivity()).setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //不能同时设置输出到文件中 和 从data中返回
                                //intent.putExtra(MediaStore.EXTRA_OUTPUT, headImgUriTmp);
                                intent.putExtra("return-data", true);
                                startActivityForResult(intent, 101);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                intent.putExtra("output", headImgUriTmp);
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

        chat.setChangeUserInfo(new Chat.ChangeUserInfoListener() {
            @Override
            public void changeUserInfo(String data) {
                Log.e(TAG, "changeUserInfo:" + data);
                NothingResponse nothingResponse = gson.fromJson(data, NothingResponse.class);
                Log.d(TAG, "" + nothingResponse.getErrno() + ", " + nothingResponse.getError());
                if (nothingResponse.getErrno() != 0 || nothingResponse.getError() != null && nothingResponse.getError().length() != 0) {
                    Message msg = new Message();
                    msg.what = HANDLER_CHANGEUSERINFO_ERROR;
                    msg.obj = nothingResponse;
                    handler.sendMessage(msg);
                    return;
                } else {
                    handler.sendEmptyMessage(HANDLER_CHANGEUSERINFO_SUCCESS);
                }
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e(TAG, "requestCode:" + requestCode + ", resultCode:" + resultCode + ", RESULT_OK:" + Activity.RESULT_OK);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case 100:
                    //bmp = intent.getParcelableExtra("data");
                    Log.e(TAG, "从相册选取");
                    headImgBitmap = decodeUriAsBitmap(headImgUriTmp, null);
                    ivHeadImg.setImageBitmap(headImgBitmap);
                    break;
                case 101:
                    Log.e(TAG, "从相机选取");
                    /*从uri中获取需要自己剪裁
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outHeight = 180;
                    options.outWidth = 180;
                    bmp = decodeUriAsBitmap(headImgUri, options);
                    //*/

                    headImgBitmap = intent.getParcelableExtra("data");
                    ivHeadImg.setImageBitmap(headImgBitmap);
                    break;
                default:
                    break;
            }
        }
    }
}
