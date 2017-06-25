package com.tcl.update.systemui;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dejun.xie on 2016/12/30.
 * SharedPreference的工具类
 */
public class SharePreferenceUtils {

    public static String TYPE_SDK_UPDATE_TIME="sdkUpdate";//上次弹框出现的时间所对应的“键”
    public static final String LAST_POP_TIME="lastTime";//上次弹框出现的时间所对应的“值”

    private SharePreferenceUtils(){

    }

    public static void recordTime(Context mContext){
        SharedPreferences sp= mContext.getSharedPreferences(TYPE_SDK_UPDATE_TIME,Context.MODE_PRIVATE);
        long nowTime=System.currentTimeMillis();
        sp.edit().putLong(LAST_POP_TIME,nowTime).apply();
    }

    public static long getLastTime(Context mContext){
        SharedPreferences sp= mContext.getSharedPreferences(TYPE_SDK_UPDATE_TIME,Context.MODE_PRIVATE);
        return sp.getLong(LAST_POP_TIME,0);
    }
}
