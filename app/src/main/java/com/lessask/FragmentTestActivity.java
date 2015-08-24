package com.lessask;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class FragmentTestActivity extends FragmentActivity {

    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private Class fragmentArray[] = {FragmentFriends.class, FragmentMe.class};
    private String mTextviewArray[] = {"好友", "我"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost)findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        int count = fragmentArray.length;

        for(int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中,每个标签与fragment关联起来
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }
     private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.main_tab_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }
}
