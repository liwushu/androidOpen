package com.example.heaptest.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.heaptest.BuildConfig;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.Map;

public class InitUtils {
    private static final String DIR = "/sdcard/tmp";
    private static XLogImpl mXLogImpl;

    public static void initBugly(Context context) {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setAppChannel("Release");
        strategy.setUploadProcess(true);
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            @Override
            public synchronized Map<String, String> onCrashHandleStart(int i, String s, String s1, String s2) {
                return super.onCrashHandleStart(i, s, s1, s2);
            }

            @Override
            public synchronized byte[] onCrashHandleStart2GetExtraDatas(int i, String s, String s1, String s2) {
                return super.onCrashHandleStart2GetExtraDatas(i, s, s1, s2);
            }
        });

        Bugly.init(context, "1400007687", BuildConfig.DEBUG, strategy);
        // 附加信息添加到最后,最多9个
        Bugly.putUserData(context, "manufacturer", Build.MANUFACTURER);
        Bugly.putUserData(context, "model", Build.MODEL);
        Bugly.putUserData(context, "branch", "release");
        Bugly.putUserData(context, "rev", "1.0.1");
        Bugly.putUserData(context, "build", "2018-04-24");
    }


    public static void initXlog() {
        File file = new File(DIR);
        if(!file.exists()) {
            file.mkdirs();
        }
        mXLogImpl = new XLogImpl("/sdcard/tmp","log");
    }

    public static void log(String tag,String text) {
        mXLogImpl.logWriter(Log.ERROR,tag,text);
    }
}
