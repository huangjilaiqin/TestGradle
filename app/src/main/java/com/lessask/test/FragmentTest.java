package com.lessask.test;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lessask.R;
import com.lessask.dialog.StringPickerDialog;

import java.util.Calendar;

/**
 * Created by JHuang on 2015/11/25.
 */
public class FragmentTest  extends Fragment implements View.OnClickListener{
    private View rootView;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_test, null);
            rootView.findViewById(R.id.slider_menu).setOnClickListener(this);
            rootView.findViewById(R.id.item_touch_helper).setOnClickListener(this);
            rootView.findViewById(R.id.storage).setOnClickListener(this);
            rootView.findViewById(R.id.volley).setOnClickListener(this);
            rootView.findViewById(R.id.date_picker).setOnClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.slider_menu:
                intent = new Intent(getActivity(), SlideMenuActivity.class);
                startActivity(intent);
                break;
            case R.id.item_touch_helper:
                intent = new Intent(getActivity(), ItemTouchHelperActivity.class);
                startActivity(intent);
                break;
            case R.id.storage:
                intent = new Intent(getActivity(), StorageActivity.class);
                startActivity(intent);
                break;
            case R.id.volley:
                intent = new Intent(getActivity(), TestVolleyActivity.class);
                startActivity(intent);
                break;
            case R.id.date_picker:
                StringPickerDialog dialog = new StringPickerDialog(getContext(), new StringPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(String data) {
                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                break;
        }
    }


}
