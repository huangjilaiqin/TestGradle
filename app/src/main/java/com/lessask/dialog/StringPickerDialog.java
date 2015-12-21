package com.lessask.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.lessask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangji on 2015/12/18.
 */
public class StringPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private OnSelectListener mSelectCallBack;
    private NumberPicker numberPicker;
    private String[] values;


    public StringPickerDialog(Context context, String[] values, OnSelectListener mSelectCallBack) {
        super(context);
        this.values = values;
        this.mSelectCallBack = mSelectCallBack;
        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.string_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        numberPicker = (NumberPicker) view.findViewById(R.id.picker);
        numberPicker.setDisplayedValues(values);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length-1);
        numberPicker.setValue(2);
        numberPicker.setDividerDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.main_color)));

    }
    public StringPickerDialog(Context context, ArrayList<String> listValues, OnSelectListener mSelectCallBack) {
        super(context);
        this.values = listValues.toArray(new String[]{});
        this.mSelectCallBack = mSelectCallBack;
        init(context);
    }

    public void setValue(int index){
        numberPicker.setValue(index);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case BUTTON_POSITIVE:
                if (mSelectCallBack != null) {
                    mSelectCallBack.onSelect(values[numberPicker.getValue()]);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    public interface OnSelectListener {
        void onSelect(String data);
    }
}
