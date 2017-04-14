package com.android.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liwu.shu on 2017/1/6.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    protected void init(){
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("I am "+this.getClass().getSimpleName());
    }


    @OnClick({R.id.standard,R.id.singleTop,R.id.singleTask,R.id.singleInstance,
            R.id.new_task,R.id.clear_top,R.id.bring_front,R.id.clear_task,
            R.id.service})
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.standard: {
                invokeStartActivity(createIntent(SubActivity_LauncherMode.SubActivity_Standard.class));
                break;
            }
            case R.id.singleTop: {
                invokeStartActivity(createIntent(SubActivity_LauncherMode.SubActivity_SingleTop.class));
                break;
            }
            case R.id.singleTask: {
                invokeStartActivity(createIntent(SubActivity_LauncherMode.SubActivity_SingleTask.class));
                break;
            }
            case R.id.singleInstance: {
                invokeStartActivity(createIntent(SubActivity_LauncherMode.SubActivity_SingleInstance.class));
                break;
            }
            case R.id.new_task: {
                Intent intent = createIntent(SubActivity_Intent_Flag.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                invokeStartActivity(intent);
                break;
            }

            case R.id.clear_top: {
                Intent intent = createIntent(SubActivity_Intent_Flag.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                invokeStartActivity(intent);
                break;
            }

            case R.id.bring_front:{
                Intent intent = createIntent(SubActivity_Intent_Flag.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                invokeStartActivity(intent);
                break;
            }

            case R.id.clear_task:{
                Intent intent = createIntent(SubActivity_Intent_Flag.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                invokeStartActivity(intent);
                break;
            }
            case R.id.service:{
                Intent intent = new Intent();
                intent.setClass(this,MyService.class);
                startService(intent);
                break;
            }
        }
    }

    private Intent createIntent(Class<?> actClass){
        Intent intent = new Intent();
        intent.setClass(this, actClass);
        return intent;
    }

    private void invokeStartActivity(Intent intent){
        startActivity(intent);
    }
}
