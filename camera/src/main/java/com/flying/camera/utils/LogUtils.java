package com.flying.camera.utils;

import android.util.Log;

/**
 * Created by liwushu on 2017/8/13.
 */

public class LogUtils {

    private static final String TAG = "sliver";
    private static final boolean IS_LOGABLE= true;

    public static void e(String msg){
        Log.e(TAG,msg);
    }
}
