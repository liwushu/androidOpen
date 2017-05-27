package com.flying.test.notification;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.flying.test.utils.LogUtils;


/**
 *
 * Created by cbf on 17/2/22.
 */

public class LockNotificationListenerService extends NotificationListenerService {
    private static final String TAG = "flying";

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.logd(TAG, "onBind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.logd(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.logd(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logd(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        LogUtils.logd(TAG, "onNotificationPosted");
    }

    @Override
    public void onNotificationRemoved(final StatusBarNotification sbn) {

    }

}
