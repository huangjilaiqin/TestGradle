package com.lessask.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lessask.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangji on 2015/12/18.
 */
public class TagsPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private String TAG = TagsPickerDialog.class.getSimpleName();
    private OnSelectListener mSelectCallBack;
    private ArrayList<String> values;
    private TagAdapter<String> tagAdapter;
    private TagFlowLayout tagFlowLayout;
    private int maxSelected = -1;
    private TextView tip;

    public TagsPickerDialog(Context context, ArrayList<String> values, OnSelectListener mSelectCallBack) {
        super(context);
        this.values = values;
        this.mSelectCallBack = mSelectCallBack;
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tags_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        tagFlowLayout = (TagFlowLayout) view.findViewById(R.id.tag_flowlayout);
        tip = (TextView) view.findViewById(R.id.tip);

        tagAdapter = new TagAdapter<String>(values) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) inflater.inflate(R.layout.tv,
                        tagFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        };

        tagFlowLayout.setAdapter(tagAdapter);
    }

    public void setSelectedList(int[] selected, int maxSelected){
        this.maxSelected = maxSelected;
        tip.setText("*最多选择"+maxSelected+"项");
        tagFlowLayout.setMaxSelectCount(maxSelected);
        Log.e(TAG, "selected:"+selected.toString());
        if(selected.length<maxSelected) {
            tagAdapter.setSelectedList(selected);
        }else {
            for(int i=0;i<selected.length;i++) {
                if(maxSelected==-1 || i<maxSelected){
                    tagAdapter.setSelectedList(selected[i]);
                    Log.e(TAG, "selected:"+selected[i]);
                }else {
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case BUTTON_POSITIVE:
                if (mSelectCallBack != null) {
                    Iterator<Integer> iterator = tagFlowLayout.getSelectedList().iterator();
                    List<String> result = new ArrayList<>();
                    Log.e(TAG, values.toString());
                    while (iterator.hasNext()){
                        int i = iterator.next();
                        if(i<0)
                            continue;
                        Log.e(TAG, "i:"+i);
                        result.add(values.get(i));
                    }
                    mSelectCallBack.onSelect(result);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    public interface OnSelectListener {
        void onSelect(List data);
    }
}
