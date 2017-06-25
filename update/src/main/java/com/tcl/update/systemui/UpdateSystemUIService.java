package com.tcl.update.systemui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.tcl.update.BuildConfig;
import com.tcl.update.Config;
import com.tcl.update.DownloadListener;
import com.tcl.update.InstallListener;
import com.tcl.update.R;
import com.tcl.update.UpdateSdkManager;
import com.tcl.update.db.UpdateInfo;

/**
 * UpdateService运行后台升级SDK的服务，以Activity形式弹出升级提示框
 * Created by dejun.xie on 2016/12/21.
 */
public class UpdateSystemUIService extends Service {
    private static boolean isShowingDialog = false;
    private boolean isInited=false;
    private Context mContext;
    private static UpdateInfo updateInfo;
    // 2次升级弹框间隔时间
    private static int TIME_INTERVAL = 3 * 60 * 60 * 1000 * 24;
    static {
        if (BuildConfig.DEBUG) {
            // 测试时间2分钟
            TIME_INTERVAL = 2 * 60 * 1000;
        } else {
            // 正式版时间间隔为3天
            TIME_INTERVAL = 3 * 60 * 60 * 1000 * 24;
        }
    }

    private final int LOG_LEVEL = LogUtils.WARNING;// 打印日志的等级
    private AlertDialog mAlertDialog;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.logXDJ(LOG_LEVEL,"systemuiHelper onStartCommand: isInited="+isInited);
        //升级SDK的初始化
        initUpdateSdkManager();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initUpdateSdkManager(){
        if(!isInited){
            UpdateSdkManager.init(mContext,downloadListener);
            setDebugParams(Config.isDebug());
            isInited=true;
            LogUtils.logXDJ(LOG_LEVEL,"开启服务初始化升级SDK...");
        }
    }

    private void setDebugParams(boolean isDebug){
        UpdateSdkManager.setDebug(isDebug);
        UpdateSdkManager.setUrlDebug(isDebug);
        UpdateSdkManager.setLogDebug(isDebug);
    }

    //监听升级包的下载情况
    DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onCompleted(UpdateInfo updateInfo) {
            LogUtils.logXDJ(LOG_LEVEL,"onCompleted:升级包下载完成...");
            UpdateSystemUIService.updateInfo= updateInfo;
            //当前用户处于通话空闲状态且超过弹窗提示时常间隔
            if(isCallStateIdle() && overPopTime()){
                showUpdateDialog();
            }
        }
    };

    //判断当前是否有来电
    private boolean isCallStateIdle() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        LogUtils.logXDJ(LOG_LEVEL, "isCallStateIdle:tm.getCallState() = "+tm.getCallState());
        //“摘机”和“响铃”状态不弹出升级提示框
        if(tm.getCallState() == TelephonyManager.CALL_STATE_RINGING ||
                tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
            return false;
        }
        return true;
    }

    //距离上一次弹出框的时间间隔是否大于TIME_INTERVAL
    private boolean overPopTime() {
        long lastTime=SharePreferenceUtils.getLastTime(mContext);
        long nowTime=System.currentTimeMillis();
        if (BuildConfig.DEBUG) {
            LogUtils.logXDJ(LOG_LEVEL, "overPopTime = " + (nowTime - lastTime));
        }
        if(nowTime-lastTime>TIME_INTERVAL){
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showUpdateDialog() {
        if (isShowingDialog) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Light_Dialog);
        builder.setTitle(R.string.lock_screen_update);
        builder.setMessage(getString(R.string.new_version) + "\n\n" + getString(R.string.update_restart));
        builder.setPositiveButton(R.string.tip_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recordClickTime();
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                startInstallUpdatePackage();
            }
        });
        builder.setNegativeButton(R.string.tip_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recordClickTime();
            }
        });

        isShowingDialog = true;
        mAlertDialog = builder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);
        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowingDialog = false;
            }
        });
        mAlertDialog.show();
    }

    private void recordClickTime() {
        //记录当前时间，以便计算弹出框的时间间隔
        SharePreferenceUtils.recordTime(this);
    }

    private void startInstallUpdatePackage() {
        //升级包的安装
        UpdateSdkManager.install(this, UpdateSystemUIService.updateInfo, new InstallListener() {
            @Override
            public void onSuccess() {
                LogUtils.logXDJ(LOG_LEVEL, "升级成功...");
            }

            @Override
            public void onFailed(int i, String s) {
                LogUtils.logXDJ(LOG_LEVEL, "升级失败：" + s);
            }
        });
    }
}
