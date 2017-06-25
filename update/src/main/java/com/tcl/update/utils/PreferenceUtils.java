package com.tcl.update.utils;

import android.content.Context;

import com.tcl.update.framework.util.PrefsUtils;


public class PreferenceUtils {

    public final static String PRE_BOOT_COUNT = "boot_count";
    public final static String PRE_LAST_CHECK_UPDATE_TIME = "last_check_update_time";

    /**
     * 保存启动次数（自升级界限判断，并非准确次数）
     * 
     * @param context
     * @param count
     */
    public static void saveBootCount(Context context, int count) {
        PrefsUtils.savePrefInt(context, PRE_BOOT_COUNT, count);
    }

    /**
     * 获取启动次数（自升级界限判断，并非准确次数）
     * 
     * @param context
     * @return
     */
    public static int loadBootCount(Context context) {
        return PrefsUtils.loadPrefInt(context, PRE_BOOT_COUNT, 0);
    }

    /**
     * 保存最后的检查更新时间
     * 
     * @param context
     * @param time
     */
    public static void saveLastCheckUpdateTime(Context context, long time) {
        PrefsUtils.savePrefLong(context, PRE_LAST_CHECK_UPDATE_TIME, time);
    }

    /**
     * 获取最后的检查更新时间
     * 
     * @param context
     * @return
     */
    public static long loadLastCheckUpdateTime(Context context) {
        return PrefsUtils.loadPrefLong(context, PRE_LAST_CHECK_UPDATE_TIME, 0);
    }


}
