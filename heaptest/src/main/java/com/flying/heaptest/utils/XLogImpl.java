package com.example.heaptest.utils;


import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

public class XLogImpl{

    static {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");
    }

    public XLogImpl(String logDir, String prex) {
        String cachePath = logDir + "/" + "cache";
        Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logDir, prex, "");
        Xlog.setConsoleLogOpen(false);
        com.tencent.mars.xlog.Log.setLogImp(new Xlog());
    }

    public void logWriter(int lvl, String tag, String text) {
        switch (lvl) {
            case Log.LEVEL_DEBUG:
                com.tencent.mars.xlog.Log.d(tag, text);
                break;
            case Log.LEVEL_ERROR:
                com.tencent.mars.xlog.Log.e(tag, text);
                break;
            case Log.LEVEL_INFO:
                com.tencent.mars.xlog.Log.i(tag, text);
                break;
            case Log.LEVEL_VERBOSE:
                com.tencent.mars.xlog.Log.v(tag, text);
                break;
            case Log.LEVEL_WARNING:
                com.tencent.mars.xlog.Log.w(tag, text);
                break;
        }
    }

    public void setConsoleLogAndLevel(boolean open, int level) {
        Xlog.setConsoleLogOpen(open);
        Xlog.setLogLevel(level);
    }


    public void uninit() {
        com.tencent.mars.xlog.Log.appenderClose();
    }

    public void flush(boolean isSync) {
        com.tencent.mars.xlog.Log.appenderFlush(isSync);
    }
}

