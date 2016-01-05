package com.lessask.lesson;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.hedgehog.ratingbar.RatingBar;
import com.lessask.DividerItemDecoration;
import com.lessask.OnItemMenuClickListener;
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
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.OnStartDragListener;
import com.lessask.recyclerview.SimpleItemTouchHelperCallback;
import com.lessask.util.ArrayUtil;
import com.lessask.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditLessonActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, OnStartDragListener {
    private String TAG = EditLessonActivity.class.getSimpleName();

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
    private Lesson lesson;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private final int SELECT_ACTION = 1;
    private File mCoverFile;
    private boolean isChangeCover = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lesson);

        mIntent = getIntent();
        lesson = mIntent.getParcelableExtra("lesson");
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
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mCover,R.drawable.man, R.drawable.women);
        VolleyHelper.getInstance().getImageLoader().get(config.getImgUrl() + lesson.getCover(), listener);

        mName = (EditText)findViewById(R.id.name);
        mName.setText(lesson.getName());
        mPurpose  = (EditText)findViewById(R.id.purpose);
        mPurpose.setText(lesson.getPurpose());
        mBodies = (EditText)findViewById(R.id.bodies);
        mBodies.setText(ArrayUtil.join(lesson.getBodies(), " "));
        mAddress = (EditText)findViewById(R.id.address);
        mAddress.setText(lesson.getAddress());
        mCosttime = (EditText)findViewById(R.id.costtime);
        mCosttime.setText(lesson.getCostTime()+"分钟");
        mDescription = (EditText)findViewById(R.id.description);
        mDescription.setText(lesson.getDescription());
        mRecycleTimes = (EditText)findViewById(R.id.recycle_times);
        mRecycleTimes.setText(lesson.getRecycleTimes() + "次");

        mFatBar = (RatingBar)findViewById(R.id.fat_start);
        mFatBar.setStar(lesson.getFatEffect());
        mFatBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(int RatingCount) {
                lesson.setFatEffect(RatingCount);
            }
        });
        mMuscleBar = (RatingBar)findViewById(R.id.muscle_start);
        mMuscleBar.setStar(lesson.getMuscleEffect());
        mMuscleBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(int RatingCount) {
                lesson.setMuscleEffect(RatingCount);
            }
        });

        findViewById(R.id.add).setOnClickListener(this);
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
        //添加动作信息
        mAdapter.appendToList(lesson.getLessonActionInfos());

        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(mAdapter);
        //callback.setmSwipeFlag(ItemTouchHelper.LEFT);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mActionsRecycleView);
    }

    private String[] purposeValues = {"增肌", "减脂", "塑形"};

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        StringPickerDialog stringPickerDialog;
        if(event.getAction()== MotionEvent.ACTION_UP) {
            int pos;
            switch (v.getId()) {
                case R.id.purpose:
                    StringPickerDialog dialog = new StringPickerDialog(EditLessonActivity.this, purposeValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mPurpose.setText(data);
                            lesson.setPurpose(data);
                        }
                    });
                    dialog.setEditable(false);
                    List<String> purpose = Arrays.asList(purposeValues);
                    pos = purpose.indexOf(lesson.getPurpose());
                    if(pos==-1)
                        pos=0;
                    dialog.setValue(pos);
                    dialog.show();
                    break;
                case R.id.bodies:
                    String[] values1 = {"胸部", "背部", "腰部", "臀部", "大腿", "小腿"};
                    ArrayList<String> bodiesValues = new ArrayList<>();
                    for (int i = 0; i < values1.length; i++)
                        bodiesValues.add(values1[i]);
                    TagsPickerDialog dialog1 = new TagsPickerDialog(EditLessonActivity.this, bodiesValues, new TagsPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(List data) {
                            String resulte = "";
                            int size = data.size();
                            int last = size-1;
                            List<String> bodies = new ArrayList<>();
                            for (int i = 0; i < size; i++) {
                                if(i!=last)
                                    resulte += data.get(i)+" ";
                                else
                                    resulte += data.get(i);
                                bodies.add((String)data.get(i));
                            }
                            mBodies.setText(resulte);
                            lesson.setBodies(bodies);
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
                    StringPickerDialog addressDialog = new StringPickerDialog(EditLessonActivity.this, addressValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mAddress.setText(data);
                            lesson.setAddress(data);
                        }
                    });
                    List<String> address = Arrays.asList(addressValues);
                    pos = address.indexOf(lesson.getAddress());
                    if(pos==-1)
                        pos=0;
                    addressDialog.setValue(pos);
                    addressDialog.setEditable(false);
                    addressDialog.show();
                    break;
                case R.id.costtime:
                    ArrayList<String> costtimeValues = new ArrayList<>();
                    for (int i = 1; i < 91; i++)
                        costtimeValues.add(i + "分钟");
                    StringPickerDialog costtimeDialog = new StringPickerDialog(EditLessonActivity.this, costtimeValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mCosttime.setText(data);
                            lesson.setCostTime(Integer.parseInt(data.replace("分钟","")));
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
                    stringPickerDialog = new StringPickerDialog(EditLessonActivity.this, actionRecycleTimesValues, new StringPickerDialog.OnSelectListener() {
                        @Override
                        public void onSelect(String data) {
                            mRecycleTimes.setText(data);
                            lesson.setRecycleTimes(Integer.parseInt(data.replace("次","")));
                        }
                    });
                    stringPickerDialog.setEditable(false);
                    pos = lesson.getRecycleTimes()-1;
                    if (pos<0 || pos>actionRecycleTimesValues.size())
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
                String name = mName.getText().toString().trim();
                if(name.length()==0) {
                    Toast.makeText(getBaseContext(), "请填写课程名", Toast.LENGTH_SHORT).show();
                    return;
                }
                lesson.setName(name);
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
                lesson.setDescription(description);

                ArrayList<Integer> actionsId = getSelectedActionsId();
                if(actionsId.size()==0) {
                    Toast.makeText(getBaseContext(), "请选择课程动作", Toast.LENGTH_SHORT).show();
                    return;
                }

                final LoadingDialog loadingDialog = new LoadingDialog(EditLessonActivity.this);
                NetworkFileHelper.getInstance().startPost(config.getUpdateLessonUrl(), HandleLessonResponse.class, new NetworkFileHelper.PostFileRequest() {
                    @Override
                    public void onStart() {
                        loadingDialog.show();
                    }

                    @Override
                    public void onResponse(Object response) {
                        loadingDialog.cancel();
                        HandleLessonResponse handleLessonResponse = (HandleLessonResponse) response;
                        int lessonId = handleLessonResponse.getId();
                        if(isChangeCover)
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
                        lesson.setLessonActionInfos(mAdapter.getList());
                        headers.put("lesson", gson.toJson(lesson));
                        return headers;
                    }

                    @Override
                    public Map<String, String> getFiles() {
                        return null;
                    }

                    @Override
                    public Map<String, String> getImages() {
                        if(isChangeCover) {
                            Map<String, String> images = new HashMap<String, String>();
                            if (mCoverFile.exists())
                                images.put("cover", mCoverFile.getAbsolutePath());
                            return images;
                        }else {
                            return null;
                        }
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
                    ArrayList<Integer> selectedActionsId = data.getIntegerArrayListExtra("selected");
                    Log.e(TAG, "selectAction:" + selectedActionsId.size());

                    int maxItemCount = mAdapter.getItemCount();
                    mAdapter.clear();
                    List<LessonActionInfo> lessonActionInfos = new ArrayList<>();
                    for (int i=0;i<selectedActionsId.size();i++){
                        int actionId = selectedActionsId.get(i);
                        LessonActionInfo info = new LessonActionInfo(actionId,3,10,60,120);
                        lessonActionInfos.add(info);
                    }
                    lesson.setLessonActionInfos(lessonActionInfos);


                    mAdapter.appendToList(lessonActionInfos);
                    if(maxItemCount<lessonActionInfos.size())
                        maxItemCount = lessonActionInfos.size();
                    mAdapter.notifyItemRangeChanged(0,maxItemCount);
                    break;
                case 100:
                    //bmp = intent.getParcelableExtra("data");
                    Log.e(TAG, "从相册选取");
                    Bitmap bmp = ImageUtil.getBitmapFromFile(mCoverFile);//decodeUriAsBitmap(headImgUri, null);
                    mCover.setImageBitmap(bmp);
                    isChangeCover = true;
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
                    bmp = ImageUtil.getOptimizeBitmapFromFile(mCoverFile);
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
