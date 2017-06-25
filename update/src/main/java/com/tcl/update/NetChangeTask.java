package com.tcl.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tcl.taskflows.TaskManager;
import com.tcl.update.debug.LogUtils;

/**
 * Created by yancai.liu on 2016/10/12.
 */

public class NetChangeTask extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * 根据SDK的mode选择不同的方式
         */
        if (EverydayTaskManager.isConnect(context)) {
            LogUtils.showLog(context, "UpdateSdkManager startEverydayTask->NetChangeTask");

            if (UpdateSdkManager.hasPullMethod(context)) {
                modePull(context);
            } else {
                modePush(context);
            }
        }
    }

    /**
     * mode = 0
     * 
     * @param context
     */
    private void modePull(Context context) {
        UpdateSdkManager.startEverydayTask(context);
    }

    /**
     * mode = 1 Push 功能：当wifi变化时，不能从服务器拉取数据，而是从下载开始
     *
     * @param context
     */
    private void modePush(Context context) {
        LogUtils.showLog(context, "pull has closed,do download", true);
        TaskManager.runOnWorkerThread(new DownloadTask(context));
    }
}
