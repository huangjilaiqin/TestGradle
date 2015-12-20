package com.lessask.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.lessask.R;

import me.kaede.tagview.TagView;

/**
 * Created by huangji on 2015/12/18.
 */
public class TagsPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private OnSelectListener mSelectCallBack;
    private TagView tagView;
    private String[] values;
    private String[] selected;

    public TagsPickerDialog(Context context, String[] values, String[] selected, OnSelectListener mSelectCallBack) {
        super(context);
        this.values = values;
        this.mSelectCallBack = mSelectCallBack;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tags_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        tagView = (TagView) view.findViewById(R.id.tags_view);
        tagView.addTags(values);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case BUTTON_POSITIVE:
                if (mSelectCallBack != null) {
                    mSelectCallBack.onSelect(selected);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    public interface OnSelectListener {
        void onSelect(String[] data);
    }
}
