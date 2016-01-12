package com.lessask.lesson;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.HandleLessonResponse;
import com.lessask.model.Lesson;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by huangji on 2015/11/24.
 */
public class FragmentLesson extends Fragment implements View.OnClickListener{
    private View rootView;
    private final String TAG = FragmentLesson.class.getName();
    private LessonAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    private final int EDIT_LESSON = 0;
    private final int CREATE_LESSON = 1;
    private FloatingActionButton mAdd;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_lesson, null);
            rootView.findViewById(R.id.add).setOnClickListener(this);

            rootView.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadLessons();
                }
            });

            mRecyclerView = (RecyclerViewStatusSupport)rootView.findViewById(R.id.lesson_list);
            mRecyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setClickable(true);

            mRecyclerViewAdapter = new LessonAdapter(getContext());
            //设置点击事件, 编辑动作
            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    Intent intent = new Intent(getActivity(), EditLessonActivity.class);
                    intent.putExtra("lesson", mRecyclerViewAdapter.getItem(position));
                    intent.putExtra("position", position);
                    startActivityForResult(intent, EDIT_LESSON);
                }
            });
            mRecyclerViewAdapter.setOnItemMenuClickListener(new OnItemMenuClickListener() {
                @Override
                public void onItemMenuClick(View view, final int position) {
                    final Lesson lesson = mRecyclerViewAdapter.getItem(position);
                    switch (view.getId()) {
                        case R.id.delete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(FragmentLesson.this.getContext());
                            builder.setMessage("确认删除吗？position:" + position + ", name:" + lesson.getName());
                            builder.setTitle("提示");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //网络协议
                                    deleteAction(position);
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            break;
                    }
                }
            });
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            loadLessons();
        }
        return rootView;
    }

    private void deleteAction(final int deletePostion){
        GsonRequest deleteActionRequest = new GsonRequest<>(Request.Method.POST, config.getDeleteLessonUrl(), HandleLessonResponse.class, new GsonRequest.PostGsonRequest<HandleLessonResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(HandleLessonResponse response) {
                if(response.getError()!=null){
                    Toast.makeText(getContext(), "error:"+response.getError(), Toast.LENGTH_SHORT).show();
                    return;
                }
                mRecyclerViewAdapter.remove(deletePostion);
                mRecyclerViewAdapter.notifyItemRemoved(deletePostion);
                mRecyclerViewAdapter.notifyItemRangeChanged(deletePostion, mRecyclerViewAdapter.getItemCount());
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), "error:"+error.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setPostData(Map datas) {
                Lesson lesson = mRecyclerViewAdapter.getItem(deletePostion);
                datas.put("userId", globalInfos.getUserId() + "");
                datas.put("id", lesson.getId() + "");

            }
        });

        volleyHelper.addToRequestQueue(deleteActionRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Lesson lesson = null;
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case EDIT_LESSON:
                    Toast.makeText(getContext(), "edit lesson done", Toast.LENGTH_SHORT).show();
                    int position = data.getIntExtra("position", -1);
                    lesson = data.getParcelableExtra("lesson");
                    Lesson oldOne = mRecyclerViewAdapter.getItem(position);
                    oldOne.setName(lesson.getName());
                    oldOne.setCover(lesson.getCover());
                    oldOne.setPurpose(lesson.getPurpose());
                    oldOne.setBodies(lesson.getBodies());
                    oldOne.setAddress(lesson.getAddress());
                    oldOne.setCostTime(lesson.getCostTime());
                    oldOne.setFatEffect(lesson.getFatEffect());
                    oldOne.setMuscleEffect(lesson.getMuscleEffect());
                    oldOne.setDescription(lesson.getDescription());
                    oldOne.setRecycleTimes(lesson.getRecycleTimes());
                    oldOne.setLessonActions(lesson.getLessonActions());
                    Log.e(TAG, "selectAction append:"+oldOne.getLessonActions());
                    mRecyclerViewAdapter.notifyItemChanged(position);
                    break;
                case CREATE_LESSON:
                    Toast.makeText(getContext(), "create lesson done", Toast.LENGTH_SHORT).show();
                    lesson = data.getParcelableExtra("lesson");
                    if (lesson != null) {
                        mRecyclerViewAdapter.append(lesson);
                        mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount()-1);
                        Log.e(TAG, "create lesson success notifyItemInserted");
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                Intent intent = new Intent(getContext(), CreateLessonActivity.class);
                startActivityForResult(intent, CREATE_LESSON);
                break;
        }

    }
    private void loadLessons(){
        Type type = new TypeToken<ArrayList<Lesson>>() {}.getType();
        GsonRequest  gsonRequest = new GsonRequest<ArrayList<Lesson>>(Request.Method.POST,config.getLessonsUrl(),type,new GsonRequest.PostGsonRequest<ArrayList<Lesson>>(){
            @Override
            public void onStart() {
                mRecyclerView.showLoadingView();
            }

            @Override
            public void onResponse(ArrayList<Lesson> response) {
                Lesson l = response.get(0);
                mRecyclerViewAdapter.appendToList(response);
                mRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                mRecyclerView.showErrorView(error.toString());
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userId", "" + globalInfos.getUserId());
            }
        });
        volleyHelper.addToRequestQueue(gsonRequest);
    }
}
