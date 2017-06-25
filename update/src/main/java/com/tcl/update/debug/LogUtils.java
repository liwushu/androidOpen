package com.tcl.update.debug;

import android.content.Context;
import android.util.Log;

import com.tcl.update.BuildConfig;
import com.tcl.update.Config;

/**
 * Created by yancai.liu on 2016/10/8.
 */

public class LogUtils {


    public static void showLog(Context context, String msg) {
        showLog(context, msg, Config.isLogDebug());
    }

    public static void showLog(Context context, String msg, boolean needShow) {
        if (needShow) {
            Log.i("CheckUpdateSDKLog", BuildConfig.VERSION_NAME + ":"
                    + (context != null ? context.getPackageName() : "null") + "->" + msg);
        }
    }
}
