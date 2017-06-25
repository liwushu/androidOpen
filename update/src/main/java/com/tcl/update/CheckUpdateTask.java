package com.tcl.update;

import android.content.Context;

import com.tcl.taskflows.Task;
import com.tcl.taskflows.TaskManager;
import com.tcl.update.api.ApiConstants;
import com.tcl.update.context.ConfigUrl;
import com.tcl.update.db.PackageNames;
import com.tcl.update.db.PackageNamesProvider;
import com.tcl.update.db.UpdateInfo;
import com.tcl.update.db.UpdateInfoResponse;
import com.tcl.update.debug.LogUtils;
import com.tcl.update.framework.util.FileUtils;
import com.tcl.update.httpurlconnection.HttpsRequest;
import com.tcl.update.httpurlconnection.HttpsResponse;
import com.tcl.update.logstatistic.LogStatisticsKey;
import com.tcl.update.logstatistic.LogStatisticsManager;
import com.tcl.update.utils.ApkUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by yancai.liu on 2016/10/8.
 */

class CheckUpdateTask extends Task {

    PackageNamesProvider mPackageNamesProvider;

    private boolean isFromPush;

    // public CheckUpdateTask(Context context) {
    // super(context);
    // mPackageNamesProvider = new PackageNamesProvider(mContext);
    // }

    public CheckUpdateTask(Context context, boolean fromPush) {
        super(context);
        mPackageNamesProvider = new PackageNamesProvider(mContext);
        this.isFromPush = fromPush;
    }


    @Override
    protected void onExecute() throws Exception {
        try {

            if (!EverydayTaskManager.isConnect(mContext)) {
                LogUtils.showLog(mContext, "CheckUpdateTask#onExecute()->Wifi is not Connect()");
                return;
            }

            LogUtils.showLog(mContext,
                    "CheckUpdateTask->" + CheckUpdateTaskManager.getInstance(mContext).getTaskStatusKey() + ":"
                            + CheckUpdateTaskManager.getInstance(mContext).getTaskStatus(),
                    true);

            if (!CheckUpdateTaskManager.getInstance(mContext).hasTaskToday(isFromPush)) {
                if (ApkUtil.hasInstallPermission(mContext)) {
                    LogUtils.showLog(mContext, "CheckUpdateTask#onExecute()->Try DownloadTask#execute");
                    TaskManager.runOnWorkerThread(new DownloadTask(mContext));
                }
                return;
            }

            // 防止shared文件过大，进行删除
            CheckUpdateTaskManager.getInstance(mContext).clearTaskStatusData();
            CheckUpdateTaskManager.getInstance(mContext).setTaskStatus(TaskStatus.Doing);

            // 判断有没有安装权限，
            if (!ApkUtil.hasInstallPermission(mContext)) {
                // 发日志...
                LogStatisticsManager.getInstance().logException(mContext, LogStatisticsKey.LogException,
                        "No Install Permission");

                CheckUpdateTaskManager.getInstance(mContext).setTaskStatus(TaskStatus.Done);
                return;
            }

            PackageNames packageNames = mPackageNamesProvider.getData();
            if (packageNames == null) {
                LogUtils.showLog(mContext, "packageNames == null");
                HttpsResponse response = HttpsRequest.httpRequestAddParams(mContext,
                        ConfigUrl.configUrl(mContext) + "/apk/version/getPackageNames", "POST", null);
                handlerNameRequest(response);
            } else {
                LogUtils.showLog(mContext, "packageNames->" + packageNames.toJSON());
                requestUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
            CheckUpdateTaskManager.getInstance(mContext).setTaskStatus(TaskStatus.Error);
        }

    }

    private void requestUpdate() {
        HttpsResponse response = new UpdateRequest(mContext).run();
        handlerUpdateRequest(response);
    }

    private void handlerNameRequest(HttpsResponse response) {
        try {
            if (response.getCode() == HttpsResponse.SUCCESS) {
                if (response.getMsg() == null) {
                    throw new Exception();
                }
                JSONObject obj = new JSONObject(response.getMsg());
                int code = obj.getInt(ApiConstants.PARAM_CODE);
                if (code != ApiConstants.PARAM_CODE_SUCCESS) {
                    throw new Exception("code != 1");
                }
                LogUtils.showLog(mContext, "checkPackageNamesRequest onSuccess");
                PackageNames packageNames = PackageNames.fromJSONObject(obj.getJSONObject(ApiConstants.PARAM_DATA));
                mPackageNamesProvider.deleteAll();
                mPackageNamesProvider.saveOrUpdate(packageNames);

                requestUpdate();
            } else {
                throw new Exception(response.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.showLog(mContext, "checkPackageNamesRequest onFiled:" + e.getMessage());
            CheckUpdateTaskManager.getInstance(mContext).setTaskStatus(TaskStatus.Error);
        }
    }


    private void handlerUpdateRequest(HttpsResponse resp) {

        LogUtils.showLog(mContext, "CheckUpdateRequest->" + resp.getMsg(), true);
        try {
            if (resp.getCode() == HttpsResponse.SUCCESS) {
                JSONObject obj = new JSONObject(resp.getMsg());
                int code = obj.getInt(ApiConstants.PARAM_CODE);
                if (code != ApiConstants.PARAM_CODE_SUCCESS) {
                    throw new Exception("code != 1");
                }
                UpdateInfoResponse response =
                        UpdateInfoResponse.fromJSONObject(obj.getJSONObject(ApiConstants.PARAM_DATA));

                CheckUpdateTaskManager.getInstance(mContext).setTaskStatus(TaskStatus.Done);
                if (response == null)
                    throw new Exception();
                else {

                    LogStatisticsManager.getInstance().logException(mContext, LogStatisticsKey.LogException,
                            "getUpgradeApkList is OnSuccess");

                    PackageNames packageNames = mPackageNamesProvider.getData();
                    if (response.getPkgVersion() != packageNames.getPkgVersion()) {
                        LogUtils.showLog(mContext, "服务器有新的APK上线，重新请求列表", true);
                        CheckUpdateTaskManager.getInstance(mContext).setTaskStatus(TaskStatus.Error);
                        mPackageNamesProvider.deleteAll();

                        // 重新请求今天的任务。
                        TaskManager.runOnWorkerThread(new CheckUpdateTask(mContext, isFromPush));
                    } else {
                        CheckUpdateTaskManager.getInstance(mContext).saveTime(response.getTime());
                        CheckUpdateTaskManager.getInstance(mContext).saveCountry(response.getCountry());
                        // StatisticManager.buildBaseLog(mContext, response.getTime(), true);

                        if (response.getData() != null && response.getData().size() > 0) {

                            LogUtils.showLog(mContext, "CheckUpdateTask is onSuccess->" + response.getData(), true);

                            checkOldFailInfoAndDelete(response.getData());
                            CheckUpdateTaskManager.getInstance(mContext).deleteAllCheckUpdateTasks();
                            CheckUpdateTaskManager.getInstance(mContext).setCheckUpdateTasks(response.getData());

                            TaskManager.runOnWorkerThread(new DownloadTask(mContext));

                        } else {
                            LogUtils.showLog(mContext, "CheckUpdateTask is onSuccess,but " + response.getData(), true);
                        }
                    }
                }
            } else {
                throw new Exception(resp.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.showLog(mContext, "CheckUpdateTask is onFailed", true);
            CheckUpdateTaskManager.getInstance(mContext).setTaskStatus(TaskStatus.Error);

            LogStatisticsManager.getInstance().logException(mContext, LogStatisticsKey.LogException,
                    "getUpgradeApkList is onFailed : " + e.getMessage());
        }
    }

    private void checkOldFailInfoAndDelete(List<UpdateInfo> newInfos) {
        List<UpdateInfo> oldUpdateInfos = CheckUpdateTaskManager.getInstance(mContext).getCheckUpdateTasks();
        if (oldUpdateInfos != null && oldUpdateInfos.size() > 0) {
            for (UpdateInfo oldInfo : oldUpdateInfos) {
                boolean isExist = false;
                for (UpdateInfo newInfo : newInfos) {
                    if (oldInfo.getUrl().equals(newInfo.getUrl())) {
                        isExist = true;
                    }
                }
                if (!isExist) {
                    FileUtils.deleteFile(oldInfo.getPath());
                }
            }

        }

    }


}
