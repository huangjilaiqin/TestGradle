package com.lessask.lesson;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.dialog.TagsPickerDialog;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.ItemTouchHelperAdapter;
import com.lessask.recyclerview.ItemTouchHelperViewHolder;
import com.lessask.recyclerview.OnStartDragListener;
import com.lessask.recyclerview.RecyclerViewDragHolder;
import com.lessask.recyclerview.SimpleItemTouchHelperCallback;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateLessonActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, OnStartDragListener{
    private String TAG = CreateLessonActivity.class.getSimpleName();

    private EditText mName;
    private ImageView mCover;
    private TextView mPurpose;
    private TextView mBodies;
    private TextView mAddress;
    private TextView mCosttime;
    private RatingBar mFatBar;
    private RatingBar mMuscleBar;

    private RecyclerView mActionsRecycleView;
    private LessonActionsAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private int SELECT_ACTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

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
        mCosttime.setClickable(true);

        findViewById(R.id.save).setOnClickListener(this);
        mCover.setOnClickListener(this);
        mPurpose.setOnTouchListener(this);
        mBodies.setOnTouchListener(this);
        mAddress.setOnTouchListener(this);
        mCosttime.setOnTouchListener(this);

        mActionsRecycleView = (RecyclerView) findViewById(R.id.actions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mActionsRecycleView.setLayoutManager(linearLayoutManager);
        mActionsRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new LessonActionsAdapter(this, this);
        mAdapter.appendToList(getData());
        mActionsRecycleView.setAdapter(mAdapter);

        //ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(mAdapter);
        callback.setmSwipeFlag(ItemTouchHelper.LEFT);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mActionsRecycleView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
                    int pos = costtimeValues.indexOf(mCosttime.getText().toString().trim());
                    if (pos == -1) {
                        costtimeDialog.setValue(29);
                    } else {
                        costtimeDialog.setValue(pos);
                    }
                    costtimeDialog.show();
                    Log.e(TAG, "costtime");
                    break;
                case R.id.cover:
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.save:
                break;
            case R.id.cover:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_ACTION){
            if(resultCode == RESULT_OK){

            }
        }
    }

    private ArrayList<LessonActionInfo> getData(){
        ArrayList<LessonActionInfo> datas = new ArrayList<>();
        for(int i=0;i<20;i++){
            datas.add(new LessonActionInfo(i,"深蹲"+i,"1,jpg",3,15,60));
        }
        return  datas;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
