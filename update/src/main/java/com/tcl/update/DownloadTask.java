package com.tcl.update;

import android.content.Context;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadInfo;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.tcl.taskflows.Task;
import com.tcl.update.context.ACContext;
import com.tcl.update.context.DirType;
import com.tcl.update.db.UpdateInfo;
import com.tcl.update.db.UpdateProvider;
import com.tcl.update.debug.LogUtils;
import com.tcl.update.logstatistic.LogStatisticsData;
import com.tcl.update.logstatistic.LogStatisticsKey;
import com.tcl.update.logstatistic.LogStatisticsManager;
import com.tcl.update.utils.ApkUtil;

import java.io.File;
import java.util.List;


/**
 * Created by yancai.liu on 2016/10/8.
 */

class DownloadTask extends Task {

    private UpdateProvider mUpdateProvider;

    public DownloadTask(Context context) {
        super(context);
        mUpdateProvider = new UpdateProvider(context);
    }

    @Override
    protected void onExecute() throws Exception {
        try {
            List<UpdateInfo> mUpdateItemInfoList = mUpdateProvider.getAllData();
            if (mUpdateItemInfoList != null && mUpdateItemInfoList.size() > 0) {
                File file = new File(ACContext.getDirectoryPath(DirType.apps));
                if (!file.exists()) {
                    LogUtils.showLog(mContext,
                            "Path is null, create new Folder:" + ACContext.getDirectoryPath(DirType.apps));
                    file.mkdirs();
                }
                for (int i = 0; i < mUpdateItemInfoList.size(); i++) {
                    UpdateInfo info = mUpdateItemInfoList.get(i);
                    String url = info.getUrl();
                    String name = url.substring(url.lastIndexOf(File.separator) + 1);
                    // 判断是否安装apk，并且版本是不是和服务器的一直。
                    DownloadInfo downloadInfo = DownloadManager.getInstance().getDownloadInfo(info.getUrl());
                    if (downloadInfo != null) {
                        boolean isRunning = DownloadManager.getInstance().isRunning(info.getUrl());
                        if (!isRunning) {
                            // 下载
                            LogUtils.showLog(mContext,
                                    "DownloadTask->downloadInfo!=null下载:" + info + "  url:" + url + "  name:" + name);
                            download(info, url, name);
                        } else {
                            // 正在下载，是什么也不做。
                        }
                    } else if (!ApkUtil.isInstalledNewestApk(mContext, info.getPkgName(), info.getVcode())) {
                        if (!ApkUtil.isExistNewestApk(mContext, info.getPath(), info.getVcode())) {
                            // 下载
                            LogUtils.showLog(mContext,
                                    "DownloadTask->!isExistNewestApk()下载:" + info + "  url:" + url + "  name:" + name);
                            download(info, url, name);
                        } else {
                            // 安装
                            LogUtils.showLog(mContext, "DownloadTask->install,本地存在安装包");
                            downloadListener(info);
                        }
                    }
                }
            } else {
                LogUtils.showLog(mContext, "Toady no download task!!! mUpdateItemInfoList is null.", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadListener(UpdateInfo info) {
        // 开放给锁屏的接口
        if (UpdateSdkManager.sDownloadListener == null) {
            LogUtils.showLog(mContext, "接口为空，直接安装");
            InstallTaskManager.execute(mContext, info);
        } else {
            UpdateSdkManager.sDownloadListener.onCompleted(info);
            LogUtils.showLog(mContext, "接口不为空，弹出对话框");
        }
    }

    private void download(UpdateInfo info, String url, String name) {
        if (!EverydayTaskManager.isConnect(mContext)) {
            LogUtils.showLog(mContext, "No connect!!!" + "  url:" + url + "  name:" + name);
            return;
        }
        final DownloadRequest request =
                new DownloadRequest.Builder().setName(name).setUri(url).setNetStatus(DownloadRequest.NetStatus.Wifi)
                        .setFolder(new File(ACContext.getDirectoryPath(DirType.apps))).build();
        DownloadManager.getInstance().download(request, url, new DownloadCallBack(request, info));
    }

    class DownloadCallBack implements CallBack {
        private DownloadRequest request;
        private UpdateInfo info;

        public DownloadCallBack(DownloadRequest request, UpdateInfo info) {
            this.request = request;
            this.info = info;
        }

        @Override
        public void onStarted() {}

        @Override
        public void onConnecting() {}

        @Override
        public void onConnected(long total, boolean isRangeSupport) {}

        @Override
        public void onProgress(long finished, long total, int progress) {

        }

        @Override
        public void onCompleted() {
            // 操作日志
            LogStatisticsData data = new LogStatisticsData();
            data.initLogData(info);
            data.setApkId(info.getId());
            data.setOperType(LogStatisticsKey.operType_Download);
            data.setOperResult(LogStatisticsKey.operResult_Success);
            LogStatisticsManager.getInstance().logEvent(mContext, LogStatisticsKey.LogEventID, data);


            LogUtils.showLog(mContext, UpdateInfo.urlToName(request.getUri()) + "->onCompleted()");

            // InstallTaskManager.execute(mContext, info);
            downloadListener(info);
        }

        @Override
        public void onDownloadPaused() {
            LogUtils.showLog(mContext, UpdateInfo.urlToName(request.getUri()) + "->onDownloadPaused()");
        }

        @Override
        public void onDownloadCanceled() {
            LogUtils.showLog(mContext, UpdateInfo.urlToName(request.getUri()) + "->onDownloadCanceled()");
            // DownLoadStatusCache.setQuestList(request, this);
        }

        @Override
        public void onFailed(DownloadException e) {
            LogUtils.showLog(mContext, UpdateInfo.urlToName(request.getUri()) + "->onFailed()", true);
            // DownLoadStatusCache.setQuestList(request, this);

            // 操作日志
            LogStatisticsData data = new LogStatisticsData();
            data.initLogData(info);
            data.setApkId(info.getId());
            data.setOperType(LogStatisticsKey.operType_Download);
            data.setOperResult(LogStatisticsKey.operResult_Fail);
            data.setErrorCode(e.getErrorCode());
            data.setMessage(e.getErrorMessage() + ";" + e.getMessage());
            LogStatisticsManager.getInstance().logEvent(mContext, LogStatisticsKey.LogEventID, data);
        }

        @Override
        public void onDownloadRetry() {

        }
    }
}
