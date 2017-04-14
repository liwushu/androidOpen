package com.android.task;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,int flag,int startid){
        super.onStartCommand(intent,flag,startid);
        Intent it = new Intent();
        intent.setClass(this, SubActivity_LauncherMode.SubActivity_Standard.class);
        startActivity(it);
        return START_STICKY_COMPATIBILITY;
    }
}
