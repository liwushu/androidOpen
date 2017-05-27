package com.flying.test.compount;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service {
    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent ,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        Intent it = new Intent();
        it.setClass(this,TestActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        return START_STICKY;
    }
}
