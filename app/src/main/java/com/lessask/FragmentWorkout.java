package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lessask.custom.RatingBar;
import com.lessask.custom.RoundProgressBar;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.Lesson;
import com.lessask.model.Workout;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.test.CoordinatorLayoutActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHuang on 2015/10/22.
 */
public class FragmentWorkout extends Fragment {
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();

    private String TAG = FragmentWorkout.class.getSimpleName();

    private View rootView;

    private final int GET_WORKOUT=1;

    private RatingBar mFatBar;
    private RatingBar mMuscleBar;
    private TextView lessonName;
    private ImageView lessonImage;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_WORKOUT:
                    Workout workout = (Workout)msg.obj;
                    Lesson lesson = workout.getLesson();
                    if(lesson!=null){
                        lessonName.setText(lesson.getName());
                        mFatBar.setStar(lesson.getFatEffect());
                        mMuscleBar.setStar(lesson.getMuscleEffect());
                        String imgUrl = imageUrlPrefix+lesson.getCover();
                        //Log.e(TAG, "other :"+headImgUrl);
                        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(lessonImage, 0, 0);
                        VolleyHelper.getInstance().getImageLoader().get(imgUrl, headImgListener, 0, 0);
                    }else {
                        lessonName.setText("休息日");
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "FragmentWorkout onCreateView");
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_workout, null);
            Log.e(TAG, "FragmentWorkout");
            /*
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CoordinatorLayoutActivity.class);
                    startActivity(intent);
                }
            });
            */
            lessonName = (TextView)rootView.findViewById(R.id.lesson_name);
            lessonImage = (ImageView)rootView.findViewById(R.id.lesson_image);
            /*
            RoundProgressBar personalCircle = (RoundProgressBar)rootView.findViewById(R.id.personal_circle);
            personalCircle.setProgress(65);
            RoundProgressBar joinCircle = (RoundProgressBar)rootView.findViewById(R.id.join_circle);
            joinCircle.setProgress(65);
            */

            mFatBar = (RatingBar)rootView.findViewById(R.id.fat_star);
            mFatBar.setmClickable(false);

            mMuscleBar = (RatingBar)rootView.findViewById(R.id.muscle_star);
            mMuscleBar.setmClickable(false);

        }
        loadWorkout();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            Log.e(TAG, "onActivityResult");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadWorkout(){
        GsonRequest gsonRequest = new GsonRequest<Workout>(Request.Method.POST,config.getWorkoutUrl(),Workout.class,new GsonRequest.PostGsonRequest<Workout>(){
            @Override
            public void onStart() {
                //加载提示
            }

            @Override
            public void onResponse(Workout response) {
                if(response.getError()!=null || response.getErrno()!=0){
                    Toast.makeText(getContext(), response.getError(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, response.getError());
                }else {
                    if(response.getErrno()!=0 || response.getError()!=null){
                        Toast.makeText(getContext(), response.getError(), Toast.LENGTH_SHORT).show();
                    }else {
                        Message msg = new Message();
                        msg.what = GET_WORKOUT;
                        msg.obj = response;
                        handler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", globalInfos.getUserId()+ "");
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                int week = calendar.get(Calendar.DAY_OF_WEEK)-2;
                if(week==-1)
                    week=6;
                Log.e(TAG, "week:"+week);
                datas.put("week",week+"");
                return datas;
            }
            @Override
            public void setPostData(Map datas) {
                datas.put("userid", globalInfos.getUserId()+ "");
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                int week = calendar.get(Calendar.DAY_OF_WEEK)-2;
                if(week==-1)
                    week=6;
                Log.e(TAG, "week:"+week);
                datas.put("week",week+"");
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }
}
