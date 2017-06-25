package com.tcl.update.utils;

import android.content.Context;
import android.text.TextUtils;

public class MetaUtils {
    public static final String REGION = "REGION";
    public static final String SCOPE = "SCOPE";
    public static final String DOMAIN_VERSION = "DOMAIN_VERSION";
    public static final String PRELOADED = "PRELOADED";
    public static final String PPRLOADED_BOOT_COUNT = "PPRLOADED_BOOT_COUNT";

    /**
     * 预装版本达到自动更新条件的最少启动次数默认值
     */
    private static final int PPRLOADED_BOOT_COUNT_DEFAULT = 2;
    private static final boolean PRELOADED_DEFAULT = true;

    /**
     * 获取预装版本最少启动次数
     */
    public static int getBootCountLimit(Context context) {
        String bootCount = ContextUtils.getMetaData(context, PPRLOADED_BOOT_COUNT);
        if (TextUtils.isEmpty(bootCount)) return PPRLOADED_BOOT_COUNT_DEFAULT;
        return Integer.parseInt(bootCount);
    }

    public static boolean checkUpdateCount(Context context, int count) {
        int bootCountLimit = getBootCountLimit(context);
        return count > bootCountLimit;
    }


    public static boolean isPreloaded(Context context) {
        String preloaded = ContextUtils.getMetaData(context, PRELOADED);
        if (TextUtils.isEmpty(preloaded)) return PRELOADED_DEFAULT;
        return Boolean.parseBoolean(preloaded);
    }
}
