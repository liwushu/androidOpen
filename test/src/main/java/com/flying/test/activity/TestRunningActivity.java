package com.flying.test.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.flying.test.R;

import java.util.List;

public class TestRunningActivity extends Activity implements View.OnClickListener{

    Button btnClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_running);
        initViews();
    }

    private void initViews(){
        btnClick = (Button)findViewById(R.id.btn_click);
        btnClick.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_click:
                invokeClick();
                break;
        }
    }

    private void invokeClick(){
        isProessRunning("");
        isServiceRunning("");
        getRunningTask();
    }

    public boolean isProessRunning(String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : lists){
            Log.i("isProessRunning: ", ""+info.processName);
            if(info.processName.equals(proessName)){
                Log.i("Service2进程", ""+info.processName);
                isRunning = true;
            }
        }

        return isRunning;
    }

    public boolean isServiceRunning( String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> lists = am.getRunningServices(30);

        for (ActivityManager.RunningServiceInfo info : lists) {//判断服务
            Log.i("isServiceRunning", ""+info.service.getClassName());
            if(info.service.getClassName().equals(serviceName)){
                Log.i("Service1进程", ""+info.service.getClassName());
                isRunning = true;
            }
        }
        return isRunning;
    }

    public boolean getRunningTask() {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> lists = am.getRunningTasks(30);
        Log.i("getRunningTask","lists: "+lists);
        for (ActivityManager.RunningTaskInfo info : lists) {//判断服务
            Log.i("isServiceRunning", ""+info.topActivity);
        }
        return isRunning;
    }
}
