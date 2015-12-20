package com.lessask.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.lessask.R;

import java.util.ArrayList;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
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
        for (int i=0;i<values.length;i++)
            tagView.addTag(createTag(values[i]));
        tagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if(tag.layoutColor==R.color.main_color){
                    tag.layoutColor = R.color.red_fab;
                    tag.background = new ColorDrawable(getContext().getResources().getColor(R.color.main_color));
                    Toast.makeText(getContext(), "set red:"+position, Toast.LENGTH_SHORT).show();
                }else {
                    tag.layoutColor = R.color.main_color;
                    tag.background = new ColorDrawable(getContext().getResources().getColor(R.color.gray));
                    Toast.makeText(getContext(), "set main:"+position, Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private Tag createTag(String name){
        Tag tag = new Tag(name);
        tag.tagTextColor = R.color.main_color;
        //tag.layoutColor = R.color.white;
        tag.layoutBorderColor = R.color.border_gray;
        //tag.layoutColor =  Color.parseColor("#DDDDDD");
        //tag.layoutColor = R.color.main_color;
        //tag.layoutColorPress = Color.parseColor("#555555");
        tag.background = getContext().getResources().getDrawable(R.color.background_white);

        tag.radius = 20f;
        tag.tagTextSize = 18f;
        tag.layoutBorderSize = 1f;
        //tag.layoutBorderColor = Color.parseColor("#FFFFFF");
        //tag.isDeletable = true;
        return tag;
    }

    public interface OnSelectListener {
        void onSelect(String[] data);
    }
}
