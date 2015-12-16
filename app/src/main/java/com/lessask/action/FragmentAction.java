package com.lessask.action;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lessask.DividerItemDecoration;
import com.lessask.MainActivity;
import com.lessask.OnItemClickListener;
import com.lessask.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.model.GetActionResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.NetFragment;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.RecyclerViewStatusSupport;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by JHuang on 2015/11/28.
 */
public class FragmentAction extends Fragment {
    private View rootView;
    private final String TAG = FragmentAction.class.getName();
    private ActionAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private int deletePostion;

    private VolleyHelper volleyHelper = VolleyHelper.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_action, null);

            rootView.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadAtions();
                }
            });


            mRecyclerView = (RecyclerViewStatusSupport)rootView.findViewById(R.id.action_list);
            mRecyclerView.setStatusViews(rootView.findViewById(R.id.loading_view), rootView.findViewById(R.id.empty_view), rootView.findViewById(R.id.error_view));
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setClickable(true);

            mRecyclerViewAdapter = new ActionAdapter(getContext());
            //数据
            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(getContext(), "onClick:" + position, Toast.LENGTH_SHORT).show();
                    /*
                    Intent intent = new Intent(getActivity(), EditActionActivity.class);
                    startActivityForResult(intent, MainActivity.EDIT_ACTION);
                    */
                }
            });
            mRecyclerViewAdapter.setOnItemMenuClickListener(new OnItemMenuClickListener() {
                @Override
                public void onItemMenuClick(View view, final int position) {
                    final ActionItem actionItem = mRecyclerViewAdapter.getItem(position);
                    Log.e(TAG, "onItemMenuClick count:+"+mRecyclerViewAdapter.getItemCount()+" position:"+position);
                    switch (view.getId()){
                        case R.id.delete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(FragmentAction.this.getContext());
                            builder.setMessage("确认删除吗？position:"+position+", name:"+actionItem.getName());
                            builder.setTitle("提示");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //网络协议
                                    deletePostion = position;
                                    deleteAction();
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
                        case R.id.edit:
                            Intent intent = new Intent(FragmentAction.this.getActivity(), EditActionActivity.class);
                            intent.putExtra("actionItem", actionItem);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, MainActivity.EDIT_ACTION);
                            break;
                    }
                }
            });
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            mRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "click");
                    //Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
                }
            });
            mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.e(TAG, "onLongClick");
                    //Toast.makeText(getActivity(), "longClick", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            //加载数据
            loadAtions();
        }
        return rootView;
    }

    private void deleteAction(){
        GsonRequest deleteActionRequest = new GsonRequest<>(Request.Method.POST, config.getDeleteActionUrl(), HandleActionResponse.class, new GsonRequest.PostGsonRequest<HandleActionResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(HandleActionResponse response) {
                mRecyclerViewAdapter.remove(deletePostion);
                mRecyclerViewAdapter.notifyItemRemoved(deletePostion);
                mRecyclerViewAdapter.notifyItemRangeChanged(deletePostion, mRecyclerViewAdapter.getItemCount());
            }

            @Override
            public void onError(VolleyError error) {

            }

            @Override
            public void setPostData(Map datas) {
                ActionItem actionItem = mRecyclerViewAdapter.getItem(deletePostion);
                datas.put("userid", globalInfos.getUserid() + "");
                datas.put("name", actionItem.getVideoName());
                datas.put("id", actionItem.getId() + "");

            }
        });

        volleyHelper.addToRequestQueue(deleteActionRequest);
    }

    private void loadAtions(){
        GsonRequest getActionsRequest = new GsonRequest<>(Request.Method.POST, config.getActioinsUrl(), GetActionResponse.class, new GsonRequest.PostGsonRequest<GetActionResponse>() {
            @Override
            public void onStart() {
                mRecyclerView.showLoadingView();
            }

            @Override
            public void onResponse(GetActionResponse response) {
                ArrayList<ActionItem> actiondatas = response.getActionDatas();
                Log.e(TAG, "actiondatas length:" + actiondatas.size());
                mRecyclerViewAdapter.appendToList(actiondatas);
                //Log.e(TAG, "oldShowId:" + oldShowId + " newShowId:" + newShowId);
                mRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                mRecyclerView.showErrorView(error.toString());
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", "" + globalInfos.getUserid());
            }
        });
        volleyHelper.addToRequestQueue(getActionsRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActionItem actionItem = null;
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MainActivity.EDIT_ACTION:
                    actionItem = data.getParcelableExtra("actionItem");
                    int position = data.getIntExtra("position", -1);
                    Log.e(TAG, "onActivityResult EDIT_ACTION position:"+position);
                    ActionItem oldOne = mRecyclerViewAdapter.getItem(position);
                    oldOne.setName(actionItem.getName());
                    oldOne.setVideoName(actionItem.getVideoName());
                    oldOne.setTags(actionItem.getTags());
                    oldOne.setNotices(actionItem.getNotices());
                    mRecyclerViewAdapter.notifyItemChanged(position);
                    break;
                case MainActivity.CREATE_ACTION:
                    if (data == null) {
                        Log.e(TAG, "intent is null");
                    }
                    actionItem = data.getParcelableExtra("actionItem");
                    if (actionItem != null) {
                        mRecyclerViewAdapter.append(actionItem);
                        mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount()-1);
                        Log.e(TAG, "create action success notifyItemInserted");
                    }
                    break;
            }
        }
    }
}

