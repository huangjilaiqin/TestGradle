package com.lessask.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
public class MenuDialog extends Dialog {

    private Context context;
    private String TAG = MenuDialog.class.getSimpleName();
    private OnSelectMenu onSelectMenu;
    private SimpleDraweeView mAnimatedGifView;

    public void setOnSelectMenu(OnSelectMenu onSelectMenu) {
        this.onSelectMenu = onSelectMenu;
    }

    public MenuDialog(Context context,String[] titles) {
        super(context, R.style.menu_dialog);
        this.context = context;
        LinearLayout menu = new LinearLayout(context);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setBackgroundResource(R.drawable.menu_dialog_bg);
        menu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        menu.setMinimumWidth((int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.78));
        //setContentView(menu);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.menu_dialog_test, null);
        setContentView(view);
        //setView(menu);

        /*
        //LinearLayout menu = (LinearLayout)view.findViewById(R.id.menu);
        int size = titles.length;
        for (int i=0;i<size;i++){
            final int position = i;
            TextView item = new TextView(context);
            item.setText(titles[i]);
            //item.setTextSize(sp2Px(18));
            item.setTextSize(16);
            item.setMinHeight(dp2Px(48));
            //item.setAlpha(0);
            //item.setBackgroundColor(0x00000000);
            item.setBackgroundColor(Color.parseColor("#0e00ff00"));
            item.setTextColor(context.getResources().getColor(R.color.black));
            item.setPadding(dp2Px(20), 0, 0, 0);
            item.setGravity(Gravity.CENTER_VERTICAL);
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
            if(i+1<size){
                LinearLayout line = new LinearLayout(context);
                line.setGravity(LinearLayout.HORIZONTAL);
                line.setBackgroundColor(context.getResources().getColor(R.color.borde_gray));
                //line.setMinimumHeight(dp2Px(1));
                line.setMinimumHeight(1);
                line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                menu.addView(line);
            }
        }
        */
        setCanceledOnTouchOutside(true);

    }
    private int dp2Px(int dp){
        return  (int)(dp*context.getResources().getDisplayMetrics().density+0.5f);
    }
    private int sp2Px(float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        int px =    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, context.getResources().getDisplayMetrics());
        Log.e(TAG, "px:"+px);
        //return (int) (spValue * fontScale + 0.5f);
        return px;
    }
}
