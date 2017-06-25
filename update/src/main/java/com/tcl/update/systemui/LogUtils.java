package com.tcl.update.systemui;

import android.util.Log;

import com.tcl.update.BuildConfig;

/**
 * Created by dejun.xie on 2017/1/16.
 * 日志工具类
 */
public class LogUtils {

    public static final String TAG="XDJ";
    public static final int DEBUG=1;
    public static final int ERROR=2;
    public static final int WARNING=3;
    public static final int INFO=4;
    public static final int CLEAR_LOG=5;

    private LogUtils(){

    }

    public static void logXDJ(int i,String info){
        if (!BuildConfig.DEBUG) {
            return;
        }
        if(i==CLEAR_LOG){

        }else if(i==DEBUG){
            Log.d(TAG, info);
        }else if(i==ERROR){
            Log.e(TAG, info);
        }else if(i==WARNING){
            Log.w(TAG, info);
        }else if(i==INFO){
            Log.i(TAG, info);
        }else{

        }
    }
}
