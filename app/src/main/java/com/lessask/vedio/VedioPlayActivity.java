package com.lessask.vedio;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.lessask.R;
import com.lessask.dialog.LoadingDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class VedioPlayActivity extends Activity implements TextureView.SurfaceTextureListener
        ,OnClickListener,OnCompletionListener{

    private final int SELECT_TAGS = 1;
    private final String TAG = VedioPlayActivity.class.getSimpleName();
    private String path;
    private TextureView surfaceView;
    private MediaPlayer mediaPlayer;
    private ImageView imagePlay;
    private EditText mName;
    private ImageView mEditTags;
    private TagView mTagView;
    private ImageView mNotice;
    private ImageView mUpload;
    private DisplayMetrics displaymetrics;

    private ArrayList<TagData> tagDatas;
    private ArrayList<String> noticeDatas;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private final int ON_UPLOAD_START = 1;
    private final int ON_UPLOAD_DONE = 2;
    private LoadingDialog loadingDialog;

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
                        Toast.makeText(VedioPlayActivity.this, "upload success", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(VedioPlayActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_play);

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
        surfaceView = (TextureView) findViewById(R.id.preview_video);

        //修改控件大小
        RelativeLayout preview_video_parent = (RelativeLayout)findViewById(R.id.preview_video_parent);
        LayoutParams layoutParams = (LayoutParams) preview_video_parent.getLayoutParams();
        layoutParams.width = displaymetrics.widthPixels/2;
        layoutParams.height = displaymetrics.heightPixels/2;
        preview_video_parent.setLayoutParams(layoutParams);

        surfaceView.setSurfaceTextureListener(this);
        surfaceView.setOnClickListener(this);

        path = getIntent().getStringExtra("path");

        imagePlay = (ImageView) findViewById(R.id.preview_play);
        imagePlay.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mName.clearFocus();
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
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            imagePlay.setVisibility(View.GONE);
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            mediaPlayer.setDataSource(path);
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            //mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG, "prepared");
                    //mediaPlayer.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(TAG, "error:" + mp + ", what:" + what + ", extra:" + extra);
                    return false;
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.seekTo(0);

        } catch (Exception e) {
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        Log.e(TAG, "onSurfaceTextureAvailable prepare");
        Surface surface = new Surface(arg0);
        prepare(surface);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,int arg2) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_play:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                imagePlay.setVisibility(View.GONE);
                break;
            case R.id.preview_video:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    imagePlay.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.edit_tags:
                Intent intent = new Intent(this,SelectTagsActivity.class);
                intent.putParcelableArrayListExtra("tagDatas", tagDatas);
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
        loadingDialog = new LoadingDialog(VedioPlayActivity.this);
        PostSingleEvent event = new PostSingleEvent() {

            @Override
            public HashMap<String, HashMap> postData() {
                HashMap<String, HashMap> datas = new HashMap<>();
                HashMap<String, String> headers = new HashMap<>();
                headers.put("userid", globalInfos.getUserid()+"");
                headers.put("name", mName.getText().toString().trim());
                headers.put("tags", getTagsString());
                headers.put("notice", getNoticeString());

                HashMap<String, String> files = new HashMap<>();
                files.put("vediofile", path);

                datas.put("headers", headers);
                datas.put("files", files);
                return datas;
            }

            @Override
            public void onStart() {
                Message msg = new Message();
                msg.what = ON_UPLOAD_START;
                handler.sendMessage(msg);
            }

            @Override
            public void onDone(boolean success) {
                Message msg = new Message();
                msg.what = ON_UPLOAD_DONE;
                if(success)
                    msg.arg1 = 1;
                else
                    msg.arg1 = 0;
                handler.sendMessage(msg);
            }
        };
        new PostSingle(config.getUploadVedioUrl(), event).start();

    }
    private String getTagsString(){
        StringBuilder builder = new StringBuilder();
        int tagSize = tagDatas.size();
        int lastIndex = tagSize-1;
        for(int i=0;i<tagSize;i++){
            builder.append(tagDatas.get(i).getId());
            if(i!=lastIndex){
                builder.append(",");
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
    private Tag getTag(String name){
        Tag tag = new Tag(name);
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
                tagDatas = data.getParcelableArrayListExtra("tagDatas");
                Log.e(TAG, "on result:"+tagDatas);
                mTagView.removeAllTags();
                for(int i=0;i<tagDatas.size();i++){
                    mTagView.addTag(getTag(tagDatas.get(i).getName()));
                }
                break;
        }
    }

    private void stop(){
        mediaPlayer.stop();
        Intent intent = new Intent(this,VideoRecordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "onCompletion");
        imagePlay.setVisibility(View.VISIBLE);
    }
}