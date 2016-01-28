package com.lessask.lesson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.crud.CRUD;
import com.lessask.crud.DefaultGsonRequestCRUD;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ArrayListResponse;
import com.lessask.model.Lesson;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHuang on 2016/1/28.
 */
public class FragmentLessonActions extends Fragment implements CRUD<LessonAction> {
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private View rootView;

    private Lesson lesson;

    private RecyclerViewStatusSupport mActionsRecycleView;
    private ShowLessonActionsAdapter mAdapter;

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_lesson_actions, null);
            mActionsRecycleView = (RecyclerViewStatusSupport) rootView.findViewById(R.id.actions);
            mActionsRecycleView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mActionsRecycleView.setLayoutManager(linearLayoutManager);
            mActionsRecycleView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mAdapter = new ShowLessonActionsAdapter(getContext());
            mActionsRecycleView.setAdapter(mAdapter);
            read(mActionsRecycleView);
        }
        return rootView;
    }

    @Override
    public void create(LessonAction obj, int position) {

    }

    @Override
    public void read(RecyclerViewStatusSupport recyclerView) {
        DefaultGsonRequestCRUD  crud = new DefaultGsonRequestCRUD();
        Type type = new TypeToken<ArrayListResponse<LessonAction>>() {}.getType();
        Map datas = new HashMap();
        datas.put("userId", globalInfos.getUserId() + "");
        datas.put("lessonId", ""+lesson.getId());
        crud.read(getContext(), mAdapter, recyclerView, config.getLessonActionsUrl(), type, datas);
    }

    @Override
    public void update(LessonAction obj, int position) {

    }

    @Override
    public void delete(LessonAction obj, int position) {

    }
}
