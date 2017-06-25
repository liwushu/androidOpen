package com.tcl.update;

import android.content.Context;

import com.tcl.taskflows.TaskManager;
import com.tcl.update.db.UpdateInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yancai.liu on 2016/10/19.
 */

public class InstallTaskManager {

    private static List<UpdateInfo> mAllTask = new ArrayList<>();
    private static InstallTaskManager instance;


    private InstallTaskManager() {}

    public static InstallTaskManager getInstance() {
        if (instance == null) {
            instance = new InstallTaskManager();
        }
        return instance;
    }

    static boolean addTask(UpdateInfo task) {
        if (!mAllTask.contains(task)) {
            mAllTask.add(task);
            return true;
        }
        return false;
    }

    /**
     * 避免重复安装
     * 
     * @param task
     */
    static void removeTask(UpdateInfo task) {
        mAllTask.remove(task);
    }

    static void execute(Context context, UpdateInfo task) {
        if (addTask(task)) {
            TaskManager.runOnWorkerThread(new InstallTask(context, task));
        }
    }


}
