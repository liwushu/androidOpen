package com.example.camera.utils;

import android.util.Log;

/**
 * Created by shuliwu on 2018/3/26.
 */

public class LaunchUtil {
    private static final String TAG = "LaunchUtil";
    private static long sLaunchTime = 0;

    public static void init() {
        sLaunchTime = System.currentTimeMillis();
    }

    public static void logTime(String msg) {
        long time = System.currentTimeMillis() - sLaunchTime ;
        Log.w(TAG,msg+" time: ["+time+"ms]");
    }
}
