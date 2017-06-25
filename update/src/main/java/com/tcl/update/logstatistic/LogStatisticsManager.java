package com.tcl.update.logstatistic;

import com.tcl.statistic.StatisticsSDK;

public class LogStatisticsManager {

    private static MultiStatisticsEvent sMultiStatisticsEvent;

    public static MultiStatisticsEvent getInstance() {
        if (sMultiStatisticsEvent == null) {
            sMultiStatisticsEvent = new MultiStatisticsEvent();
            init();
        }
        return sMultiStatisticsEvent;
    }

    public static void init() {
        getInstance().clear();
        getInstance().addAnalyticsLogger(new UpdataSDKStatistics());
        // getInstance().addAnalyticsLogger(new FlurryAnalytics(context));
        getInstance().addAnalyticsLogger(new StatisticsSDK());
    }

}
