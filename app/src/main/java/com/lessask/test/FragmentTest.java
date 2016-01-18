package com.lessask.test;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lessask.R;
import com.lessask.dialog.StringPickerDialog;
import com.lessask.dialog.TagsPickerDialog;
import com.lessask.global.GlobalInfos;
import com.lessask.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JHuang on 2015/11/25.
 */
public class FragmentTest  extends Fragment implements View.OnClickListener{
    private View rootView;
    private File testFile;
    private String TAG = FragmentTest.class.getSimpleName();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private HashMap<Integer,Class> intentActivity = new HashMap<>();
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_test, null);
            rootView.findViewById(R.id.slider_menu).setOnClickListener(this);
            intentActivity.put(R.id.slider_menu, SlideMenuActivity.class);
            rootView.findViewById(R.id.item_touch_helper).setOnClickListener(this);
            intentActivity.put(R.id.item_touch_helper, ItemTouchHelperActivity.class);
            rootView.findViewById(R.id.storage).setOnClickListener(this);
            intentActivity.put(R.id.storage, StorageActivity.class);
            rootView.findViewById(R.id.volley).setOnClickListener(this);
            intentActivity.put(R.id.volley, TestVolleyActivity.class);
            rootView.findViewById(R.id.date_picker).setOnClickListener(this);
            rootView.findViewById(R.id.customer_picker).setOnClickListener(this);
            rootView.findViewById(R.id.tags_picker).setOnClickListener(this);
            rootView.findViewById(R.id.coordinator_layout).setOnClickListener(this);
            intentActivity.put(R.id.coordinator_layout, CoordinatorLayoutActivity.class);
            rootView.findViewById(R.id.get_pic).setOnClickListener(this);
            testFile = new File(getActivity().getExternalCacheDir(), "test.jpg");
            rootView.findViewById(R.id.status_change).setOnClickListener(this);
            intentActivity.put(R.id.status_change, ReplaceChildActivity.class);
            rootView.findViewById(R.id.dynamicload_img).setOnClickListener(this);
            intentActivity.put(R.id.dynamicload_img, DynamicLoadImgActivity.class);
            rootView.findViewById(R.id.palette).setOnClickListener(this);
            intentActivity.put(R.id.palette,PaletteActivity.class);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
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
            case R.id.get_pic:
                /*
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.putExtra("output", Uri.fromFile(testFile));
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);// 裁剪框比例
                intent.putExtra("aspectY", 1);
                startActivityForResult(intent, 100);
                */
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
                break;
            default:
                intent = new Intent(getActivity(), intentActivity.get(v.getId()));
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 100:
                    //Bitmap bmp = Utils.getOptimizeBitmapFromFile(testFile);
                    Uri originalUri = data.getData();
                    Log.e(TAG, "uri:"+data.getData());
                    String[] proj = {MediaStore.Images.Media.DATA};

                    //好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = getActivity().getContentResolver().query(originalUri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    Log.e(TAG, "path:"+path);

                    File inFile = new File(path);
                    Log.e(TAG, "orign size:"+inFile.length()/1024.0);
                    //Bitmap bmp = Utils.getOptimizeBitmapFromFile(inFile, 200, 200);
                    Bitmap bmp = ImageUtil.getOptimizeBitmapFromFile(inFile, globalInfos.getScreenWidth(), globalInfos.getScreenHeight());
                    File outFile = new File(getActivity().getExternalCacheDir(), "out.jpg");
                    try {
                        ImageUtil.setBitmap2File(outFile, bmp);
                    }catch (IOException e){
                        Log.e(TAG, "IOException:"+e.getMessage());
                    }
                    Log.e(TAG, "opti size:"+outFile.length()/1024.0);
                    Log.e(TAG, "file rate:"+((float)inFile.length())/outFile.length());
                    break;
            }
        }
    }
}
