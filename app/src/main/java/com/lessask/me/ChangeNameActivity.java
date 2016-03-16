package com.lessask.me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lessask.R;
import com.lessask.chat.Chat;
import com.lessask.chat.ChatResponseListener;
import com.lessask.dialog.LoadingDialog;
import com.lessask.global.GlobalInfos;
import com.lessask.model.ChangeName;

public class ChangeNameActivity extends AppCompatActivity {

    private int userid;
    private String name;
    private Gson gson = new Gson();
    private GlobalInfos globalInfos = GlobalInfos.getInstance();
    private String TAG = ChangeNameActivity.class.getSimpleName();


    private EditText nameView;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("更改名字");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String name = globalInfos.getUser().getNickname();

        nameView = (EditText) findViewById(R.id.name);
        nameView.setText(name);
        nameView.setSelection(name.length());
        loadingDialog = new LoadingDialog(this);

        Chat.getInstance(ChangeNameActivity.this).appendChatResponseListener("changename", new ChatResponseListener() {
            @Override
            public void response(String obj) {
                ChangeName changeName = gson.fromJson(obj, ChangeName.class);
                if(changeName.getErrno()==0 && changeName.getError()==null){
                    Intent intent = new Intent();
                    intent.putExtra("name", changeName.getName());
                    globalInfos.getUser().setNickname(changeName.getName());
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    Toast.makeText(ChangeNameActivity.this, "error:"+changeName.getError(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                String name = nameView.getText().toString();
                ChangeName changeName = new ChangeName(globalInfos.getUserId(), name);
                Chat.getInstance(ChangeNameActivity.this).emit("changename", gson.toJson(changeName));
                loadingDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
