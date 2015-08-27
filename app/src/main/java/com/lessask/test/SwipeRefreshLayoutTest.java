package com.lessask.test;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lessask.R;

import java.util.ArrayList;

public class SwipeRefreshLayoutTest extends Activity {
    private ArrayList mylist;
    private int num = 0;

    private ArrayList createItems(int a, int b){
        ArrayList list = new ArrayList();
        while(b<a){
            list.add(b);
            b++;
        }
        return list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_refresh_layout_test);
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

        swipeView.setEnabled(false);
        final ListView lView = (ListView) findViewById(R.id.list);
        mylist = createItems(40,0);
        final ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mylist);
        lView.setAdapter(adp);
        swipeView.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        mylist.add(0, --num);
                        adp.notifyDataSetChanged();

                    }
                }, 3000);
            }
        });

        lView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }
        });

    }
}
