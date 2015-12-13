package com.lessask.sports;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by JHuang on 2015/12/13.
 */
class MyButton extends Button {
    private Button test;
    private MyButton myButton;
    private ViewGroup parent;
    public MyButton(Context context) {
            super(context);
        }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setStatusView(Button test, MyButton myButton){
        this.test = test;
        this.myButton = myButton;
        parent = (ViewGroup)getParent();
        change();
    }
    public void change(){
        parent.removeAllViews();
        parent.addView(myButton);
    }
    public void change1(){
        parent.removeAllViews();
        parent.addView(test);
    }
}
