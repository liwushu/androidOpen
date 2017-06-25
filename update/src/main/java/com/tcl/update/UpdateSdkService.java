package com.tcl.update;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.tcl.taskflows.TaskManager;
import com.tcl.update.systemui.UpdateSystemUIService;

/**
 * Created by fanyang.sz on 2016/9/28.
 */

public class UpdateSdkService extends Service {

    final static String ACTION_EVERYDAY_TASK = "com.tcl.update.UpdateSdkManager.action_everyday_task";

    final static String ACTION_EVERYDAY_TIPS_TASK = "com.tcl.update.UpdateSdkManager.action_everyday_tips_task";

    final static String EXTRA_FORM_PUSH = "extra_form_push";

    // final static String ACTION_PUSH_UPDATE_TASK =
    // "com.tcl.update.UpdateSdkManager.action_push_date";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UpdateState.getInstance().init(getApplicationContext());
        TaskManager.runOnSecondaryThread(new CheckOldInfoTask(this));

        //update SystemUI add modify by flying
        if(Config.isUpdateSystemUIEnable())
            startService(new Intent(this, UpdateSystemUIService.class));
    }

    @Override
    public void onDestroy() {
        UpdateState.getInstance().onDestory(getApplicationContext());
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (ACTION_EVERYDAY_TASK.equals(action)) {

                if (intent.hasExtra(EXTRA_FORM_PUSH)) {
                    EverydayTaskManager.getInstance(getApplicationContext()).executeTask(true);
                } else {
                    EverydayTaskManager.getInstance(getApplicationContext()).executeTask(false);
                }


            } else if (ACTION_EVERYDAY_TIPS_TASK.equals(action)) {

                TipsInfo info = (TipsInfo) intent.getSerializableExtra("tipsinfo");

                TaskManager.runOnWorkerThread(new TipsDownloadTask(getApplicationContext(), info,
                        new TipsDownloadCallBack(getApplicationContext(), info)));
            }
        }
        return START_STICKY;
    }


}
