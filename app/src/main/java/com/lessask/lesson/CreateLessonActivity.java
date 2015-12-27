package com.lessask.lesson;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
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
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.action.SelectActionActivity;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.dialog.TagsPickerDialog;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.model.Lesson;
import com.lessask.recyclerview.OnStartDragListener;
import com.lessask.recyclerview.SimpleItemTouchHelperCallback;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private GlobalInfos globalInfos = GlobalInfos.getInstance();

    private RecyclerView mActionsRecycleView;
    private LessonActionsAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Intent mIntent;
    private ArrayList<Integer> selectedActionsId;

    private final int SELECT_ACTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        mIntent = getIntent();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectedActionsId = new ArrayList<>();

        mCover = (ImageView)findViewById(R.id.cover);
        mName = (EditText)findViewById(R.id.name);
        mPurpose  = (EditText)findViewById(R.id.purpose);
        mBodies = (EditText)findViewById(R.id.bodies);
        mAddress = (EditText)findViewById(R.id.address);
        mCosttime = (EditText)findViewById(R.id.costtime);
        mRecycleTimes = (EditText)findViewById(R.id.recycle_times);
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

        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(mAdapter);
        callback.setmSwipeFlag(ItemTouchHelper.LEFT);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mActionsRecycleView);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        StringPickerDialog stringPickerDialog;
        if(event.getAction()==MotionEvent.ACTION_UP) {
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
                    dialog.show();
                    Log.e(TAG, "purpose");
                    break;
                case R.id.bodies:
                    String[] values1 = {"胸部", "背部", "腰部腰部腰部腰部", "臀部", "大腿", "小腿"};
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
                            int pos = bodiesValues.indexOf(name);
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
                    int pos = costtimeValues.indexOf(mCosttime.getText().toString().trim());
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
                    if (pos == -1) {
                        stringPickerDialog.setValue(9);
                    } else {
                        stringPickerDialog.setValue(pos);
                    }
                    stringPickerDialog.show();
                    break;
                case R.id.cover:
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
                ArrayList<String> bodies = (ArrayList)Arrays.asList(mBodies.getText().toString().trim().split(" "));
                String address = mAddress.getText().toString().trim();
                String purpose = mPurpose.getText().toString().trim();
                int costTime = Integer.parseInt(mCosttime.getText().toString().trim());
                String description = mDescription.getText().toString().trim();
                Lesson lesson = new Lesson(-1,name,"",bodies,address,purpose,costTime,description,selectedActionsId);
                mIntent.putExtra("lesson", lesson);
                setResult(RESULT_OK, mIntent);
                finish();
                break;
            case R.id.cover:
                break;
            case R.id.add:
                intent = new Intent(this, SelectActionActivity.class);
                intent.putIntegerArrayListExtra("selected", selectedActionsId);
                startActivityForResult(intent, SELECT_ACTION);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case SELECT_ACTION:
                    selectedActionsId = data.getIntegerArrayListExtra("selected");
                    Log.e(TAG, "selectAction:"+selectedActionsId.size());

                    int maxItemCount = mAdapter.getItemCount();
                    mAdapter.clear();
                    ArrayList<LessonActionInfo> lessonActionInfos = new ArrayList<>();
                    for (int i=0;i<selectedActionsId.size();i++){
                        int actionId = selectedActionsId.get(i);
                        ActionItem actionItem = globalInfos.getActionById(actionId);
                        LessonActionInfo info = new LessonActionInfo(actionId, actionItem.getName(), actionItem.getVideoName(),3,10,60,120);
                        lessonActionInfos.add(info);
                    }
                    Log.e(TAG, "selectAction append:"+lessonActionInfos.size());
                    mAdapter.appendToList(lessonActionInfos);
                    if(maxItemCount<lessonActionInfos.size())
                        maxItemCount = lessonActionInfos.size();
                    mAdapter.notifyItemRangeChanged(0,maxItemCount);
                    break;
            }
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
