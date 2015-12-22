package com.lessask.lesson;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;
import com.lessask.R;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.dialog.TagsPickerDialog;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.ItemTouchHelperAdapter;
import com.lessask.recyclerview.ItemTouchHelperViewHolder;
import com.lessask.recyclerview.RecyclerViewDragHolder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateLessonActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
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
    private ArrayList<LessonActionInfo> datas;

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
        mAdapter = new LessonActionsAdapter(this);
        mAdapter.appendToList(getData());
        mActionsRecycleView.setAdapter(mAdapter);
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
            datas.add(new LessonActionInfo("深蹲"+i, 15, 3, 60));
        }
        return  datas;
    }

    class ActionListAdapter extends BaseRecyclerAdapter<LessonActionInfo, RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
        private Context context;
        private ArrayList<LessonActionInfo> datas;

        ActionListAdapter(Context context, ArrayList<LessonActionInfo> datas){
            this.context = context;
            this.datas = datas;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //获取背景菜单
            //这个控件的match_parent的大小就是item大小,如果item边缘使用margin会显示bg_menu的底色
            View mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_lesson_action_menu, null);
            mybg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            //获取item布局
            //item布局要有两层layout在第二层layout里面放控件,背景色设置为不透明，否则bg_menu会显示出来
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_lesson_action_item, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            //生成返回RecyclerView.ViewHolder
            return new MyHolder(context, mybg, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            List list = getList();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(list, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(list, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            getList().remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
