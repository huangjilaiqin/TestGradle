package com.lessask.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lessask.R;

/**
 * Created by huangji on 2015/10/16.
 */
public class MenuDialog extends AlertDialog {

    private Context context;
    private String TAG = MenuDialog.class.getSimpleName();
    private OnSelectMenu onSelectMenu;
    private SimpleDraweeView mAnimatedGifView;

    public void setOnSelectMenu(OnSelectMenu onSelectMenu) {
        this.onSelectMenu = onSelectMenu;
    }

    public MenuDialog(Context context,String[] titles) {
        super(context, R.style.nothing_dialog);
        this.context = context;
        LayoutInflater li = LayoutInflater.from(context);
        //View view = li.inflate(R.layout.menu_dialog, null);
        LinearLayout menu = new LinearLayout(context);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        menu.setMinimumWidth((int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8));
        //setContentView(view);
        setView(menu);

        //LinearLayout menu = (LinearLayout)view.findViewById(R.id.menu);
        int size = titles.length;
        for (int i=0;i<size;i++){
            final int position = i;
            TextView item = new TextView(context);
            item.setText(titles[i]);
            item.setTextSize(sp2Px(18));
            item.setPadding(dp2Px(16), dp2Px(8), 0, dp2Px(8));
            item.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onSelectMenu != null)
                        onSelectMenu.onSelectMenu(position);
                    cancel();
                }
            });
            menu.addView(item);
            Log.e(TAG, "add "+titles[i]);
            if(i+1<size){
                LinearLayout line = new LinearLayout(context);
                line.setGravity(LinearLayout.HORIZONTAL);
                line.setBackgroundColor(context.getResources().getColor(R.color.borde_gray));
                line.setMinimumHeight(dp2Px(1));
                line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                menu.addView(line);
            }
        }
        setCanceledOnTouchOutside(true);

    }
    private int dp2Px(int dp){
        return  (int)(dp*context.getResources().getDisplayMetrics().density+0.5f);
    }
    private int sp2Px(float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
