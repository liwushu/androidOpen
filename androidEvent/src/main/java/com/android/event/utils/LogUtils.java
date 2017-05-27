package com.android.event.utils;

import android.util.Log;

/**
 * Created by liwu.shu on 2016/12/30.
 */

public class LogUtils {
    private final static  String TAG = "flying";

    public static void logd(String tag,String msg){
        Log.d(TAG,tag+": "+msg);
    }
}
