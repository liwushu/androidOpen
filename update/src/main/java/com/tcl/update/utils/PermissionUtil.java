package com.tcl.update.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.tcl.update.debug.LogUtils;


public class PermissionUtil {

    public static final int REQUEST_CODE_ASK_READ_PHONE_PERMISSION = 999;

    public static boolean hanPermission(Context context, String permission) {
        boolean hasPermission = context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_DENIED;
        LogUtils.showLog(context, "permission " + permission + ":" + hasPermission);
        return hasPermission;
    }
}
