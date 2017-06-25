package com.tcl.update.systemui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageReceiver extends BroadcastReceiver {
    public PackageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装包更新的广播
        if(
        intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) ||
        intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
        intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String dataString = intent.getDataString();
            if(dataString.equals("package:com.android.systemui")) {
                Intent intentStart = new Intent(Intent.ACTION_REBOOT);
                //其中false换成true,会弹出是否关机的确认窗口
                intentStart.putExtra("android.intent.extra.KEY_CONFIRM", false);
                intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentStart);
                }
        }
        //接收systemui发送的解锁广播，并转发该解锁广播
        else if(intent.getAction().equals("android.tct.SYSTEMUI_TRNSMIT")) {
            Intent newIntent = new Intent();
            newIntent.setAction(intent.getStringExtra("transmit_action"));
            newIntent.setFlags(intent.getIntExtra("transmit_flag", newIntent.getFlags()));
            int profileId = intent.getIntExtra("android.tct.SYSTEMUI_USER_PROFILEID", -10);
            if(profileId != -10) {
                try {
                    //context.sendBroadcastAsUser(newIntent, UserHandle.of(profileId));
                }catch (Exception e) {
                    LogUtils.logXDJ(LogUtils.CLEAR_LOG,"onReceive -- e.msg = "+ e.getMessage());
                }
            }
            else {
                //context.sendBroadcast(newIntent);
            }
        }
    }
}
