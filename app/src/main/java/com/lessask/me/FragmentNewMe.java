package com.lessask.me;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.lessask.MyFragmentPagerAdapter;
import com.lessask.R;
import com.lessask.chat.Chat;
import com.lessask.global.Config;
import com.lessask.global.GlobalInfos;
import com.lessask.net.VolleyHelper;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JHuang on 2015/8/23.
 */
public class FragmentNewMe extends Fragment{
    private String TAG = FragmentNewMe.class.getSimpleName();
    private Chat chat = Chat.getInstance(getContext());
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private Config config = globalInfos.getConfig();
    private  String imageUrlPrefix = config.getImgUrl();
    private View rootView;
    public final static int INFO_CHANGE=1;
    private TextView name;
    private CircleImageView head;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_new_me, container,false);
            head = (CircleImageView) rootView.findViewById(R.id.head);
            ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(head,0,0);
            String headImgUrl = imageUrlPrefix+globalInfos.getUserId()+".jpg";
            VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);
            name = (TextView) rootView.findViewById(R.id.name);
            name.setText(globalInfos.getUser().getNickname());

            rootView.findViewById(R.id.head_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), PersonInfoActivity.class);
                    intent.putExtra("user", globalInfos.getUser());
                    startActivityForResult(intent,INFO_CHANGE);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case INFO_CHANGE:
                    ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(head,0,0);
                    String headImgUrl = imageUrlPrefix+globalInfos.getUserId()+".jpg";
                    VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);
                    name.setText(globalInfos.getUser().getNickname());
                    break;
            }
        }
    }
}



