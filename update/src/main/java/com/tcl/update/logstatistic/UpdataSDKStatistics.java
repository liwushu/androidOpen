package com.tcl.update.logstatistic;

import android.content.Context;

import com.tcl.taskflows.TaskManager;

/**
 * Created by fanyang.sz on 2016/12/9.
 */

public class UpdataSDKStatistics implements LogStatisticsEvent {


    @Override
    public void logEvent(Context context, int eventId, LogStatisticsData data) {
        LogStatisticsItem item = new LogStatisticsItem();
        item.setEv(eventId);
        // 防止data.getMessage有数据，把他的数据清空
        data.setMessage("");
        item.setData(data);
        item.setTime(System.currentTimeMillis());
        TaskManager.runOnSecondaryThread(new LogUploadTask(context, item));
    }

    @Override
    public void logException(Context context, int exception, String msg) {
        LogStatisticsItem item = new LogStatisticsItem();
        item.setEv(exception);
        LogStatisticsData data = new LogStatisticsData();
        data.setMessage(msg);
        item.setData(data);
        item.setTime(System.currentTimeMillis());
        TaskManager.runOnSecondaryThread(new LogUploadTask(context, item));
    }
}
