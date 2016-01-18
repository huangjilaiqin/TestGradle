package com.lessask.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lessask.R;
import com.lessask.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import me.kaede.tagview.Tag;

import android.support.v7.graphics.Palette;
import android.widget.TextView;

public class PaletteActivity extends AppCompatActivity {

    private ImageView img;
    private TextView iv1;
    private TextView iv2;
    private TextView iv3;
    private TextView iv4;
    private TextView iv5;
    private TextView iv6;
    private TextView iv7;
    private String TAG = PaletteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);
        img = (ImageView) findViewById(R.id.img);
        iv1 = (TextView) findViewById(R.id.color1);
        iv2 = (TextView) findViewById(R.id.color2);
        iv3 = (TextView) findViewById(R.id.color3);
        iv4 = (TextView) findViewById(R.id.color4);
        iv5 = (TextView) findViewById(R.id.color5);
        iv6 = (TextView) findViewById(R.id.color6);
        iv7 = (TextView) findViewById(R.id.color7);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPickerIntent intent = new PhotoPickerIntent(PaletteActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                //intent.setShowGif(true);
                startActivityForResult(intent, 10);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 10:
                    ArrayList<String> images = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    Log.e(TAG, "file:" + images.get(0));
                    Bitmap bm = ImageUtil.getOptimizeBitmapFromFile(new File(images.get(0)));
                    Log.e(TAG, "getByteCount:"+bm.getByteCount());
                    img.setImageBitmap(bm);
                    /*
                    异步方式
                    Palette.from(bm).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Log.e(TAG, "color1:" + palette.getLightVibrantColor(0x000000));
                            int v = palette.getVibrantColor(0x000000);
                            int lv = palette.getLightVibrantColor(0x000000);
                            int dv = palette.getDarkVibrantColor(0x000000);
                            int m = palette.getMutedColor(0x000000);
                            int lm = palette.getLightMutedColor(0x000000);
                            int dm = palette.getDarkMutedColor(0x000000);
                            iv1.setBackgroundColor(v);
                            iv2.setBackgroundColor(lv);
                            iv3.setBackgroundColor(dv);
                            iv4.setBackgroundColor(m);
                            iv5.setBackgroundColor(lm);
                            iv6.setBackgroundColor(dm);
                            int c = v + lv + dv + m + lm + dm;
                            Log.e(TAG, "custom color:"+c);
                            iv7.setBackgroundColor(c);

                        }
                    });
                    */
                    //同步方式
                    Palette palette = Palette.from(bm).generate();
                    int v = palette.getVibrantColor(0x000000);
                    int lv = palette.getLightVibrantColor(0x000000);
                    int dv = palette.getDarkVibrantColor(0x000000);
                    int m = palette.getMutedColor(0x000000);
                    int lm = palette.getLightMutedColor(0x000000);
                    int dm = palette.getDarkMutedColor(0x000000);
                    iv1.setBackgroundColor(v);
                    iv2.setBackgroundColor(lv);
                    iv3.setBackgroundColor(dv);
                    iv4.setBackgroundColor(m);
                    iv5.setBackgroundColor(lm);
                    iv6.setBackgroundColor(dm);
                    int c = (v + lv + dv + m + lm + dm)/6;
                    iv7.setBackgroundColor(c);
                    break;
            }
        }
    }
}
