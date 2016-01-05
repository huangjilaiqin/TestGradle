package com.lessask.lesson;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hedgehog.ratingbar.RatingBar;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.action.SelectActionActivity;
import com.lessask.dialog.LoadingDialog;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.dialog.TagsPickerDialog;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.HandleLessonResponse;
import com.lessask.model.Lesson;
import com.lessask.net.NetworkFileHelper;
import com.lessask.recyclerview.OnStartDragListener;
import com.lessask.recyclerview.SimpleItemTouchHelperCallback;
import com.lessask.util.ImageUtil;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CreateLessonActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, OnStartDragListener{
    private String TAG = CreateLessonActivity.class.getSimpleName();

    private EditText mName;
    private ImageView mCover;
    private EditText mPurpose;
    private EditText mBodies;
    private EditText mAddress;
    private EditText mCosttime;
    private EditText mRecycleTimes;
    private RatingBar mFatBar;
    private RatingBar mMuscleBar;
    private EditText mDescription;

    private RecyclerView mActionsRecycleView;
    private LessonActionsAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Intent mIntent;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private final int SELECT_ACTION = 1;
    private File mCoverFile;
    private boolean isSelectedCover;

    private int fatEffect=1;
    private int muscleEffect=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        mIntent = getIntent();
        mCoverFile = new File(getExternalCacheDir(), "cover.jpg");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCover = (ImageView)findViewById(R.id.cover);
        mName = (EditText)findViewById(R.id.name);
        mPurpose  = (EditText)findViewById(R.id.purpose);
        mBodies = (EditText)findViewById(R.id.bodies);
        mAddress = (EditText)findViewById(R.id.address);
        mCosttime = (EditText)findViewById(R.id.costtime);
        mDescription = (EditText)findViewById(R.id.description);
        mRecycleTimes = (EditText)findViewById(R.id.recycle_times);
        findViewById(R.id.add).setOnClickListener(this);
        mFatBar = (RatingBar)findViewById(R.id.fat_start);
        mFatBar.setStar(fatEffect);
        mFatBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(int RatingCount) {
                fatEffect = RatingCount;
            }
        });
        mMuscleBar = (RatingBar)findViewById(R.id.muscle_start);
        mMuscleBar.setStar(muscleEffect);
        mMuscleBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(int RatingCount) {
                muscleEffect = RatingCount;
            }
        });

        findViewById(R.id.save).setOnClickListener(this);
        mCover.setOnClickListener(this);
        mPurpose.setOnTouchListener(this);
        mBodies.setOnTouchListener(this);
        mAddress.setOnTouchListener(this);
        mCosttime.setOnTouchListener(this);
        mRecycleTimes .setOnTouchListener(this);

        mActionsRecycleView = (RecyclerView) findViewById(R.id.actions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mActionsRecycleView.setLayoutManager(linearLayoutManager);
        mActionsRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mAdapter = new LessonActionsAdapter(this, this,coordinatorLayout );
        mActionsRecycleView.setAdapter(mAdapter);

        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(mAdapter);
        //callback.setmSwipeFlag(ItemTouchHelper.LEFT);
        //callback.setmSwipeFlag(0);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mActionsRecycleView);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        StringPickerDialog stringPickerDialog;
        if(event.getAction()==MotionEvent.ACTION_UP) {
            int pos;
            switch (v.getId()) {
                case R.id.purpose:
                    String[] purposeValues = {"增肌", "减脂", "塑形"};
                    StringPickerDialog dialog = new StringPickerDialog(CreateLessonActivity.this, purposeValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mPurpose.setText(data);
                            Log.e(TAG, "select" + data);
                        }
                    });
                    dialog.setEditable(false);
                    String purposeStr = mPurpose.getText().toString().trim();
                    if(purposeStr.length()>0){
                        List<String> purpose = Arrays.asList(purposeValues);
                        pos = purpose.indexOf(purposeStr);
                        if(pos==-1)
                            pos=0;
                        dialog.setValue(pos);
                    }
                    dialog.show();
                    Log.e(TAG, "purpose");
                    break;
                case R.id.bodies:
                    String[] values1 = {"胸部", "背部", "腰部", "臀部", "大腿", "小腿"};
                    ArrayList<String> bodiesValues = new ArrayList<>();
                    for (int i = 0; i < values1.length; i++)
                        bodiesValues.add(values1[i]);
                    TagsPickerDialog dialog1 = new TagsPickerDialog(CreateLessonActivity.this, bodiesValues, new TagsPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(List data) {
                            String resulte = "";
                            int size = data.size();
                            int last = size-1;
                            for (int i = 0; i < size; i++) {
                                if(i!=last)
                                    resulte += data.get(i)+" ";
                                else
                                    resulte += data.get(i);
                            }
                            mBodies.setText(resulte);
                        }
                    });
                    String content = mBodies.getText().toString().trim();
                    ArrayList<Integer> selected = new ArrayList<>();
                    if(content!=null && content.length()>0){
                        String[] values = content.split(" ");
                        for (String name:values){
                            pos = bodiesValues.indexOf(name);
                            if(pos<0 || pos>=bodiesValues.size())
                                continue;
                            selected.add(pos);
                        }
                    }
                    int[] selectedIndex = new int[selected.size()];
                    for(int i=0;i<selected.size();i++)
                        selectedIndex[i] = selected.get(i);
                    dialog1.setSelectedList(selectedIndex, 2);
                    dialog1.show();
                    break;
                case R.id.address:
                    String[] addressValues = {"健身房", "家里", "公园"};
                    StringPickerDialog addressDialog = new StringPickerDialog(CreateLessonActivity.this, addressValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mAddress.setText(data);
                        }
                    });
                    String addressStr = mAddress.getText().toString().trim();
                    if(addressStr.length()>0){
                        List<String> address = Arrays.asList(addressStr);
                        pos = address.indexOf(addressStr);
                        if(pos==-1)
                            pos=0;
                        addressDialog.setValue(pos);
                    }
                    addressDialog.setEditable(false);
                    addressDialog.show();
                    break;
                case R.id.costtime:
                    ArrayList<String> costtimeValues = new ArrayList<>();
                    for (int i = 1; i < 91; i++)
                        costtimeValues.add(i + "分钟");
                    StringPickerDialog costtimeDialog = new StringPickerDialog(CreateLessonActivity.this, costtimeValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mCosttime.setText(data);
                        }
                    });
                    costtimeDialog.setEditable(false);
                    pos = costtimeValues.indexOf(mCosttime.getText().toString().trim());
                    if (pos == -1) {
                        costtimeDialog.setValue(29);
                    } else {
                        costtimeDialog.setValue(pos);
                    }
                    costtimeDialog.show();
                    break;
                case R.id.recycle_times:
                    ArrayList<String> actionRecycleTimesValues = new ArrayList<>();
                    for (int i = 1; i < 50; i++)
                        actionRecycleTimesValues.add(i + "次");
                    stringPickerDialog = new StringPickerDialog(CreateLessonActivity.this, actionRecycleTimesValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mRecycleTimes.setText(data);
                        }
                    });
                    stringPickerDialog.setEditable(false);
                    pos = actionRecycleTimesValues.indexOf(mRecycleTimes.getText().toString().trim());
                    if (pos == -1)
                        pos=9;
                    stringPickerDialog.setValue(pos);
                    stringPickerDialog.show();
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.save:
                if(!isSelectedCover){
                    Toast.makeText(getBaseContext(), "请选择计划封面", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = mName.getText().toString().trim();
                if(name.length()==0) {
                    Toast.makeText(getBaseContext(), "请填写课程名", Toast.LENGTH_SHORT).show();
                    return;
                }
                String purpose = mPurpose.getText().toString().trim();
                if(purpose.length()==0) {
                    Toast.makeText(getBaseContext(), "请选择训练目的", Toast.LENGTH_SHORT).show();
                    return;
                }
                String bodiesStr = mBodies.getText().toString().trim();
                if(bodiesStr.length()==0){
                    Toast.makeText(getBaseContext(), "请选择训练部位", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> bodies = Arrays.asList(bodiesStr.split(" "));

                String address = mAddress.getText().toString().trim();
                if(address.length()==0) {
                    Toast.makeText(getBaseContext(), "请选择训练地点", Toast.LENGTH_SHORT).show();
                    return;
                }

                String costTimeStr = mCosttime.getText().toString().trim();
                if(costTimeStr.length()==0) {
                    Toast.makeText(getBaseContext(), "请选择训练耗时", Toast.LENGTH_SHORT).show();
                    return;
                }
                int costTime = Integer.parseInt(costTimeStr.replace("分钟", ""));

                String description = mDescription.getText().toString().trim();
                if(description.length()==0) {
                    Toast.makeText(getBaseContext(), "请填写课程描述", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mAdapter.getList().size()==0) {
                    Toast.makeText(getBaseContext(), "请选择课程动作", Toast.LENGTH_SHORT).show();
                    return;
                }

                int recycleTimes = Integer.parseInt(mRecycleTimes.getText().toString().trim().replace("次",""));

                final Lesson lesson = new Lesson(-1,name,"",bodies,address,purpose,costTime,description,mAdapter.getList(),recycleTimes,fatEffect,muscleEffect);
                final LoadingDialog loadingDialog = new LoadingDialog(CreateLessonActivity.this);

                NetworkFileHelper.getInstance().startPost(config.getAddLessonUrl(), HandleLessonResponse.class, new NetworkFileHelper.PostFileRequest() {
                    @Override
                    public void onStart() {
                        loadingDialog.show();
                    }

                    @Override
                    public void onResponse(Object response) {
                        loadingDialog.cancel();
                        HandleLessonResponse handleLessonResponse = (HandleLessonResponse) response;
                        int lessonId = handleLessonResponse.getId();
                        lesson.setId(lessonId);
                        lesson.setCover(handleLessonResponse.getCover());
                        mIntent.putExtra("lesson", lesson);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        loadingDialog.cancel();
                        Toast.makeText(getBaseContext(), "保存课程失败:"+error.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("userId", "" + globalInfos.getUserId());
                        String gsonStr = gson.toJson(lesson);
                        Log.e(TAG, gsonStr.length() + ", " + gsonStr);
                        headers.put("lesson", gson.toJson(lesson));
                        return headers;
                    }

                    @Override
                    public Map<String, String> getFiles() {
                        Map<String, String> images = new HashMap<String, String>();
                        if(mCoverFile.exists())
                            images.put("cover", mCoverFile.getAbsolutePath());
                        return images;
                    }

                    @Override
                    public Map<String, String> getImages() {
                        return null;
                    }
                });

                break;
            case R.id.cover:
                getCover();
                break;
            case R.id.add:
                intent = new Intent(this, SelectActionActivity.class);
                intent.putIntegerArrayListExtra("selected", getSelectedActionsId());
                startActivityForResult(intent, SELECT_ACTION);
                break;
        }
    }

    private void getCover(){
        getImageFromPick();
        //相机获取图片有翻转问题
        /*
        final Uri headImgUri = Uri.fromFile(mCoverFile);//获取文件的Uri
        final int outputX = 120;
        final int outputY = 180;
        new AlertDialog.Builder(CreateLessonActivity.this).setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //不能同时设置输出到文件中 和 从data中返回
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, headImgUri);
                    //intent.putExtra("return-data", true);
                    startActivityForResult(intent, 101);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    intent.putExtra("output", headImgUri);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);// 裁剪框比例
                    intent.putExtra("aspectY", 1);
                    //intent.putExtra("outputX", outputX);// 输出图片大小
                    //intent.putExtra("outputY", outputY);
                    //intent.putExtra("return-data", true);
                    startActivityForResult(intent, 100);
                }
            }
        }).create().show();
        */
    }

    private void getImageFromPick(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", Uri.fromFile(mCoverFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        startActivityForResult(intent, 100);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Intent intent = null;
            switch (requestCode){
                case SELECT_ACTION:
                    ArrayList<Integer> modifyOldSelectedActionsId = data.getIntegerArrayListExtra("old_selected");
                    ArrayList<Integer> newSelectedActionsId = data.getIntegerArrayListExtra("new_selected");
                    ArrayList<Integer> oldSelectedActionsId = getSelectedActionsId();

                    Iterator<Integer> oldIterator = oldSelectedActionsId.iterator();
                    List<LessonActionInfo> lessonActionInfos = mAdapter.getList();
                    while (oldIterator.hasNext()){
                        int actionId = oldIterator.next();
                        //该动作被删除了
                        if(!modifyOldSelectedActionsId.contains(new Integer(actionId))){
                            //遍历找到移除
                            for(int i=0;i<lessonActionInfos.size();i++){
                                LessonActionInfo info = lessonActionInfos.get(i);
                                if(info.getActionId()==actionId){
                                    lessonActionInfos.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    Iterator<Integer> newIterator = newSelectedActionsId.iterator();
                    while (newIterator.hasNext()){
                        int actionId = newIterator.next();
                        lessonActionInfos.add(new LessonActionInfo(actionId,1,10,60,120));
                    }

                    int maxItemCount = lessonActionInfos.size();
                    mAdapter.notifyItemRangeChanged(0,maxItemCount);
                    break;
                case 100:
                    //bmp = intent.getParcelableExtra("data");
                    Log.e(TAG, "从相册选取");
                    //to do 图片压缩
                    //Bitmap bmp = Utils.getBitmapFromFile(mCoverFile);//decodeUriAsBitmap(headImgUri, null);
                    Log.e(TAG, "cover w:"+mCover.getWidth()+", h:"+mCover.getHeight());
                    Bitmap bmp = ImageUtil.getOptimizeBitmapFromFile(mCoverFile,mCover.getWidth(),mCover.getHeight());
                    try {
                        Log.e(TAG, "before optmize cover size:"+mCoverFile.length()/1024.0);
                        ImageUtil.setBitmap2File(mCoverFile, bmp);
                        Log.e(TAG, "optmize cover size:"+mCoverFile.length()/1024.0);
                    }catch (IOException e){
                        Toast.makeText(CreateLessonActivity.this, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isSelectedCover=true;
                    mCover.setImageBitmap(bmp);
                    break;
                case 101:
                    Log.e(TAG, "从相机选取");
                    /*从uri中获取需要自己剪裁
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outHeight = 180;
                    options.outWidth = 180;
                    bmp = decodeUriAsBitmap(headImgUri, options);
                    //*/
                    if(mCoverFile.isFile() && mCoverFile.exists())
                        Log.e(TAG, mCoverFile.toString()+" is exit");
                    bmp = ImageUtil.getOptimizeBitmapFromFile(mCoverFile,mCover.getWidth(),mCover.getHeight());
                    try {
                        Log.e(TAG, "before optmize cover size:"+mCoverFile.length()/1024.0);
                        ImageUtil.setBitmap2File(mCoverFile, bmp);
                        Log.e(TAG, "optmize cover size:"+mCoverFile.length()/1024.0);
                    }catch (IOException e){
                        Toast.makeText(CreateLessonActivity.this, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.e(TAG, "count:" + bmp.getByteCount());

                    //bmp = intent.getParcelableExtra("data");
                    mCover.setImageBitmap(bmp);
                    break;
            }
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private ArrayList<Integer> getSelectedActionsId(){
        //获取选中的动作id
        ArrayList<Integer> selectedActionsId = new ArrayList<>();
        List<LessonActionInfo> datas = mAdapter.getList();
        for(int i=0;i<datas.size();i++)
            selectedActionsId.add(datas.get(i).getActionId());
        return selectedActionsId;
    }
}
