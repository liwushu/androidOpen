package com.tcl.update.context;

import android.os.FileObserver;

import com.tcl.update.framework.log.NLog;

/**
 * Created by yancai.liu on 2016/10/8.
 */

public class FileMonitor extends FileObserver {

    public FileMonitor(String path) {
        super(path);
    }

    public FileMonitor(String path, int mask) {
        super(path, mask);
    }

    @Override
    public void onEvent(int event, String path) {
        NLog.d("FileMonitor", "ON MONITOR: %d", event);
        // if ((event & (DELETE | DELETE_SELF)) != 0) {
        // Process.killProcess(Process.myPid());
        // }
    }
}
