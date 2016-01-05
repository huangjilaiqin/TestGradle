package com.lessask.action;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.lessask.lesson.EditLessonActivity;
import com.lessask.model.ActionItem;
import com.lessask.net.NetworkFileHelper;
import com.lessask.tag.SelectTagsActivity;
import com.lessask.video.RecordVideoActivity;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class EditActionActivity extends AppCompatActivity implements OnClickListener{

    private final String TAG = EditActionActivity.class.getSimpleName();
    private ScalableVideoView mScalableVideoView;
    private EditText mName;
    private ImageView mEditTags;
    private TagView mTagView;
    private ImageView mNotice;
    private Button mRerecord;
    private DisplayMetrics displaymetrics;
    private float widthDivideHeightRatio = 320/240f;
    private Toolbar mToolbar;
    private ImageView mSave;
    public static final int EDIT_ACTION = 1;

    private ArrayList<Integer> tagDatas;
    private ArrayList<String> noticeDatas;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private Config config = globalInfos.getConfig();
    private ActionTagsHolder actionTagsHolder = globalInfos.getActionTagsHolder();

    private final int SELECT_TAGS = 1;
    private final int RECORD_ACTION = 2;

    private LoadingDialog loadingDialog;
    private Intent mIntent;

    private ActionItem actionItem;

    private boolean isReRecord;
    //重拍后的视频路径
    private String newLocalVideoPath;
    private String newActionImage;
    private String oldVideoName;
    private String oldActionImage;

    private int itemPosition;
    private NetworkFileHelper networkFileHelper = NetworkFileHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getIntent();
        itemPosition = mIntent.getIntExtra("position", -1);
        actionItem = mIntent.getParcelableExtra("actionItem");
        oldVideoName = actionItem.getVideoName();
        oldActionImage = actionItem.getActionImage();

        loadingDialog = new LoadingDialog(EditActionActivity.this);

        setContentView(R.layout.activity_edit_action);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tagDatas = actionItem.getTags();
        noticeDatas = actionItem.getNotices();
        mName = (EditText) findViewById(R.id.name);
        mName.setText(actionItem.getName());
        mName.clearFocus();

        mRerecord = (Button)findViewById(R.id.re_record);
        mRerecord.setOnClickListener(this);

        mSave = (ImageView)findViewById(R.id.save);
        mSave.setOnClickListener(this);

        mEditTags = (ImageView) findViewById(R.id.edit_tags);
        mEditTags.setOnClickListener(this);
        mTagView = (TagView) findViewById(R.id.selected_tags);
        for(int i=0;i<tagDatas.size();i++){
            mTagView.addTag(getTag(tagDatas.get(i)));
        }

        mNotice = (ImageView) findViewById(R.id.notice);
        mNotice.setOnClickListener(this);
        initAttentionListItem();

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

        final File videoFile = new File(config.getVideoCachePath(), oldVideoName);
        final String videoUrl = config.getVideoUrl()+oldVideoName;

        if(!videoFile.exists()){
            NetworkFileHelper.getInstance().startGetFile(videoUrl, videoFile.getAbsolutePath(), new NetworkFileHelper.GetFileRequest() {
                @Override
                public void onStart() {
                    //显示加载中
                    Log.e(TAG, "download "+videoFile.getAbsolutePath());
                }

                @Override
                public void onResponse(String error) {
                    if(error!=null){
                        Log.e(TAG, "get action video error:"+error);
                        Toast.makeText(EditActionActivity.this, "error:"+error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    play(videoFile.getAbsolutePath());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(EditActionActivity.this, "下载文件错误:"+error, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            play(new File(config.getVideoCachePath(), oldVideoName).getAbsolutePath());
        }
    }

    private void play(final String path){
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
                    Toast.makeText(EditActionActivity.this, "播放视频异常", Toast.LENGTH_SHORT).show();
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

    private void initAttentionListItem(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.notice_item_layout);
        LinearLayout.LayoutParams tvLayoutParams;
        for(int i=0;i<noticeDatas.size();i++) {
            final TextView textView = new TextView(this);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = textView.getText().toString();
                    beforeEditContent = content;
                    showEditNoticeDialog(true, content, (TextView) v);
                }
            });
            textView.setText(noticeDatas.get(i));
            textView.setTextSize(18);
            textView.setBackgroundResource(R.drawable.text_white_bg);
            textView.setPadding(5, 3, 5, 3);
            linearLayout.addView(textView);
            tvLayoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            tvLayoutParams.bottomMargin = 3;
            textView.setLayoutParams(tvLayoutParams);
        }
    }

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
        textView.setPadding(5, 3, 5, 3);
        linearLayout.addView(textView);
        tvLayoutParams = (LinearLayout.LayoutParams)textView.getLayoutParams();
        tvLayoutParams.bottomMargin = 3;
        textView.setLayoutParams(tvLayoutParams);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        //停止播放视频
        mScalableVideoView.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScalableVideoView.start();
        mName.clearFocus();
        Log.e(TAG, "onResume");
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
            case R.id.re_record:
                if(isReRecord){
                    File file = new File(newLocalVideoPath);
                    if(file.exists() && file.isFile())
                        file.delete();
                    file = new File(newActionImage);
                    if(file.exists() && file.isFile())
                        file.delete();
                }
                intent = new Intent(EditActionActivity.this, RecordVideoActivity.class);
                intent.putExtra("startActivityForResult", true);
                startActivityForResult(intent, RECORD_ACTION);
                break;
            case R.id.save:
                if(mName.toString().trim().length()<=0){
                    Toast.makeText(EditActionActivity.this, "请填写动作名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                networkFileHelper.startPost(config.getUpdateActionUrl(), HandleActionResponse.class, updateActionRequest);
                break;
            default:
                break;
        }
    }

    private String getTagsString(){
        StringBuilder builder = new StringBuilder();
        int tagSize = tagDatas.size();
        int lastIndex = tagSize-1;
        for(int i=0;i<tagSize;i++){
            builder.append(tagDatas.get(i));
            if(i!=lastIndex){
                builder.append(";");
            }
        }
        return builder.toString();
    }

    // to do 字段越来越长
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
                ArrayList<Integer> newTagDatas = data.getIntegerArrayListExtra("tagDatas");
                Log.e(TAG, "on result:"+tagDatas);
                mTagView.removeAllTags();
                tagDatas.clear();
                for(int i=0;i<newTagDatas.size();i++){
                    int tagId = newTagDatas.get(i);
                    mTagView.addTag(getTag(tagId));
                    tagDatas.add(tagId);
                }
                break;
            case RECORD_ACTION:
                newLocalVideoPath = data.getStringExtra("videoPath");
                Log.e(TAG, "newLocalVideoPath size:"+new File(newLocalVideoPath).length()/1024);
                newActionImage = data.getStringExtra("imagePath");
                float ratio = data.getFloatExtra("ratio", 1.33f);
                isReRecord = true;
                play(newLocalVideoPath);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.setResult(EDIT_ACTION);
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActionActivity.this);
        builder.setMessage("确认放弃修改吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EditActionActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private NetworkFileHelper.PostFileRequest updateActionRequest = new NetworkFileHelper.PostFileRequest() {
        @Override
        public void onStart() {
            loadingDialog.show();
        }

        @Override
        public void onResponse(Object response) {
            HandleActionResponse handleActionResponse = (HandleActionResponse) response;
            if(handleActionResponse.getError()!=null){
                Toast.makeText(EditActionActivity.this, "error:"+handleActionResponse.getError(), Toast.LENGTH_SHORT).show();
                return;
            }
            int actionId = handleActionResponse.getActionId();
            //这是完整路径
            String videoName = handleActionResponse.getVideoName();
            String actionIamge = handleActionResponse.getActionImage();
            if(isReRecord) {
                //删除旧video文件
                File oldVideoFile = new File(oldVideoName);
                Log.e(TAG, "delete oldVideoName:" + oldVideoFile.getAbsolutePath());
                if (oldVideoFile.exists() && oldVideoFile.isFile()) {
                    oldVideoFile.delete();
                    Log.e(TAG, "delete old file:" + oldVideoFile.getAbsolutePath());
                }

                //重命名新video文件
                File localVideoFile = new File(newLocalVideoPath);
                if (localVideoFile.exists() && localVideoFile.isFile()) {
                    //网络文件名字
                    File newVideoFile = new File(config.getVideoCachePath(), videoName);
                    Log.e(TAG, "before rename new file:" + localVideoFile.getAbsolutePath());
                    if (localVideoFile.renameTo(newVideoFile))
                        Log.e(TAG, "after rename new file:" + localVideoFile.getAbsolutePath());
                    else
                        Log.e(TAG, "fialde after rename new file:" + localVideoFile.getAbsolutePath());
                }
            }
            Log.e(TAG, "newVideoName:" + videoName + ", new actionImage:" + actionIamge);
            actionItem.setName(mName.getText().toString().trim());
            actionItem.setVideoName(videoName);
            actionItem.setActionImage(actionIamge);
            mIntent.putExtra("actionItem", actionItem);
            mIntent.putExtra("position", itemPosition);
            EditActionActivity.this.setResult(RESULT_OK, mIntent);
            loadingDialog.cancel();
            finish();
        }

        @Override
        public void onError(String error) {
            loadingDialog.cancel();
            Toast.makeText(EditActionActivity.this, "更新动作错误:" + error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public HashMap<String, String> getHeaders() {
            HashMap<String, String> headers = new HashMap<>();

            actionItem.setName(mName.getText().toString());
            headers.put("userid", globalInfos.getUserId() + "");
            headers.put("actionItem", gson.toJson(actionItem));
            Log.e(TAG, "actionItem:"+gson.toJson(actionItem));
            return headers;
        }

        @Override
        public HashMap<String, String> getFiles() {
            HashMap<String, String> files = new HashMap<>();
            if(isReRecord){
                files.put("videofile", newLocalVideoPath);
            }
            return files;
        }

        @Override
        public HashMap<String, String> getImages() {
            HashMap<String, String> images = new HashMap<>();
            if(isReRecord){
                images.put("actionImage", newActionImage);
            }
            return images;
        }
    };
}

