package com.lessask.contacts;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.chat.Chat;
import com.lessask.chat.ChatResponseListener;
import com.lessask.global.Config;
import com.lessask.global.DbHelper;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChatMessage;
import com.lessask.model.User;
import com.lessask.net.VolleyHelper;
import com.lessask.recyclerview.BaseRecyclerAdapter;
import com.lessask.recyclerview.OnItemClickListener;
import com.lessask.recyclerview.OnItemLongClickListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huangji on 2016/2/19.
 */
public class FindFriendAdapter extends BaseRecyclerAdapter<User,FindFriendAdapter.ViewHolder>{
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private Context context;
    private String TAG = FindFriendAdapter.class.getSimpleName();
    private Gson gson = new Gson();
    private Set<Integer> friendids;

    private final int HANDLER_ADD_FRIEND = 1;
    public FindFriendAdapter(Context context) {
        this.context = context;
        Chat.getInstance(context).appendChatResponseListener("addfriend", addFriendListener);
        friendids = loadContact();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_ADD_FRIEND:
                    notifyItemChanged(msg.arg1);
                    //通知friendActivity更新
                    break;
                default:
                    break;
            }
        }
    };

    private ChatResponseListener addFriendListener = new ChatResponseListener() {
        @Override
        public void response(String obj) {
            Log.e(TAG, "addFriendListener:"+obj);
            AddFriend addFriend = gson.fromJson(obj, AddFriend.class);
            if(addFriend.getError()!="" || addFriend.getErrno()!=0){

            }else {
                //本地入库
                /*
                ContentValues values = new ContentValues();
                values.put("userid", user.getUserid());
                values.put("nickname", user.getNickname());
                values.put("headImg", user.getHeadImg());
                db.insert("t_contact", "", values);
                */
            }
            updateItem(addFriend);
        }
    };

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private Set loadContact(){
        SQLiteDatabase db = DbHelper.getInstance(context).getDb();
        Cursor cursor = db.rawQuery("select * from t_contact", null);
        Log.e(TAG, "query contact size:"+cursor.getCount());
        Set<Integer> friendids = new HashSet<>();
        while (cursor.moveToNext()){
            friendids.add(cursor.getInt(0));
        }
        return friendids;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = getItem(position);
        holder.name.setText(user.getNickname());
        String headImgUrl = imageUrlPrefix+user.getHeadImg();
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(holder.headImg, 0, 0);
        VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);

        if(friendids.contains(user.getUserid())){
            Log.e(TAG, "contains id:"+user.getUserid());
            holder.add.setVisibility(View.INVISIBLE);
            holder.adding.setVisibility(View.INVISIBLE);
        }else {
            Log.e(TAG, "not contains id:"+user.getUserid());
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddFriend addFriend = new AddFriend(globalInfos.getUserId(), user.getUserid());
                    Chat.getInstance(context).emit("addfriend", gson.toJson(addFriend));
                    holder.adding.setVisibility(View.VISIBLE);
                    holder.add.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void updateItem(AddFriend addFriend){
        int positon=-1;
        List list = getList();
        for(int i=list.size()-1;i>0;i--){
            User user= (User) list.get(i);
            if(user.getUserid()==addFriend.getFriendid()){
                positon=i;
                break;
            }
        }
        Log.e(TAG, "updateItem positoin:"+positon);
        if(positon!=-1){
            friendids.add(addFriend.getFriendid());
            Log.e(TAG, "addfriend position:"+positon);
            Message msg = new Message();
            msg.arg1 = positon;
            msg.what = HANDLER_ADD_FRIEND;
            handler.sendMessage(msg);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView headImg;
        TextView name;
        boolean isFriend;
        Button add;
        ProgressBar adding;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            headImg = (ImageView) itemView.findViewById(R.id.head_img);
            name =(TextView)itemView.findViewById(R.id.name);
            add = (Button) itemView.findViewById(R.id.add);
            adding = (ProgressBar) itemView.findViewById(R.id.adding);
        }
    }
}
