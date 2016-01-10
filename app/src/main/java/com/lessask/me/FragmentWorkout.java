package com.lessask.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.dialog.MenuDialog;
import com.lessask.dialog.OnSelectMenu;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.Workout;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by JHuang on 2015/10/22.
 */
public class FragmentWorkout extends Fragment {
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private final int SELECT_LESSON = 1;
    private String TAG = FragmentWorkout.class.getSimpleName();

    private View rootView;
    private WorkoutAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ItemTouchHelper mItemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_workout, null);
            rootView.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadWorkouts();
                }
            });
            mRecyclerView = (RecyclerViewStatusSupport)rootView.findViewById(R.id.workouts);
            mRecyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setClickable(true);

            mRecyclerViewAdapter = new WorkoutAdapter(getContext());
            //查看动作
            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    /*
                    Intent intent = new Intent(getActivity(), LessonActivity.class);
                    intent.putExtra("lesson", mRecyclerViewAdapter.getItem(position).getLesson());
                    startActivity(intent);
                    */
                    MenuDialog menuDialog = new MenuDialog(getContext(),new String[]{"休息","更改"});
                    menuDialog.setOnSelectMenu(new OnSelectMenu() {
                        @Override
                        public void onSelectMenu(int menupos) {
                            Toast.makeText(getContext(),"menupos:"+menupos+", position:"+position,Toast.LENGTH_SHORT).show();
                        }
                    });
                    menuDialog.show();
                }
            });
            mRecyclerViewAdapter.setOnItemMenuClickListener(new OnItemMenuClickListener() {
                @Override
                public void onItemMenuClick(View view, final int position) {
                    final Workout workout = mRecyclerViewAdapter.getItem(position);
                    switch (view.getId()) {
                        case R.id.reset:
                            Toast.makeText(FragmentWorkout.this.getContext(), "reset", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.change:
                            Toast.makeText(FragmentWorkout.this.getContext(), "change", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.add:
                            Toast.makeText(FragmentWorkout.this.getContext(), "add", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            loadWorkouts();
        }
        return rootView;
    }
    private void loadWorkouts(){
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
                    Log.e(TAG, response.getError());
                }else {
                    ArrayList<Workout> showWorkouts = new ArrayList<>();
                    ArrayList<Workout> workouts = response.getDatas();
                    int currentWeek = 1;
                    Workout workout;
                    if(workouts.size()==0){
                        while (currentWeek<8){
                            Workout resetWorkout = new Workout();
                            resetWorkout.setWeek(currentWeek);
                            showWorkouts.add(resetWorkout);
                            currentWeek++;
                        }
                    }else if(workouts.size()!=7) {
                        for (int i=0;i<workouts.size();i++){
                            workout = workouts.get(i);
                            int week = workout.getWeek();
                            while (currentWeek<week){
                                Workout resetWorkout = new Workout();
                                resetWorkout.setWeek(currentWeek);
                                showWorkouts.add(resetWorkout);
                                currentWeek++;
                            }
                            currentWeek=week+1;
                        }
                    }
                    mRecyclerViewAdapter.appendToList(showWorkouts);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                mRecyclerView.showErrorView(error.getMessage());
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userId", "" + globalInfos.getUserId());
            }
        });
        VolleyHelper.getInstance().addToRequestQueue(gsonRequest);
    }

}
