package com.lessask.action;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.DividerItemDecoration;
import com.lessask.MainActivity;
import com.lessask.OnFragmentResult;
import com.lessask.OnItemClickListener;
import com.lessask.OnItemMenuClickListener;
import com.lessask.R;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ActionItem;
import com.lessask.model.GetActionResponse;
import com.lessask.net.PostResponse;
import com.lessask.net.PostSingle;
import com.lessask.net.PostSingleEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JHuang on 2015/11/28.
 */
public class FragmentAction extends Fragment{
    private View rootView;
    private final String TAG = FragmentAction.class.getName();
    private ActionAdapter mRecyclerViewAdapter;
    //private LessonAdapter2 mRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();

    private final int HANDLER_GETACTION_START = 0;
    private final int HANDLER_GETACTION_DONE = 1;
    private final int ON_DELETE_START = 2;
    private final int ON_DELETE_DONE = 3;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_GETACTION_START:
                    break;
                case HANDLER_GETACTION_DONE:
                    int statusCode = msg.arg1;

                    Log.e(TAG, "statusCode"+statusCode);
                    if(statusCode==200){
                        GetActionResponse getActionResponse = (GetActionResponse)msg.obj;
                        ArrayList<ActionItem> actiondatas = getActionResponse.getActionDatas();
                        Log.e(TAG, "actiondatas length:"+actiondatas.size());

                        mRecyclerViewAdapter.appendToList(actiondatas);
                        //Log.e(TAG, "oldShowId:" + oldShowId + " newShowId:" + newShowId);
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        //mRecyclerView.scrollToPosition(position);
                    }else {
                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT);
                        Log.e(TAG, "loadMore is error");
                    }
                    break;
                case ON_DELETE_START:
                    break;
                case ON_DELETE_DONE:
                    HandleActionResponse response = (HandleActionResponse)msg.obj;
                    if(msg.arg1==1) {
                        Toast.makeText(FragmentAction.this.getContext(), "delete action:" + response.getVideoName(), Toast.LENGTH_SHORT).show();
                    }else {
                        if(response!=null)
                            Toast.makeText(FragmentAction.this.getContext(), "delete action error:"+response.getError(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(FragmentAction.this.getContext(), "delete action error", Toast.LENGTH_SHORT).show();

                    }
                    break;
            }
        }
    };


    private PostSingleEvent postSingleEvent = new PostSingleEvent() {
        @Override
        public void onStart() {
            Message msg = new Message();
            msg.what = HANDLER_GETACTION_START;
            handler.sendMessage(msg);
        }

        @Override
        public void onDone(boolean success, PostResponse response) {
            Message msg = new Message();
            msg.what = HANDLER_GETACTION_DONE;
            if(response!=null) {
                msg.arg1 = response.getCode();
                Log.e(TAG, "body:"+response.getBody());
                GetActionResponse getActionResponse = gson.fromJson(response.getBody(), GetActionResponse.class);
                msg.obj = getActionResponse;
            }else {
                msg.arg1 = -1;
            }
            handler.sendMessage(msg);
        }
    };
    PostSingleEvent deleteActionEvent = new PostSingleEvent() {
        @Override
        public void onStart() {
            Message msg = new Message();
            msg.what = ON_DELETE_START;
            handler.sendMessage(msg);
        }

        @Override
        public void onDone(boolean success, PostResponse postResponse) {
            Message msg = new Message();
            msg.what = ON_DELETE_DONE;
            if(postResponse!=null) {
                int resCode = postResponse.getCode();
                String body = postResponse.getBody();
                HandleActionResponse response = gson.fromJson(body, HandleActionResponse.class);
                msg.obj = response;
                if (success)
                    msg.arg1 = 1;
                else
                    msg.arg1 = 0;
                handler.sendMessage(msg);
            }else {
                msg.arg1 = 0;
                handler.sendMessage(msg);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_action, null);

            //加载数据
            PostSingle postSingle = new PostSingle(config.getActioinsUrl(), postSingleEvent);
            HashMap<String, String> requestArgs = new HashMap<>();
            requestArgs.put("userid", "" + globalInfos.getUserid());
            postSingle.setHeaders(requestArgs);
            postSingle.start();

            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.lesson_list);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setClickable(true);

            //mRecyclerViewAdapter = new LessonAdapter(getContext());
            mRecyclerViewAdapter = new ActionAdapter(getContext());
            //数据
            //mRecyclerViewAdapter.appendToList(getData());
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
                    switch (view.getId()){
                        case R.id.delete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(FragmentAction.this.getContext());
                            builder.setMessage("确认删除吗？");
                            builder.setTitle("提示");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //网络协议
                                    PostSingle postSingle = new PostSingle(config.getDeleteActionUrl(), deleteActionEvent);

                                    HashMap<String, String> headers = new HashMap<>();
                                    headers.put("userid", globalInfos.getUserid()+"");
                                    headers.put("name", actionItem.getVideo());
                                    headers.put("id", actionItem.getId() + "");
                                    postSingle.setHeaders(headers);

                                    postSingle.start();

                                    mRecyclerViewAdapter.remove(position);
                                    mRecyclerViewAdapter.notifyItemRemoved(position);
                                    //mRecyclerViewAdapter.notifyItemRangeChanged(position, mRecyclerViewAdapter.getItemCount());
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
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case MainActivity.EDIT_ACTION:
                Log.e(TAG, "onActivityResult EDIT_ACTION");
                break;
            case MainActivity.CREATE_ACTION:
                ActionItem actionItem = (ActionItem)data.getParcelableExtra("actionItem");
                mRecyclerViewAdapter.append(actionItem);
                mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getItemCount());
                Toast.makeText(this.getContext(), "FragmentAction onActivityResult:"+actionItem.getName() , Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

