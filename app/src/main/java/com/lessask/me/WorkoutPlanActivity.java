package com.lessask.me;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.crud.CRUDExtend;
import com.lessask.crud.DefaultGsonRequestCRUD;
import com.lessask.dialog.LoadingDialog;
import com.lessask.dialog.MenuDialog;
import com.lessask.dialog.OnSelectMenu;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.lesson.SelectLessonActivity;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.Lesson;
import com.lessask.model.Workout;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkoutPlanActivity extends AppCompatActivity implements CRUDExtend<Workout> {

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private String TAG = WorkoutPlanActivity.class.getSimpleName();

    private WorkoutAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private int userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan);

        Intent intent = getIntent();
        userid = intent.getIntExtra("userid", -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("周计划");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                read(mRecyclerView);
            }
        });
        mRecyclerView = (RecyclerViewStatusSupport)findViewById(R.id.workouts);
        mRecyclerView.setStatusViews(findViewById(R.id.loading_view), findViewById(R.id.empty_view), findViewById(R.id.error_view));
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setClickable(true);

        mRecyclerViewAdapter = new WorkoutAdapter(this);

        String[] resetMenu = new String[]{"添加"};
        String[] workoutMenu = new String[]{"休息","更改"};


        mRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public void onItemLongClick(View view, final int position) {
                final Workout workout = mRecyclerViewAdapter.getItem(position);
                final MenuDialog resetMenuDialog = new MenuDialog(WorkoutPlanActivity.this, new String[]{"添加"},
                new OnSelectMenu() {
                    @Override
                    public void onSelectMenu(int menupos) {
                        Toast.makeText(WorkoutPlanActivity.this, "menupos:" + menupos + ", position:" + position, Toast.LENGTH_SHORT).show();
                        Intent intent;
                        switch (menupos){
                            case 0:
                                //选择课程
                                intent = new Intent(WorkoutPlanActivity.this, SelectLessonActivity.class);
                                intent.putExtra("position", position);
                                startActivityForResult(intent, FragmentMe.WORKOUT_ADD);
                                break;
                        }
                    }
                });
                final MenuDialog workoutMenuDialog = new MenuDialog(WorkoutPlanActivity.this, new String[]{"休息","更改训练"},
                new OnSelectMenu() {
                    @Override
                    public void onSelectMenu(int menupos) {
                        Intent intent;
                        switch (menupos){
                            case 0:
                                //休息
                                //deleteWorkout(position,workout);
                                deleteAndAdd(workout,position);
                                //delete(workout,position);
                                break;
                            case 1:
                                //更改课程
                                intent = new Intent(WorkoutPlanActivity.this, SelectLessonActivity.class);
                                startActivityForResult(intent, FragmentMe.WORKOUT_CHANGE);
                                intent.putExtra("position", position);
                                break;
                        }
                    }
                });
                switch (mRecyclerViewAdapter.getItemViewType(position)) {
                    case 0:
                        resetMenuDialog.show();
                        break;
                    case 1:
                        workoutMenuDialog.show();
                        break;
                }
            }
        });
        //查看动作
        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                /*
                Intent intent = new Intent(getActivity(), LessonActivity.class);
                intent.putExtra("lesson", mRecyclerViewAdapter.getItem(position).getLesson());
                startActivity(intent);
                */

            }
        });
        mRecyclerViewAdapter.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemMenuClick(View view, final int position) {
                final Workout workout = mRecyclerViewAdapter.getItem(position);
                switch (view.getId()) {
                    case R.id.reset:
                        Toast.makeText(WorkoutPlanActivity.this, "reset", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.change:
                        Toast.makeText(WorkoutPlanActivity.this, "change", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.add:
                        Toast.makeText(WorkoutPlanActivity.this, "add", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onItemMenuClick(View view, Object obj) {

            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        read(mRecyclerView);
    }



    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            Log.e(TAG, "onActivityResult");
            Workout workout;
            Lesson lesson;
            int position;
            switch (requestCode){
                case FragmentMe.WORKOUT_ADD:
                    position = data.getIntExtra("position", -1);
                    if(position==-1){
                        Toast.makeText(WorkoutPlanActivity.this,"错误的星期",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lesson = data.getParcelableExtra("lesson");
                    workout = new Workout(-1,lesson.getId(),userid,position+1,lesson);
                    create(workout,position);
                    break;
                case FragmentMe.WORKOUT_CHANGE:
                    workout = data.getParcelableExtra("workout");
                    position = data.getIntExtra("position", -1);
                    if(position==-1){
                        Toast.makeText(WorkoutPlanActivity.this,"错误的星期",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    workout = data.getParcelableExtra("workout");
                    workout.setWeek(position+1);
                    //updateWorkout(position,workout);
                    update(workout,position);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addWorkout(final Workout workout, final int position){
        GsonRequest gsonRequest = new GsonRequest<WorkoutResponse>(Request.Method.POST,config.getAddWorkoutUrl(),WorkoutResponse.class,new GsonRequest.PostGsonRequest<WorkoutResponse>(){

            final LoadingDialog loadingDialog = new LoadingDialog(WorkoutPlanActivity.this);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(WorkoutResponse response) {
                loadingDialog.cancel();
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(WorkoutPlanActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    workout.setId(response.getId());
                    mRecyclerViewAdapter.update(position, workout);
                    //mRecyclerViewAdapter.notifyItemUpdate(position);
                }
            }

            @Override
            public void onError(VolleyError error) {
                loadingDialog.cancel();
                Toast.makeText(WorkoutPlanActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid",""+userid);
                datas.put("lessonId", "" + workout.getLessonId());
                datas.put("week", "" + workout.getWeek());
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", userid + "");
                datas.put("lessonId", "" + workout.getLessonId());
                datas.put("week", "" + workout.getWeek());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
    private void deleteWorkout(final int position, final Workout workout){
        GsonRequest gsonRequest = new GsonRequest<WorkoutResponse>(Request.Method.POST,config.getDeleteWorkoutUrl(),WorkoutResponse.class,new GsonRequest.PostGsonRequest<WorkoutResponse>(){

            final LoadingDialog loadingDialog = new LoadingDialog(WorkoutPlanActivity.this);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(WorkoutResponse response) {
                loadingDialog.cancel();
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(WorkoutPlanActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                }else {
                    Workout resetWorkout = new Workout();
                    mRecyclerViewAdapter.update(position,resetWorkout);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(WorkoutPlanActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", ""+userid);
                datas.put("id", ""+workout.getId());
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", userid + "");
                datas.put("id", ""+workout.getId());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
    private void updateWorkout(final int position,final Workout workout){
        GsonRequest gsonRequest = new GsonRequest<WorkoutResponse>(Request.Method.POST,config.getUpdateWorkoutUrl(),WorkoutResponse.class,new GsonRequest.PostGsonRequest<WorkoutResponse>(){

            final LoadingDialog loadingDialog = new LoadingDialog(WorkoutPlanActivity.this);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(WorkoutResponse response) {
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(WorkoutPlanActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, response.getError());
                }else {
                    mRecyclerViewAdapter.update(position,workout);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(WorkoutPlanActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", ""+userid);
                datas.put("lessonId", ""+workout.getLessonId());
                datas.put("week", ""+workout.getWeek());
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", userid + "");
                datas.put("lessonId", ""+workout.getLessonId());
                datas.put("week", ""+workout.getWeek());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

    @Override
    public void deleteAndAdd(final Workout obj, final int position) {
        GsonRequest gsonRequest = new GsonRequest<WorkoutResponse>(Request.Method.POST,config.getDeleteWorkoutUrl(),WorkoutResponse.class,new GsonRequest.PostGsonRequest<WorkoutResponse>(){

            final LoadingDialog loadingDialog = new LoadingDialog(WorkoutPlanActivity.this);
            @Override
            public void onStart() {
                loadingDialog.show();
            }

            @Override
            public void onResponse(WorkoutResponse response) {
                loadingDialog.cancel();
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(WorkoutPlanActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                }else {
                    Workout resetWorkout = new Workout();
                    mRecyclerViewAdapter.update(position,resetWorkout);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(WorkoutPlanActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", ""+userid);
                datas.put("id", "" + obj.getId());
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", userid + "");
                datas.put("id", ""+obj.getId());
                return datas;
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);

    }

    @Override
    public void create(Workout obj, int position) {
        DefaultGsonRequestCRUD  crud = new DefaultGsonRequestCRUD();
        Type type = new TypeToken<Workout>() {}.getType();
        Map datas = new HashMap();
        datas.put("userid", userid + "");
        datas.put("lessonId", "" + obj.getLessonId());
        datas.put("week", "" + obj.getWeek());
        crud.create(WorkoutPlanActivity.this,mRecyclerViewAdapter,config.getAddWorkoutUrl(),type,datas,position);
    }

    @Override
    public void read(RecyclerViewStatusSupport recyclerView) {
        Type type = new TypeToken<ArrayListResponse<Workout>>() {}.getType();
        GsonRequest gsonRequest = new GsonRequest<ArrayListResponse<Workout>>(Request.Method.POST,config.getWorkoutsUrl(),type,new GsonRequest.PostGsonRequest<ArrayListResponse<Workout>>(){
            @Override
            public void onStart() {
                mRecyclerView.showLoadingView();
            }

            @Override
            public void onResponse(ArrayListResponse<Workout> response) {
                if(response.getError()!=null || response.getErrno()!=0){
                    mRecyclerView.showErrorView(response.getError());
                    Toast.makeText(WorkoutPlanActivity.this, response.getError(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, response.getError());
                }else {
                    ArrayList<Workout> showWorkouts = new ArrayList<>();
                    ArrayList<Workout> workouts = response.getDatas();
                    Log.e(TAG, "workouts size:"+workouts.size());
                    int currentWeek = 1;
                    Workout workout;
                    if(workouts.size()!=7) {
                        for (int i=0;i<workouts.size();i++){
                            workout = workouts.get(i);
                            int week = workout.getWeek();
                            Log.e(TAG, "week: "+week);
                            while (currentWeek<week){
                                Workout resetWorkout = new Workout();
                                resetWorkout.setWeek(currentWeek);
                                showWorkouts.add(resetWorkout);
                                Log.e(TAG, "reset week: "+currentWeek);
                                currentWeek++;
                            }
                            currentWeek=week+1;
                            showWorkouts.add(workout);
                        }
                        while (currentWeek<8){
                            Workout resetWorkout = new Workout();
                            resetWorkout.setWeek(currentWeek);
                            showWorkouts.add(resetWorkout);
                            currentWeek++;
                        }
                        mRecyclerViewAdapter.appendToList(showWorkouts);
                    }else {
                        mRecyclerViewAdapter.appendToList(workouts);
                    }
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                mRecyclerView.showErrorView(error.getMessage());
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", userid + "");
                return datas;
            }
            @Override
            public void setPostData(Map datas) {
                datas.put("userid", ""+userid);
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);

    }

    @Override
    public void update(Workout obj, int position) {
        DefaultGsonRequestCRUD  crud = new DefaultGsonRequestCRUD();
        Type type = new TypeToken<Workout>() {}.getType();
        Map datas = new HashMap();
        datas.put("userid", ""+userid);
        datas.put("lessonId", ""+obj.getLessonId());
        datas.put("week", ""+obj.getWeek());
        crud.update(WorkoutPlanActivity.this, mRecyclerViewAdapter, config.getAddWorkoutUrl(), type, datas, position);
    }

    @Override
    public void delete(Workout obj,int position) {
        DefaultGsonRequestCRUD  crud = new DefaultGsonRequestCRUD();
        Type type = new TypeToken<Workout>() {}.getType();
        Map datas = new HashMap();
        datas.put("userid", userid + "");
        datas.put("lessonId", "" + obj.getId());
        crud.delete(WorkoutPlanActivity.this, mRecyclerViewAdapter, config.getAddWorkoutUrl(), type, datas, position);

    }
}
