package com.lessask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.crud.CRUDExtend;
import com.lessask.crud.DefaultGsonRequestCRUD;
import com.lessask.dialog.LoadingDialog;
import com.lessask.dialog.MenuDialog;
import com.lessask.dialog.OnSelectMenu;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.lesson.SelectLessonActivity;
import com.lessask.me.FragmentMe;
import com.lessask.me.WorkoutAdapter;
import com.lessask.me.WorkoutResponse;
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

/**
 * Created by JHuang on 2015/10/22.
 */
public class FragmentWorkout extends Fragment {
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private String TAG = FragmentWorkout.class.getSimpleName();

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "FragmentWorkout onCreateView");
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_workout, null);
            Log.e(TAG, "FragmentWorkout");
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            Log.e(TAG, "onActivityResult");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
