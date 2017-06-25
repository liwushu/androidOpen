package com.tcl.update.utils;

/**
 * Created by yancai.liu on 2016/10/20.
 */

public interface InstallCallback {
    /**
     *
     * @param installType 0：普通安装；1：23以下的系统静默安装；2:24的系统静默安装;-1：安装失败；-2:APK路径为空；-3：文件不存在
     * @param apkPath
     * @param packageName
     */
    void packageInstalled(int installType, String apkPath, String packageName, String resultMsg);
}
