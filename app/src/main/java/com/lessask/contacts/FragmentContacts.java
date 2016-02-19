package com.lessask.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lessask.DividerItemDecoration;
import com.lessask.R;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

/**
 * Created by huangji on 2015/11/24.
 */
public class FragmentContacts extends Fragment implements Toolbar.OnMenuItemClickListener{
    private View rootView;

    private FloatingActionButton mSearch;
    private RecyclerViewStatusSupport mRecyclerView;
    private ContactsAdapter mRecyclerViewAdapter;

    private DrawerLayout mDrawerLayout;
    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_contacts, null);
            Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            mToolbar.setTitle("通信录");
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            });
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });
            mToolbar.inflateMenu(R.menu.menu_contacts);
            mSearch = (FloatingActionButton)rootView.findViewById(R.id.search);
            mSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "search", Toast.LENGTH_SHORT).show();
                }
            });

            mRecyclerView = (RecyclerViewStatusSupport) rootView.findViewById(R.id.show_list);
            mRecyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            //用线性的方式显示listview
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

            mRecyclerViewAdapter = new ContactsAdapter();
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            mRecyclerView.showEmptyView();
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contacts, menu);
        menu.add("Menu 1a").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Toast.makeText(getContext(), "add friend", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}
