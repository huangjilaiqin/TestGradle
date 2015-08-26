package com.lessask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.lessask.model.DownImageAsync;
import com.lessask.model.NothingResponse;
import com.lessask.model.RegisterResponse;
import com.lessask.model.User;
import com.lessask.model.UserInfo;

import java.io.File;
import java.io.FileOutputStream;

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
    private boolean changeHeadImg;

    private Bitmap headImgBitmap;
    private String headImgContent;
    private static final String TAG = FragmentMe.class.getName();
    private int outputX = 180;
    private int outputY = 180;
    private View view;

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
                    Toast.makeText(getActivity(), "changeuserinfo errno:" + nothingResponse.getErrno() + ", error:" + nothingResponse.getError(), Toast.LENGTH_LONG).show();
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
                    Log.e(TAG, "handle success");
                    //去掉转圈圈
                    if(changeuserinfoDialog==null){
                        Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();
                    }else {
                        changeuserinfoDialog.dismiss();
                        Toast.makeText(getActivity(), "修改信息成功", Toast.LENGTH_LONG).show();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userId = globalInfos.getUserid();
        user = globalInfos.getUser();
        changeHeadImg = false;
        Log.e(TAG,"onActivityCreated");

        File headImgDir = globalInfos.getHeadImgDir();
        File imageFile = new File(headImgDir, userId+".jpg");
        Log.e(TAG, imageFile.getAbsolutePath());
        headImgUri = Uri.fromFile(imageFile);//获取文件的Uri

        File appDir = getActivity().getApplicationContext().getExternalFilesDir("tmp");
        File imageFileTmp = new File(appDir, userId+".jpg");
        headImgUriTmp = Uri.fromFile(imageFile);//获取文件的Uri

        ivHeadImg = (ImageView)view.findViewById(R.id.head_img);
        //获取用户原来的头像
        if(imageFile.exists()){
            Bitmap bmp = Utils.decodeUriAsBitmap(headImgUri);
            ivHeadImg.setImageBitmap(bmp);
        }else {
            //设置默认头像
            ivHeadImg.setImageResource(R.mipmap.ic_launcher);
            //异步网络请求
            new DownImageAsync(globalInfos.getHeadImgHost()+user.getUserid()+".jpg",ivHeadImg).execute();
        }

        etMail = (TextView)view.findViewById(R.id.mail);
        etMail.setText(user.getMail());
        etNickname = (EditText)view.findViewById(R.id.nickname);
        etNickname.setText(user.getNickname());
        etPasswd = (EditText)view.findViewById(R.id.passwd);
        etPasswd.setText(user.getPasswd());
        etConfirmPasswd = (EditText)view.findViewById(R.id.confirm_passwd);
        etConfirmPasswd.setText(user.getPasswd());
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
                UserInfo userInfo;
                //读取头像
                if(changeHeadImg){
                    headImgContent = Utils.decodeUriAsBase64(headImgUriTmp);
                    String headImgName = userId+".jpg";
                    userInfo = new UserInfo(userId, mail, passwd, nickname, headImgName, headImgContent);
                }else {
                    userInfo = new UserInfo(userId, mail, passwd, nickname);
                }
                //修改用户信息

                changeuserinfoDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
                changeuserinfoDialog.setTitle("保存中...");
                //changeuserinfoDialog.setCancelable(false);
                changeuserinfoDialog.show();
                Log.e(TAG, "init changeuserinfoDialog");
                chat.emit("changeUserInfo", gson.toJson(userInfo));
                if(changeuserinfoDialog==null){
                    Toast.makeText(getActivity(), "null ag", Toast.LENGTH_LONG).show();
                }
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

        chat.setChangeUserInfoListener(new Chat.ChangeUserInfoListener() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, null);
        Log.e(TAG,"onCreateView");
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e(TAG, "requestCode:" + requestCode + ", resultCode:" + resultCode + ", RESULT_OK:" + Activity.RESULT_OK);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case 100:
                    //bmp = intent.getParcelableExtra("data");
                    Log.e(TAG, "从相册选取");
                    headImgBitmap = Utils.decodeUriAsBitmap(headImgUriTmp);
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
            changeHeadImg = true;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}
