package com.slw.opengl.utils;

import android.util.Log;

/**
 * Created by liwu.shu on 2017/4/14.
 */

public class LogUtils {

    private static final String TAG = "flying";

    public static void logd(String msg){
        logd(TAG,msg);
    }

    public static void logd(String tag,String msg){
        Log.d(tag,msg);
    }

    public static void loge(String msg){
        loge(TAG,msg);
    }

    public static void loge(String tag,String msg){
        Log.e(tag,msg);
    }
}
