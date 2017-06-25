package com.tcl.update.logstatistic;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tcl.update.debug.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MultiStatisticsEvent implements LogStatisticsEvent {

    private static final int MAX_LOGEXCEPTION = 10;
    private static final int MAX_LOGEVENT = 20;

    private String LOGSHARED = "logShared";
    private String LOGDAY = "logDay";
    private String COUNT_LOGEXCEPTION = "countOfToday";
    private String COUNT_LOGEVENT = "count_logevent";

    private SharedPreferences preferences;

    private List<LogStatisticsEvent> mList = new ArrayList<>(2);

    void addAnalyticsLogger(LogStatisticsEvent logger) {
        mList.add(logger);
    }

    void clear() {
        mList.clear();
    }


    @Override
    public void logEvent(Context context, int eventId, LogStatisticsData data) {
        if (countLog(context, COUNT_LOGEVENT + eventId, MAX_LOGEVENT)) {
            for (LogStatisticsEvent logger : mList) {
                logger.logEvent(context, eventId, data);
            }
        } else {
            LogUtils.showLog(context, "logEvent不予上报, 超过了" + MAX_LOGEVENT);
        }
    }

    @Override
    public void logException(Context context, int exception, String msg) {
        if (countLog(context, COUNT_LOGEXCEPTION, MAX_LOGEXCEPTION)) {
            for (LogStatisticsEvent logger : mList) {
                logger.logException(context, exception, msg);
            }
        } else {
            LogUtils.showLog(context, "logEvent不予上报, 超过了" + MAX_LOGEXCEPTION);
        }
    }

    public SharedPreferences getSharedPre(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(LOGSHARED, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    private synchronized boolean countLog(Context context, String key, int max) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        StringBuilder today = new StringBuilder().append(calendar.get(Calendar.YEAR))
                .append(calendar.get(Calendar.MONTH)).append(calendar.get(Calendar.DAY_OF_MONTH));

        String lastDay = getSharedPre(context).getString(LOGDAY, null);

        if (TextUtils.isEmpty(lastDay)) {
            getSharedPre(context).edit().putString(LOGDAY, today.toString()).commit();
            getSharedPre(context).edit().putInt(key, 1).commit();
            return true;
        } else if (lastDay.equals(today.toString())) {
            // 同一天
            int count = getSharedPre(context).getInt(key, 0);
            if (count < max) {
                getSharedPre(context).edit().putInt(key, count + 1).commit();
                return true;
            } else {
                return false;
            }
        } else {
            // 不同一天
            getSharedPre(context).edit().putString(LOGDAY, today.toString()).commit();
            getSharedPre(context).edit().putInt(key, 1).commit();
            return true;
        }
    }


}
