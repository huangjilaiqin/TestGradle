package com.lessask.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lessask.R;
import com.lessask.RecyclerViewDragHolder;

import java.util.ArrayList;

public class SlideMenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_menu);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置方向
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        MyAdapter myAdapter = new MyAdapter(this);
        recyclerView.setAdapter(myAdapter);
    }

    public class MyAdapter extends RecyclerView.Adapter {

        private ArrayList<String> stringArrayList;
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
            stringArrayList = new ArrayList<>();
            stringArrayList.add("neo");
            stringArrayList.add("android");
            stringArrayList.add("ios");
            stringArrayList.add("blog");
            stringArrayList.add("app");
            stringArrayList.add("html");
            stringArrayList.add("python");
            stringArrayList.add("linux");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //获取背景菜单
            //这个控件的match_parent的大小就是item大小,如果item边缘使用margin会显示bg_menu的底色
            View mybg = LayoutInflater.from(parent.getContext()).inflate(R.layout.bg_menu, null);
            mybg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            //获取item布局
            //item布局要有两层layout在第二层layout里面放控件,背景色设置为不透明，否则bg_menu会显示出来
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            //生成返回RecyclerView.ViewHolder
            return new MyHolder(context, mybg, view, RecyclerViewDragHolder.EDGE_RIGHT).getDragViewHolder();
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final MyHolder myHolder = (MyHolder) RecyclerViewDragHolder.getHolder(holder);
            String data = stringArrayList.get(position);

            myHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stringArrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            });
            myHolder.closeApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "关闭菜单", Toast.LENGTH_SHORT).show();
                    myHolder.close();
                }
            });
        }


        @Override
        public int getItemCount() {
            return stringArrayList.size();
        }


        class MyHolder extends RecyclerViewDragHolder {

            private TextView deleteItem;
            private TextView closeApp;

            public MyHolder(Context context, View bgView, View topView) {
                super(context, bgView, topView);
            }

            public MyHolder(Context context, View bgView, View topView, int mTrackingEdges) {
                super(context, bgView, topView, mTrackingEdges);
            }

            @Override
            public void initView(View itemView) {
                deleteItem = (TextView) itemView.findViewById(R.id.delete);
                closeApp = (TextView) itemView.findViewById(R.id.closeMenu);
            }
        }
    }

}
