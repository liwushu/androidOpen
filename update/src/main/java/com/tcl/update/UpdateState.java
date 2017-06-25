package com.tcl.update;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;

import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadManager;
import com.tcl.update.context.ACContext;
import com.tcl.update.context.ACUncaughtExceptionHandler;
import com.tcl.update.context.DirType;
import com.tcl.update.context.FileMonitor;
import com.tcl.update.debug.LogUtils;
import com.tcl.update.framework.log.Logger;
import com.tcl.update.framework.log.NLog;

/**
 * Created by yancai.liu on 2016/10/8.
 */

class UpdateState {

    private static final String UPDATESDK_CONFIG = "updateSdk_config";
    private static final String SDK_SWITCH = "sdk_switch";
    private static final String SDK_PULL_METHOD = "sdk_pull_method";

    private static UpdateState instance;

    private FileMonitor mFileMonitor;

    private NetChangeTask mNetworkChangeReceive;

    private SharedPreferences mPrefs;

    public synchronized static UpdateState getInstance() {
        if (instance == null) {
            instance = new UpdateState();
        }
        return instance;
    }

    void init(final Context context) {
        ACContext.initInstance(context, true);

        LogUtils.showLog(context, "UpdateState->" + context == null ? "null" : "not null");


        // 初始化debug
        // Config.initConfig(context);
        initNlog(context);
        // initOALogger();

        initDownloadSDK(context);

        mFileMonitor =
                new FileMonitor(ACContext.getDirectoryPath(DirType.root), FileMonitor.DELETE | FileMonitor.DELETE_SELF);
        mFileMonitor.startWatching();


        initAlarm(context);

        initNetWorkChange(context);

    }

    private SharedPreferences getPrefs(Context context) {
        if (mPrefs == null) {
            mPrefs = context.getApplicationContext().getSharedPreferences(UPDATESDK_CONFIG, Context.MODE_PRIVATE);
        }
        return mPrefs;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    void setSwitch(Context context, boolean flag) {
        SharedPreferences prefs = getPrefs(context);
        if (prefs != null) {
            prefs.edit().putBoolean(SDK_SWITCH, flag).apply();
        }
    }

    boolean getSwitch(Context context, boolean defalutValue) {
        SharedPreferences prefs = getPrefs(context);
        if (prefs != null) {
            return prefs.getBoolean(SDK_SWITCH, defalutValue);
        }
        return defalutValue;
    }


    /**
     * 是否关闭SDK的Pull功能，true为开启（默认开启）;false为关闭(如果为false，则需要接入Push功能{@link UpdateSdkManager#updateFromPush})。pull功能是一天请求一次服务器来获取更新的功能。
     *
     * @param context
     * @param openOrClose true为开启（默认开启）;false为关闭
     */
    void setPullMethod(Context context, boolean openOrClose) {

        //分开写，cancelAlarm在putInt之前
        if (!openOrClose) {
            UpdateState.getInstance().cancelAlarm(context);
        }

        SharedPreferences prefs = getPrefs(context);
        if (prefs != null) {
            prefs.edit().putInt(SDK_PULL_METHOD, openOrClose ? 1 : 2).apply();
        }

        if (openOrClose) {
            UpdateState.getInstance().initAlarm(context);
        }
    }

    /**
     * 2.1新增加的功能，判断SDK的Pull功能是否开启
     * 0默认值，1表示开启Pull功能，2为关闭Pull功能
     *
     * @param context
     * @return
     */
    int getPullMethod(Context context) {
        SharedPreferences prefs = getPrefs(context);
        if (prefs != null) {
            return prefs.getInt(SDK_PULL_METHOD, 0);
        }
        return 0;
    }

    private void initNetWorkChange(Context context) {
        mNetworkChangeReceive = new NetChangeTask();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(mNetworkChangeReceive, intentFilter);
    }


    private void initDownloadSDK(final Context context) {
        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(4);
        configuration.setThreadNum(1);
        DownloadManager.getInstance().init(context, configuration);
    }


    private void initNlog(Context context) {
        String path = ACContext.getDirectoryPath(DirType.crash);
        // 抓取崩溃日志
        ACUncaughtExceptionHandler handler = new ACUncaughtExceptionHandler(context, path);
        handler.registerForExceptionHandler();

        if (!Config.isDebug()) {
            NLog.setDebug(false, Logger.VERBOSE);
            return;
        }
        NLog.setDebug(true, Logger.VERBOSE);
        // NLog.trace(Logger.TRACE_REALTIME, null);
        // 测试日志
        String loggerPath = ACContext.getDirectoryPath(DirType.log);
        NLog.trace(Logger.TRACE_ALL, loggerPath);
    }

    // private void initOALogger() {
    // String loggerPath = ACContext.getDirectoryPath(DirType.log);
    // if (TextUtils.isEmpty(loggerPath)) {
    // ACContext.initInstance(mContext, true);
    // return;
    // }
    // OALogger.initLogger(getApplicationContext(), loggerPath);
    // }


    public void onDestory(Context context) {

        LogUtils.showLog(context, "UpdateState->onDestory()");

        cancelAlarm(context);

        DownloadManager.getInstance().pauseAll();

        if (mFileMonitor != null) {
            mFileMonitor.stopWatching();
        }
        if (mNetworkChangeReceive != null) {
            context.unregisterReceiver(mNetworkChangeReceive);
        }
        instance = null;

        EverydayTaskManager.getInstance(context).onDestroy();
    }


    private void initAlarm(Context context) {
        if (UpdateSdkManager.hasPullMethod(context)) {
            LogUtils.showLog(context, "初始化闹钟Alarm");
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, UpdateSdkService.class);
            intent.setAction(UpdateSdkService.ACTION_EVERYDAY_TASK);
            PendingIntent mPendingIntent =
                    PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.setRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(),
                    EverydayTaskManager.getPeriodic(), mPendingIntent);
        } else {
            LogUtils.showLog(context, "not init alarm,close pull method");
        }
    }

    private void cancelAlarm(Context context) {
        if (UpdateSdkManager.hasPullMethod(context)) {
            LogUtils.showLog(context, "cancel alarm,close pull method");
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, UpdateSdkService.class);
            intent.setAction(UpdateSdkService.ACTION_EVERYDAY_TASK);
            PendingIntent mPendingIntent =
                    PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.cancel(mPendingIntent);
        }
    }


}
