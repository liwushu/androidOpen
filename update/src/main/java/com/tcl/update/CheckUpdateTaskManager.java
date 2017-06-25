package com.tcl.update;

import android.content.Context;
import android.content.SharedPreferences;

import com.tcl.update.db.UpdateInfo;
import com.tcl.update.db.UpdateProvider;

import java.util.List;

/**
 * DemonHunter 20160226
 */
public class CheckUpdateTaskManager extends BaseTaskManager {

    private static final String TAG = "check_update_pref";

    private static CheckUpdateTaskManager instance;

    private UpdateProvider mUpdateProvider;

    private SharedPreferences mPrefs;

    private CheckUpdateTaskManager(Context context) {
        super(context);
        this.mPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mUpdateProvider = new UpdateProvider(context);
    }

    public void saveTime(long time) {
        mPrefs.edit().putLong("time", time).apply();
    }

    public long getTime() {
        return mPrefs.getLong("time", 0);
    }

    public void saveCountry(String country) {
        mPrefs.edit().putString("country", country).apply();
    }

    public String getCountry() {
        return mPrefs.getString("country", "");
    }



    public static CheckUpdateTaskManager getInstance(Context context) {
        if (instance == null) {
            instance = new CheckUpdateTaskManager(context);
        }
        return instance;
    }

    /**
     * 获取下载任务
     *
     * @return
     */
    public List<UpdateInfo> getCheckUpdateTasks() {
        return mUpdateProvider.getAllData();
    }

    public void setCheckUpdateTasks(List<UpdateInfo> updateItemInfoList) {
        mUpdateProvider.saveOrUpdateAll(updateItemInfoList);
    }

    public void deleteAllCheckUpdateTasks() {
        mUpdateProvider.deleteAll();
    }

    @Override
    protected String getTaskName() {
        return TAG;
    }
}
