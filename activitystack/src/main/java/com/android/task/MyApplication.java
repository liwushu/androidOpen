package com.android.task;

import android.app.Application;
import android.util.Log;

/**
 * Created by shuliwu on 2018/3/22.
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static long appTime = 0L;

    @Override
    public void onCreate() {
        super.onCreate();
        initTime();
    }

    private void initTime() {
        appTime = System.currentTimeMillis();
    }

    public static void logTime(String msg) {
        long time = System.currentTimeMillis() - appTime;
        Log.e(TAG,msg+" time: "+time);
    }

}
