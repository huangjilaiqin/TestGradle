package com.lessask.action;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.dialog.LoadingDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;
import com.lessask.tag.SelectTagsActivity;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class EditActionActivity extends AppCompatActivity implements OnClickListener{

    private final int SELECT_TAGS = 1;
    private final String TAG = EditActionActivity.class.getSimpleName();
    private String path;
    private ScalableVideoView mScalableVideoView;
    private EditText mName;
    private ImageView mEditTags;
    private TagView mTagView;
    private ImageView mNotice;
    private ImageView mUpload;
    private DisplayMetrics displaymetrics;
    private float widthDivideHeightRatio = 320/240f;
    private Toolbar mToolbar;

    private ArrayList<Integer> tagDatas;
    private ArrayList<String> noticeDatas;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private Config config = globalInfos.getConfig();
    private ActionTagsHolder actionTagsHolder = globalInfos.getActionTagsHolder();

    private final int ON_UPLOAD_START = 1;
    private final int ON_UPLOAD_DONE = 2;
    private LoadingDialog loadingDialog;
    private Intent mIntent;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:" + msg.what);
            switch (msg.what) {
                case ON_UPLOAD_START:
                    loadingDialog.show();
                    break;
                case ON_UPLOAD_DONE:
                    loadingDialog.cancel();
                    if(msg.arg1==1){
                        UploadActionResponse response = (UploadActionResponse)msg.obj;
                        int videoId = response.getVedioid();
                        String videoName = response.getVedioName();
                        ActionItem actionItem = new ActionItem(videoId,videoName,mName.getText().toString(),tagDatas, noticeDatas);
                        mIntent.putExtra("actionItem", actionItem);
                        Toast.makeText(EditActionActivity.this, "upload success", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(EditActionActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getIntent();
        ActionItem actionItem = mIntent.getParcelableExtra("actionItem");
        String vedioName = actionItem.getVedio();

        //to do volley请求动作视频

        setContentView(R.layout.activity_create_action);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tagDatas = new ArrayList<>();
        noticeDatas = new ArrayList<>();
        mName = (EditText) findViewById(R.id.name);
        mName.clearFocus();

        mEditTags = (ImageView) findViewById(R.id.edit_tags);
        mEditTags.setOnClickListener(this);
        mTagView = (TagView) findViewById(R.id.selected_tags);

        mNotice = (ImageView) findViewById(R.id.notice);
        mNotice.setOnClickListener(this);
        mUpload = (ImageView) findViewById(R.id.upload);
        mUpload.setOnClickListener(this);

        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        mScalableVideoView = (ScalableVideoView) findViewById(R.id.preview_video);
        mScalableVideoView.setOnClickListener(this);

        try {
            // 这个调用是为了初始化mediaplayer并让它能及时和surface绑定
            mScalableVideoView.setDataSource("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //修改控件大小

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mScalableVideoView.getLayoutParams();
        layoutParams.width = displaymetrics.widthPixels;
        layoutParams.height = (int)(displaymetrics.widthPixels/widthDivideHeightRatio);
        mScalableVideoView.setLayoutParams(layoutParams);


        //to do 下载文件后启动
        //在低端的手机中不用线程会显示不出来
        new Thread(){
            @Override
            public void run() {
                try {
                    mScalableVideoView.setDataSource(path);
                    mScalableVideoView.setLooping(true);
                    mScalableVideoView.prepare();
                    mScalableVideoView.start();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    Toast.makeText(getBaseContext(), "播放视频异常", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }



    private void editTextView2AttentionListItem(String content, View v){
        TextView textView = (TextView)v;
        if(content.length()>0){
            textView.setText(content);
        }else {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.notice_item_layout);
            linearLayout.removeView(v);
            noticeDatas.remove(beforeEditContent);
        }
    }
    private String beforeEditContent;
    private void addTextView2AttentionListItem(final String content){
        if(content.length()==0)
            return;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.notice_item_layout);
        LinearLayout.LayoutParams tvLayoutParams;
        final TextView textView = new TextView(this);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "click:" + content);
                beforeEditContent = content;
                showEditNoticeDialog(true, textView.getText().toString(), (TextView) v);
            }
        });
        noticeDatas.add(content);
        textView.setText(content);
        textView.setTextSize(18);
        textView.setBackgroundResource(R.drawable.text_white_bg);
        //setBackground(getResources().getDrawable(R.drawable.text_white_bg));
        //textView.setBackgroundColor(getResources().getLayout(R.drawable.text_white_bg));
        textView.setPadding(5, 3, 5, 3);
        linearLayout.addView(textView);
        tvLayoutParams = (LinearLayout.LayoutParams)textView.getLayoutParams();
        tvLayoutParams.bottomMargin = 3;
        textView.setLayoutParams(tvLayoutParams);
    }

    @Override
    protected void onStop() {
        //停止播放视频
        mScalableVideoView.stop();
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mScalableVideoView.start();
        mName.clearFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_tags:
                Intent intent = new Intent(this,SelectTagsActivity.class);
                intent.putIntegerArrayListExtra("tagDatas", tagDatas);
                startActivityForResult(intent, SELECT_TAGS);
                break;
            case R.id.notice:
                showEditNoticeDialog();
                break;
            case R.id.upload:
                uploadVedio();
                break;
            default:
                break;
        }
    }

    private void uploadVedio(){
        loadingDialog = new LoadingDialog(EditActionActivity.this);
        PostSingleEvent event = new PostSingleEvent() {

            @Override
            public void onStart() {
                Message msg = new Message();
                msg.what = ON_UPLOAD_START;
                handler.sendMessage(msg);
            }

            @Override
            public void onDone(boolean success, PostResponse postResponse) {
                Message msg = new Message();
                msg.what = ON_UPLOAD_DONE;
                if(postResponse!=null) {
                    int resCode = postResponse.getCode();
                    String body = postResponse.getBody();
                    UploadActionResponse response = gson.fromJson(body, UploadActionResponse.class);
                    msg.obj = response;

                    if (success)
                        msg.arg1 = 1;
                    else
                        msg.arg1 = 0;
                    handler.sendMessage(msg);
                }else {
                    msg.arg1 = 0;
                    handler.sendMessage(msg);
                }
            }
        };
        PostSingle postSingle = new PostSingle(config.getUploadVedioUrl(), event);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("userid", globalInfos.getUserid()+"");
        headers.put("name", mName.getText().toString().trim());
        headers.put("tags", getTagsString());
        headers.put("notice", getNoticeString());
        postSingle.setHeaders(headers);

        HashMap<String, String> files = new HashMap<>();
        files.put("vediofile", path);
        postSingle.setFiles(files);

        postSingle.start();

    }
    private String getTagsString(){
        StringBuilder builder = new StringBuilder();
        int tagSize = tagDatas.size();
        int lastIndex = tagSize-1;
        for(int i=0;i<tagSize;i++){
            builder.append(actionTagsHolder.getActionTagNameById(tagDatas.get(i)));
            if(i!=lastIndex){
                builder.append(";");
            }
        }
        return builder.toString();
    }
    private String getNoticeString(){
        StringBuilder builder = new StringBuilder();
        int noticeSize = noticeDatas.size();
        int lastIndex = noticeSize-1;
        for (int i=0;i<noticeSize;i++){
            builder.append(noticeDatas.get(i));
            if(i!=lastIndex){
                builder.append("##");
            }
        }
        return builder.toString();
    }

    private void showEditNoticeDialog(){
        showEditNoticeDialog(false, "", null);
    }
    private void showEditNoticeDialog(final boolean isEdit, String originContent, final TextView textView){
        Log.e(TAG, "showEditNoticeDialog"+isEdit+", "+originContent);
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.prompt_view, null);
        final Button cancle = (Button)view.findViewById(R.id.cancel_action);
        Button confirm = (Button)view.findViewById(R.id.confirm);
        final EditText content = (EditText)view.findViewById(R.id.content);
        if(isEdit)
            content.setText(originContent);
        content.setFocusable(true);
        content.setFocusableInTouchMode(true);
        confirm.requestFocus();
        content.setSelection(originContent.length());

        final Dialog dialog = new Dialog(this, R.style.nothing_dialog);
        dialog.setContentView(view);
        //设置水平全屏
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams dialogParams = dialogWindow.getAttributes();
        dialogParams.width = displaymetrics.widthPixels;
        dialogWindow.setAttributes(dialogParams);

        dialog.setCanceledOnTouchOutside(true);
        OnClickListener dialogOnClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.confirm:
                        if(isEdit)
                            editTextView2AttentionListItem(content.getText().toString().trim(), textView);
                        else
                            addTextView2AttentionListItem(content.getText().toString().trim());
                        break;
                    case R.id.cancel_action:
                        break;
                }
                dialog.dismiss();
            }
        };
        cancle.setOnClickListener(dialogOnClick);
        confirm.setOnClickListener(dialogOnClick);
        Log.e(TAG, "dialog show");
        dialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(content, 0);
            }
        },100);
    }
    private Tag getTag(int id){
        Tag tag = new Tag(actionTagsHolder.getActionTagNameById(id));
        tag.tagTextColor = R.color.main_color;
        tag.layoutColor =  Color.parseColor("#DDDDDD");
        //tag.layoutColorPress = Color.parseColor("#555555");
        //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
        tag.radius = 20f;
        tag.tagTextSize = 18f;
        tag.layoutBorderSize = 1f;
        tag.layoutBorderColor = Color.parseColor("#FFFFFF");
        //tag.isDeletable = true;
        return tag;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SELECT_TAGS:
                tagDatas = data.getIntegerArrayListExtra("tagDatas");
                Log.e(TAG, "on result:"+tagDatas);
                mTagView.removeAllTags();
                for(int i=0;i<tagDatas.size();i++){
                    mTagView.addTag(getTag(tagDatas.get(i)));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //stop();
        //Intent intent = new Intent(this,RecordVideoActivity.class);
        //startActivity(intent);
        finish();
    }

    /**
     * 获取视频缩略图（这里获取第一帧）
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(TimeUnit.MILLISECONDS.toMicros(1));
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}