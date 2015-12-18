package com.lessask.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.lessask.R;

/**
 * Created by huangji on 2015/12/18.
 */
public class StringPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private OnSelectListener mSelectCallBack;
    private NumberPicker numberPicker;
    private String[] values = {"增肌", "减脂", "塑形","胸部","背部","腰部","臀部","大腿","小腿"};

    public StringPickerDialog(Context context, OnSelectListener mSelectCallBack) {
        super(context, R.style.nothing_dialog);
        this.mSelectCallBack = mSelectCallBack;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.string_picker, null);
        setContentView(view);

        numberPicker = (NumberPicker) findViewById(R.id.picker);
        numberPicker.setDisplayedValues(values);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length-1);
        numberPicker.setValue(2);
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
