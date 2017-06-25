package com.tcl.update;

/**
 * Created by yancai.liu on 2016/10/19.
 */

public class Config {

    private static boolean debug;

    private static boolean urlDebug;

    private static boolean logDebug;

    //update SystemUI add by flying
    private static boolean updateSystemUIEnable = false;

    /**
     * 正式版本1个小时请求一次
     */
    public final static int EVERYDAY_TASK_PERIODIC = 3600000;

    /**
     * 请求失败，一个小时后再请求。
     */
    public final static int ERROR_PERIODIC_PERIODIC = 3600000;


    public static boolean isDebug() {
        return debug;
    }

    public static boolean isUrlDebug() {
        return urlDebug;
    }

    public static boolean isLogDebug() {
        return logDebug;
    }

    static void setDebug(boolean debug) {
        Config.debug = debug;
    }

    static void setUrlDebug(boolean urlDebug) {
        Config.urlDebug = urlDebug;
    }

    static void setLogDebug(boolean logDebug) {
        Config.logDebug = logDebug;
    }

    // public static void initConfig(Context context) {
    // String debug = ContextUtils.getMetaData(context, "DEBUG");
    // String urlDebug = ContextUtils.getMetaData(context, "URLDEBUG");
    // String logDebug = ContextUtils.getMetaData(context, "LOGDEBUG");
    //
    // if (!TextUtils.isEmpty(debug)) {
    // debug = "true".equals(debug);
    // } else {
    // debug = false;
    // }
    //
    // if (!TextUtils.isEmpty(urlDebug)) {
    // urlDebug = "true".equals(urlDebug);
    // } else {
    // urlDebug = false;
    // }
    //
    // if (!TextUtils.isEmpty(logDebug)) {
    // logDebug = "true".equals(logDebug);
    // } else {
    // logDebug = false;
    // }
    //
    // LogUtils.showLog(context, "debug = " + debug, true);
    // LogUtils.showLog(context, "urlDebug = " + urlDebug, true);
    // LogUtils.showLog(context, "logDebug = " + logDebug, true);
    // }

    public static void setUpdateSystemUIEnable(boolean isEnable){
        updateSystemUIEnable = isEnable;
    }

    public static boolean isUpdateSystemUIEnable(){
        return updateSystemUIEnable;
    }
}
