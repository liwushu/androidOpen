package com.tcl.update;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by yancai.liu on 2016/10/9.
 */

abstract class BaseTaskManager {

    private long mErrorPeriodic;
    protected Context mContext;

    BaseTaskManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private SharedPreferences getSharedPreferences() {
        return EverydayTaskManager.getInstance(mContext).getSharedPreferences();
    }

    protected abstract String getTaskName();

    // /**
    // * 任务执行周期,默认12个小时执行一次
    // *
    // * @return
    // */
    // protected long getPeriodic() {
    // return 12 * 3600 * 1000;
    // }

    /**
     * 如果出现错误，隔一定的时间再执行，默认是1个小时
     *
     * @return
     */
    protected long getErrorPeriodic() {
        return Config.ERROR_PERIODIC_PERIODIC;
    }



    public TaskStatus getTaskStatus() {
        int status = getSharedPreferences().getInt(getTaskStatusKey(), TaskStatus.None.getValue());
        return TaskStatus.valueOf(status);
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        getSharedPreferences().edit().putInt(getTaskStatusKey(), taskStatus.getValue()).apply();
    }

    String getTaskStatusKey() {
        // 年月日保证了一天执行一次
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        StringBuilder key = new StringBuilder(getTaskName());
        key.append("_taskstatus_").append(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.MONTH))
                .append(calendar.get(Calendar.DAY_OF_MONTH));
        // if (Config.isDebug()) {
        // // 5分钟请求一次
        // key.append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.MINUTE) / 2);
        // }
        return key.toString();
    }


    public boolean hasTaskToday(boolean isFromPush) {
        if (getTaskStatus() == TaskStatus.Error) {
            if (isFromPush) {
                return true;
            }

            // 如果出现错误，隔一定的时间再执行，默认是1个小时
            long now = System.currentTimeMillis() - mErrorPeriodic;
            if (now > getErrorPeriodic()) {
                mErrorPeriodic = now;
                return true;
            }
            return false;
        }
        if (isFromPush) {
            return getTaskStatus() == TaskStatus.None || getTaskStatus() == TaskStatus.Cancel
                    || getTaskStatus() == TaskStatus.Done;
        }
        return getTaskStatus() == TaskStatus.None || getTaskStatus() == TaskStatus.Cancel;
    }

    public void clearTaskStatusData() {
        getSharedPreferences().edit().clear().apply();
    }
}
