package com.tcl.update.logstatistic;

import android.content.Context;

/**
 * Created by fanyang.sz on 2016/12/9.
 */

public interface LogStatisticsEvent {

    void logEvent(Context context, int eventId, LogStatisticsData data);

    void logException(Context context, int exception, String msg);
}
