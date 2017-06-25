package com.tcl.update.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class TelephonyManagerUtil {

    private TelephonyManager tm;

    private static TelephonyManagerUtil telMUtil;

    private TelephonyManagerUtil(Context context) {
        if (hasReadPhonePermission(context))
            tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
    }

    private TelephonyManager getTelephonyManager() {
        return tm;
    }

    public static TelephonyManagerUtil getInstance(Context mContext) {
        if (telMUtil == null || telMUtil.getTelephonyManager() == null) {
            telMUtil = new TelephonyManagerUtil(mContext);
        }
        return telMUtil;
    }


    /*
     * 唯一的用户ID： 例如：IMSI(国际移动用户识别码) for a GSM phone. 需要权限：READ_PHONE_STATE
     */
    public String getSubscriberId() {
        if (tm == null) {
            return "";
        }
        return tm.getSubscriberId();
    }



    /**
     * 检查是否需要权限检测
     *
     * @return
     */
    public static boolean needCheckPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasReadPhonePermission(Context context) {
        if (!needCheckPermission()) return true;
        if (context == null) return false;
        int hasPermission = context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public String getSPN() {
        if (tm == null) {
            return "";
        }
        return tm.getSimOperatorName();
    }
}
