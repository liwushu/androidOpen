package com.tcl.update.framework.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.StatFs;
import android.view.View;

public class AndroidNewApi {

    public static boolean IsSDKLevelAbove(int nLevel) {
        return android.os.Build.VERSION.SDK_INT >= nLevel;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getBlockSizeLong(StatFs stat) {
        if (IsSDKLevelAbove(18)) {
            return stat.getBlockSizeLong();
        } else {
            return stat.getBlockSize();
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getAvailableBlocks(StatFs stat) {
        if (IsSDKLevelAbove(18)) {
            return stat.getAvailableBlocksLong();
        } else {
            return stat.getAvailableBlocks();
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBackground(View view, Drawable background) {
        if (IsSDKLevelAbove(16)) {
            view.setBackground(null);
        } else {
            view.setBackgroundDrawable(null);
        }
    }

}
