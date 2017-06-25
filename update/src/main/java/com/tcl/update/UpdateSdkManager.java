package com.tcl.update;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tcl.taskflows.TaskManager;
import com.tcl.update.db.UpdateInfo;
import com.tcl.update.debug.LogUtils;

public class UpdateSdkManager {

    public static DownloadListener sDownloadListener;

    public static void init(Context context) {
        LogUtils.showLog(context, "UpdateSdkManager init ... SDK Vname: " + BuildConfig.VERSION_NAME + " Vcode: "
                + BuildConfig.VERSION_CODE, true);
        context.startService(new Intent(context, UpdateSdkService.class));
    }

    // flag = true : SDK能正常工作
    // flag = false : 关闭SDK的功能
    public static void setSDKSwitch(Context context, boolean flag) {
        UpdateState.getInstance().setSwitch(context, flag);
    }

    // 默认返回true
    static boolean getSDKSwitch(Context context) {
        return UpdateState.getInstance().getSwitch(context, true);
    }

    static void startEverydayTask(Context context) {
        if (hasPullMethod(context)) {
            LogUtils.showLog(context, "the switch of SDK is true", true);
            Intent intent = new Intent(context, UpdateSdkService.class);
            intent.setAction(UpdateSdkService.ACTION_EVERYDAY_TASK);
            context.startService(intent);
        } else {
            LogUtils.showLog(context, "the switch of SDK is false, don't does anything", true);
        }
    }


    public static void setDebug(boolean debug) {
        Config.setDebug(debug);
    }

    public static void setUrlDebug(boolean urlDebug) {
        Config.setUrlDebug(urlDebug);
    }

    public static void setLogDebug(boolean logDebug) {
        Config.setLogDebug(logDebug);
    }


    // ********自定义下载后的动作，如systemUI的做法*********//
    public static void init(Context context, DownloadListener downloadListener) {
        LogUtils.showLog(context, "UpdateSdkManager init ,DownloadListener,InstallListener", true);
        context.startService(new Intent(context, UpdateSdkService.class));
        sDownloadListener = downloadListener;
    }

    public static void install(Context context, UpdateInfo updateInfo, InstallListener installListener) {
        TaskManager.runOnWorkerThread(new InstallTask(context, updateInfo, installListener));
    }
    // ********自定义下载后的动作，如systemUI的做法*********//



    /**
     * 2.1增加push升级功能
     *
     * @param data <br/>
     *        update:升级系统；<br/>
     *        pullOpen:启动Pull功能<br/>
     *        pullClose:关闭Pull功能
     */
    public static void updateFromPush(Context context, String data) {
        LogUtils.showLog(context, "updateFromPush#data=" + data, true);
        if (TextUtils.isEmpty(data)) {
            return;
        }
        if ("update".equals(data)) {
            // 判断data中的数据type是否等于1
            Intent intent = new Intent(context, UpdateSdkService.class);
            intent.setAction(UpdateSdkService.ACTION_EVERYDAY_TASK);
            intent.putExtra(UpdateSdkService.EXTRA_FORM_PUSH, true);
            context.startService(intent);
        } else if ("pullOpen".equals(data)) {
            UpdateState.getInstance().setPullMethod(context, true);
        } else if ("pullClose".equals(data)) {
            UpdateState.getInstance().setPullMethod(context, false);
        }
    }

    static boolean hasPullMethod(Context context) {
        if (UpdateState.getInstance().getPullMethod(context) == 0) {
            return getSDKSwitch(context);
        } else if (UpdateState.getInstance().getPullMethod(context) == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
    *@description:设置是否开启更新SysetmUI
    *param: isEnable true:开启，false:不开启；default:false;
    *@copy-right:MIE
    */
    public static void setUpdateSystemUIEnable(boolean isEnable){
        Config.setUpdateSystemUIEnable(isEnable);
    }

}
