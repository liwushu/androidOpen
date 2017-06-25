package com.tcl.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.tcl.taskflows.Task;
import com.tcl.update.db.UpdateInfo;
import com.tcl.update.db.UpdateProvider;
import com.tcl.update.debug.LogUtils;
import com.tcl.update.framework.util.FileUtils;
import com.tcl.update.logstatistic.LogStatisticsData;
import com.tcl.update.logstatistic.LogStatisticsKey;
import com.tcl.update.logstatistic.LogStatisticsManager;
import com.tcl.update.utils.ApkUtil;


/**
 * Created by yancai.liu on 2016/10/13.
 */

class InstallTask extends Task {

    private UpdateInfo info;

    private InstallListener mInstallListener;

    public InstallTask(Context context, UpdateInfo info) {
        super(context);
        this.info = info;
    }

    public InstallTask(Context context, UpdateInfo info, InstallListener installListener) {
        super(context);
        this.info = info;
        this.mInstallListener = installListener;
    }

    @Override
    protected void onExecute() throws Exception {
        if (info == null || TextUtils.isEmpty(info.getPkgName())) {
            return;
        }
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(info.getPkgName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                if (info.getVcode() > packageInfo.versionCode) {
                    install();
                } else {
                    deleteDBInfo();
                    InstallTaskManager.removeTask(info);
                }
                return;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (info.isNeedInstall()) {
            LogUtils.showLog(mContext,
                    "execute()->isNeedInstall()->PkgName:" + info.getPkgName() + "  version:" + info.getVname());
            install();
        } else {
            deleteDBInfo();
            InstallTaskManager.removeTask(info);
        }
    }


    /**
     * 安装APK 判断apk是否运行：这个需要应用push到priv-app文件夹，签名直接安装、app目录都不行
     */
    private void install() {
        if (ApkUtil.isFileExist(info.getPath())) {
            try {
                if (waitForInstall()) {

                    boolean isInstalled = ApkUtil.isAppInstalled(mContext, info.getPkgName());

                    ApkUtil.InstallResult installResult = ApkUtil.install(mContext, info.getPath(), info.getPkgName());

                    InstallTaskManager.removeTask(info);

                    LogUtils.showLog(mContext, "install()->安装类型为:" + installResult.getCode() + " " + info.getPkgName()
                            + " version:" + info.getVname() + installResult.getMessage(), true);

                    // 操作日志
                    LogStatisticsData data = new LogStatisticsData();
                    data.initLogData(info);

                    data.setApkId(info.getId());
                    if (installResult.getCode() == ApkUtil.INSTALL_SUCCESS
                            || installResult.getCode() == ApkUtil.INSTALL_SUCCESS_24) {
                        if (info.isNeedInstall() && !isInstalled) {
                            data.setOperType(LogStatisticsKey.operType_NewInstall);
                            data.setOperResult(LogStatisticsKey.operResult_Success);
                        } else {
                            data.setOperType(LogStatisticsKey.operType_Update);
                            data.setOperResult(LogStatisticsKey.operResult_Success);
                        }

                        if (mInstallListener != null) {
                            mInstallListener.onSuccess();
                        }

                    } else {
                        if (info.isNeedInstall()) {
                            data.setOperType(LogStatisticsKey.operType_NewInstall);
                            data.setOperResult(LogStatisticsKey.operResult_Fail);
                        } else {
                            data.setOperType(LogStatisticsKey.operType_Update);
                            data.setOperResult(LogStatisticsKey.operResult_Fail);
                        }

                        data.setErrorCode(installResult.getCode());
                        data.setMessage(installResult.getMessage());

                        if (mInstallListener != null) {
                            mInstallListener.onFailed(installResult.getCode(), installResult.getMessage());
                        }

                    }
                    data.initLogData(info);
                    LogStatisticsManager.getInstance().logEvent(mContext, LogStatisticsKey.LogEventID, data);

                    if (installResult.getCode() == ApkUtil.INSTALL_SUCCESS
                            || installResult.getCode() == ApkUtil.INSTALL_SUCCESS_24) {
                        deleteDBInfo();
                        FileUtils.deleteFile(info.getPath());
                    } else {
                        // 出错了，删除APK包，不会重复安装。
                        FileUtils.deleteFile(info.getPath());
                    }

                    return;
                } else {
                    if (!ApkUtil.isSystemApp(mContext)) {
                        LogUtils.showLog(mContext,
                                info.getPkgName() + "apk isn't system app, don't judge whether it running or not",
                                true);
                    } else {
                        LogUtils.showLog(mContext, info.getPkgName() + " isRunning!!! is top activity!!", true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InstallTaskManager.removeTask(info);
    }


    private boolean waitForInstall() {
        return mInstallListener != null || !ApkUtil.isSystemApp(mContext)
                || !ApkUtil.isApkRunning(mContext, info.getPkgName());
    }

    /**
     * 删除db表保存的下载信息 有两种情况需要：1.安装成功 2.版本号小于现有版本未能安装
     */
    private void deleteDBInfo() {
        LogUtils.showLog(mContext, "deleteDBInfo()->" + info.getId());
        new UpdateProvider(mContext).deleteById(String.valueOf(info.getId()));
    }


}
