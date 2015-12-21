package com.lessask.test;

import android.app.DatePickerDialog;
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
import com.lessask.action.SelectActionActivity;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.dialog.TagsPickerDialog;
import com.lessask.tag.SelectTagsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
            rootView.findViewById(R.id.customer_picker).setOnClickListener(this);
            rootView.findViewById(R.id.tags_picker).setOnClickListener(this);
            rootView.findViewById(R.id.selected_tags).setOnClickListener(this);
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
            case R.id.customer_picker:
                String[] values = {"增肌", "减脂", "塑形","胸部adadfadfaf","背部","腰部","臀部","大腿","小腿"};
                StringPickerDialog dialog = new StringPickerDialog(getContext(), values, new StringPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(String data) {
                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                break;
            case R.id.tags_picker:
                String[] values2 = {"增肌", "减脂", "塑形","胸部adadfadfaf","背部","腰部","臀部","大腿","小腿"};
                ArrayList<String> values1 = new ArrayList<>();
                for(int i=0;i<values2.length;i++)
                    values1.add(values2[i]);
                int[] selected = {2,5,6};
                TagsPickerDialog dialog1 = new TagsPickerDialog(getContext(), values1, new TagsPickerDialog.OnSelectListener() {
                    @Override
                    public void onSelect(List data) {
                        String resulte = "";
                        for(int i=0;i<data.size();i++){
                            resulte+=","+data.get(i);
                        }
                        Toast.makeText(getContext(), resulte, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog1.setSelectedList(selected, 2);
                dialog1.show();
                break;
            case R.id.date_picker:
                TimePickerDialog dialog2 = new TimePickerDialog(getContext(),null,23,45,true);
                dialog2.show();
                break;
            case R.id.selected_tags:
                intent = new Intent(getActivity(), SelectTagsActivity.class);
                startActivity(intent);
                break;
        }
    }


}
