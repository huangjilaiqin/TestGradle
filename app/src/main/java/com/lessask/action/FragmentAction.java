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

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lessask.DividerItemDecoration;
import com.lessask.dialog.MenuDialog;
import com.lessask.dialog.OnSelectMenu;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;
import com.lessask.recyclerview.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.model.GetActionResponse;
import com.lessask.net.GsonRequest;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.RecyclerViewStatusSupport;
import com.lessask.video.RecordVideoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHuang on 2015/11/28.
 */
public class FragmentAction extends Fragment implements View.OnClickListener{
    private View rootView;
    private final String TAG = FragmentAction.class.getName();
    private ActionAdapter mRecyclerViewAdapter;
    private RecyclerViewStatusSupport mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    public static final int EDIT_ACTION = 1;
    public static final int CREATE_ACTION = 2;
    public static final int RECORD_ACTION = 3;

    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
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
            rootView.findViewById(R.id.add).setOnClickListener(this);


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
                    Intent intent = new Intent(FragmentAction.this.getActivity(), EditActionActivity.class);
                    intent.putExtra("actionItem", mRecyclerViewAdapter.getItem(position));
                    intent.putExtra("position", position);
                    startActivityForResult(intent, EDIT_ACTION);

                }
            });
            mRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            mRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, final int position) {
                    final ActionItem actionItem = mRecyclerViewAdapter.getItem(position);
                    final MenuDialog menuDialog = new MenuDialog(getContext(), new String[]{"删除"},
                    new OnSelectMenu() {
                        @Override
                        public void onSelectMenu(int menupos) {
                            Intent intent;
                            switch (menupos){
                                case 0:
                                    //删除动作
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FragmentAction.this.getContext());
                                    builder.setMessage("确认删除吗？position:"+position+", name:"+actionItem.getName());
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
                    menuDialog.show();

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

            //加载数据
            loadAtions();
        }
        return rootView;
    }

    private void deleteAction(final int deletePostion){
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
                datas.put("userid", globalInfos.getUserId() + "");
                datas.put("videoName", actionItem.getVideoName());
                datas.put("actionIamge", actionItem.getActionImage());
                datas.put("id", actionItem.getId() + "");

            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                ActionItem actionItem = mRecyclerViewAdapter.getItem(deletePostion);
                datas.put("userid", globalInfos.getUserId() + "");
                datas.put("videoName", actionItem.getVideoName());
                datas.put("actionIamge", actionItem.getActionImage());
                datas.put("id", actionItem.getId() + "");
                return datas;
            }
        });

        volleyHelper.addToRequestQueue(deleteActionRequest);
    }

    private void loadAtions(){
        GsonRequest getActionsRequest = new GsonRequest<>(Request.Method.POST, config.getActioinsUrl(), GetActionResponse.class, new GsonRequest.PostGsonRequest<GetActionResponse>() {
            @Override
            public void onStart() {
                Log.e(TAG, "start getactions");
                mRecyclerView.showLoadingView();
            }

            @Override
            public void onResponse(GetActionResponse response) {
                Log.e(TAG, "response:"+response);
                ArrayList<ActionItem> actiondatas = response.getActionDatas();
                globalInfos.addActions(actiondatas);

                Log.e(TAG, "actiondatas length:" + actiondatas.size());
                mRecyclerViewAdapter.appendToList(actiondatas);
                //Log.e(TAG, "oldShowId:" + oldShowId + " newShowId:" + newShowId);
                mRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "getactions err:"+error.toString());
                mRecyclerView.showErrorView(error.toString());
            }

            @Override
            public void setPostData(Map datas) {
                datas.put("userid", "" + globalInfos.getUserId());
            }

            @Override
            public Map getPostData() {
                Map datas = new HashMap();
                datas.put("userid", globalInfos.getUserId() + "");
                return datas;
            }
        });
        volleyHelper.addToRequestQueue(getActionsRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActionItem actionItem = null;
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case EDIT_ACTION:
                    actionItem = data.getParcelableExtra("actionItem");
                    int position = data.getIntExtra("position", -1);
                    Log.e(TAG, "onActivityResult EDIT_ACTION position:"+position);
                    ActionItem oldOne = mRecyclerViewAdapter.getItem(position);
                    oldOne.setName(actionItem.getName());
                    oldOne.setVideoName(actionItem.getVideoName());
                    oldOne.setActionImage(actionItem.getActionImage());
                    oldOne.setTags(actionItem.getTags());
                    oldOne.setNotices(actionItem.getNotices());
                    mRecyclerViewAdapter.notifyItemChanged(position);
                    break;
                case RECORD_ACTION:
                    Intent intent = new Intent(getContext(), CreateActionActivity.class);
                    intent.putExtra("videoPath", data.getStringExtra("videoPath"));
                    intent.putExtra("ratio", data.getFloatExtra("ratio", 0.5f));
                    intent.putExtra("imagePath", data.getStringExtra("imagePath"));
                    startActivityForResult(intent, CREATE_ACTION);
                    Log.e(TAG, "RECORD_ACTION back");
                    break;
                case CREATE_ACTION:
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.add:
                intent = new Intent(getContext(), RecordVideoActivity.class);
                startActivityForResult(intent, RECORD_ACTION);
                break;
        }
    }
}

