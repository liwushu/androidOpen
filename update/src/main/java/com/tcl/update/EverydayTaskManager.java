package com.tcl.update;

import android.content.Context;
import android.content.SharedPreferences;

import com.tcl.taskflows.TaskManager;
import com.tcl.update.utils.AndroidUtil;

/**
 * Created by yancai.liu on 2016/10/9.
 */

class EverydayTaskManager {

    private static final String TAG = "everyday_task_pref";
    private SharedPreferences mPrefs;

    private Context mContext;

    private static EverydayTaskManager instance;


    public SharedPreferences getSharedPreferences() {
        return mPrefs;
    }

    private EverydayTaskManager(Context context) {
        this.mContext = context;
        this.mPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public static EverydayTaskManager getInstance(Context context) {
        if (instance == null) {
            instance = new EverydayTaskManager(context);
        }
        return instance;
    }

    /**
     * 执行每天的任务
     */
    public void executeTask(boolean isFromPush) {
        // 请求今天的任务。
        TaskManager.runOnWorkerThread(new CheckUpdateTask(mContext, isFromPush));
    }

    /**
     * @return
     */
    public static long getPeriodic() {
        return Config.EVERYDAY_TASK_PERIODIC;
    }

    private static boolean isWifiOnly() {
        return true;
    }


    /**
     * 判断网络是否连接，isWifiOnly()来控制是不是需要仅仅在Wifi下下载。
     *
     * @return
     */
    public static boolean isConnect(Context context) {
        if (isWifiOnly()) {
            return AndroidUtil.isWifiConnect(context);
        }
        return AndroidUtil.isNetConnect(context);
    }

    public void onDestroy() {
        mContext = null;
        instance = null;
    }


}
