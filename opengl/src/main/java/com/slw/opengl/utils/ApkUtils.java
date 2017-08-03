package com.slw.opengl.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.text.TextUtils;

/**
 * Created by liwushu on 2017/7/31.
 */

public class ApkUtils {

    public static boolean isSupportOpengl(Context mc){
        if(!checkGLSurfaceViewSupport(mc))
            return false;

        return true;
    }

    private static boolean checkGLSurfaceViewSupport(Context mc){
        ActivityManager activityManager = (ActivityManager)mc.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        if(isAndroidVirtualMachine())
            return true;
        if(0x20000 <= configurationInfo.reqGlEsVersion){
            return true;
        }
        return false;
    }

    public static boolean isAndroidVirtualMachine(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            return false;
        String fingerPrint = Build.FINGERPRINT;
        String model = Build.MODEL;
        LogUtils.logd("fingerPrint: "+fingerPrint+"  model: "+model);
        if(!TextUtils.isEmpty(fingerPrint)){
            if(fingerPrint.startsWith("generic")
                    || fingerPrint.startsWith("unknown"))
                return true;
        }

        LogUtils.logd("model.contains(\"Android SDK built for x86\"):"+model.contains("Android SDK built for x86"));
        if(!TextUtils.isEmpty(model)){
            if(model.contains("google_sdk")||
                    model.contains("Emulator")||
                    model.contains("genymotion")||
                    model.contains("Android SDK built for x86")
                    )
                return true;
        }

        return false;
    }
}
