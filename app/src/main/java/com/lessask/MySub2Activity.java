package com.lessask;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;


public class MySub2Activity extends Activity {

    private static final String[] strs = new String[]{"first", "second", "third", "fourth", "fifth","six", "seven"};
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sub2);

        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, strs));
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
