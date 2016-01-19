package com.lessask.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.R;
import com.lessask.net.VolleyHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class CircleImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_image);

        CircleImageView head = (CircleImageView)findViewById(R.id.profile_image);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(head, 0, 0);
        String imgUrl = "http://123.59.40.113/imgs/1_1453212840912_3.jpg!200_200";
        VolleyHelper.getInstance().getImageLoader().get(imgUrl, listener,200,200);
        ProgressBar bar = (ProgressBar)findViewById(R.id.bar);
    }
}
