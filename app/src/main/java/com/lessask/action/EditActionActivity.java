package com.lessask.action;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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
import com.lessask.MainActivity;
import com.lessask.R;
import com.lessask.dialog.LoadingDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.net.HttpHelper;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;
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
    private String path;
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

    private ArrayList<Integer> tagDatas;
    private ArrayList<String> noticeDatas;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Gson gson = new Gson();
    private Config config = globalInfos.getConfig();
    private ActionTagsHolder actionTagsHolder = globalInfos.getActionTagsHolder();

    private final int SELECT_TAGS = 1;
    private final int RECORD_ACTION = 2;

    private final int EDITE_CTION_START= 1;
    private final int EDITE_ACTION_DONE = 2;
    private final int EDITE_ACTION_ERROR = 3;
    private final int DOWNLOAD_CTION_START= 4;
    private final int DOWNLOAD_ACTION_DONE = 5;
    private final int DOWNLOAD_ACTION_ERROR = 6;
    private LoadingDialog loadingDialog;
    private Intent mIntent;


    private ActionItem oldActionItem;
    private ActionItem newActionItem;
    private boolean isReRecord;
    private boolean isEdit;
    private String newVideoPath;
    private String newVideoLocalName;
    private String oldVideoName;
    private int itemPosition;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "login handler:" + msg.what);
            switch (msg.what) {
                case EDITE_CTION_START:
                    break;
                case EDITE_ACTION_DONE:
                    HandleActionResponse response = (HandleActionResponse)msg.obj;
                    if(msg.arg1==1){
                        int videoId = response.getVideoId();
                        String videoName = response.getVideoName();
                        //删除旧video文件
                        File oldVideoFile = new File(config.getVideoCachePath(), oldVideoName);
                        if(oldVideoFile.exists() && oldVideoFile.isFile()) {
                            oldVideoFile.delete();
                            Log.e(TAG, "delete:"+oldVideoFile.getAbsolutePath());
                        }

                        //重命名新video文件
                        File newVideoFile = new File(config.getVideoCachePath(), newVideoLocalName);
                        if(newVideoFile.exists() && newVideoFile.isFile()) {
                            newVideoFile.renameTo(new File(config.getVideoCachePath(), videoName));
                            Log.e(TAG, "rename:" + videoName);
                        }

                        ActionItem actionItem = new ActionItem(videoId,mName.getText().toString(),videoName,tagDatas, noticeDatas);
                        mIntent.putExtra("actionItem", actionItem);
                        mIntent.putExtra("position", itemPosition);
                        Toast.makeText(EditActionActivity.this, "load video success", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(EditActionActivity.this, "load video failed", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
                case EDITE_ACTION_ERROR:
                    String error = (String)msg.obj;
                    Toast.makeText(EditActionActivity.this, "更新动作错误,"+error, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getIntent();
        itemPosition = mIntent.getIntExtra("position", -1);
        oldActionItem = mIntent.getParcelableExtra("actionItem");
        oldVideoName = oldActionItem.getVideo();

        setContentView(R.layout.activity_edit_action);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tagDatas = oldActionItem.getTags();
        noticeDatas = oldActionItem.getNotices();
        mName = (EditText) findViewById(R.id.name);
        mName.setText(oldActionItem.getName());
        mName.clearFocus();

        mRerecord = (Button)findViewById(R.id.re_record);
        mRerecord.setOnClickListener(this);

        mSave = (ImageView)findViewById(R.id.save);
        mSave.setOnClickListener(this);

        mEditTags = (ImageView) findViewById(R.id.edit_tags);
        mEditTags.setOnClickListener(this);
        mTagView = (TagView) findViewById(R.id.selected_tags);
        for(int i=0;i<tagDatas.size();i++){
            Log.e(TAG, "add tag:"+i);
            mTagView.addTag(getTag(tagDatas.get(i)));
        }

        mNotice = (ImageView) findViewById(R.id.notice);
        mNotice.setOnClickListener(this);
        ArrayList<String> originNotices = (ArrayList<String>)noticeDatas.clone();
        for(int i=0;i<originNotices.size();i++){
            Log.e(TAG, "add notice:"+i);
            addTextView2AttentionListItem(noticeDatas.get(i));
        }

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
            new Thread(new Runnable() {
                Message msg = new Message();
                @Override
                public void run() {
                    msg.what = DOWNLOAD_CTION_START;
                    handler.sendMessage(msg);

                    if(HttpHelper.httpDownload(videoUrl, videoFile.getAbsolutePath())) {
                        Log.e(TAG, "download success");
                        msg.what = DOWNLOAD_ACTION_DONE;
                        handler.sendMessage(msg);
                    }else {
                        Log.e(TAG, "download failed");
                        msg.what = DOWNLOAD_ACTION_ERROR;
                        handler.sendMessage(msg);
                    }
                }
            }).start();
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
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        //停止播放视频
        mScalableVideoView.stop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        mScalableVideoView.start();
        mName.clearFocus();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
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
                    File file = new File(newVideoPath);
                    if(file.exists() && file.isFile())
                        file.delete();
                }
                intent = new Intent(EditActionActivity.this, RecordVideoActivity.class);
                intent.putExtra("startActivityForResult", true);
                startActivityForResult(intent, RECORD_ACTION);
                break;
            case R.id.save:
                String videoName = oldVideoName;
                if(isReRecord)
                    videoName = newVideoLocalName;
                ActionItem newAction = new ActionItem(0, mName.getText().toString().trim(), videoName, tagDatas, noticeDatas);
                if(checkChange(oldActionItem, newAction)){
                    updateAction();
                }else {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void updateAction(){
        loadingDialog = new LoadingDialog(EditActionActivity.this);
        PostSingleEvent event = new PostSingleEvent() {
            @Override
            public void onStart() {
                Message msg = new Message();
                msg.what = EDITE_CTION_START;
                handler.sendMessage(msg);
            }

            @Override
            public void onDone(PostResponse postResponse) {
                Message msg = new Message();
                msg.what = EDITE_ACTION_DONE;
                String body = postResponse.getBody();
                HandleActionResponse response = gson.fromJson(body, HandleActionResponse.class);
                msg.obj = response;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(String err) {
                Message msg = new Message();
                msg.what = EDITE_ACTION_ERROR;
                msg.obj = err;
                handler.sendMessage(msg);
            }
        };
        PostSingle postSingle = new PostSingle(config.getUpdateActionUrl(), event);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("id",oldActionItem.getId()+"");
        headers.put("userid", globalInfos.getUserid()+"");
        headers.put("name", mName.getText().toString().trim());
        headers.put("tags", getTagsString());
        headers.put("notice", getNoticeString());
        if(isReRecord)
            headers.put("oldVideoName", new File(newVideoPath).getName());
        postSingle.setHeaders(headers);

        if(isReRecord) {
            HashMap<String, String> files = new HashMap<>();
            files.put("vediofile", path);
            postSingle.setFiles(files);
        }
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
            case RECORD_ACTION:
                newVideoPath = data.getStringExtra("path");
                File file = new File(newVideoPath);
                newVideoLocalName = file.getName();
                float ratio = data.getFloatExtra("ratio", 1.33f);
                String image = data.getStringExtra("imagePath");
                isReRecord = true;
                play(newVideoPath);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        String videoName = oldVideoName;
        if(isReRecord) {
            videoName = newVideoLocalName;
        }
        ActionItem newAction = new ActionItem(0, mName.getText().toString().trim(), videoName, tagDatas, noticeDatas);
        if(checkChange(oldActionItem, newAction)){
            this.setResult(MainActivity.EDIT_ACTION);
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
        }else {
            Log.e(TAG, "not change");
            finish();
        }
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

    private boolean checkChange(ActionItem newOne, ActionItem oldOne){
        return !newOne.equals(oldOne);
    }
}