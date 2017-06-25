package com.tcl.update.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.tcl.update.debug.LogUtils;
import com.tcl.update.framework.log.NLog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yancai.liu on 2016/10/11.
 */

public class ApkUtil {

    private final static String TAG = "ApkUtil";

    public static final int INSTALL_SUCCESS = 1;// 安装成功
    public static final int INSTALL_SUCCESS_24 = 2;// 安装成功
    public static final int INSTALL_FILE_NOT_EXIT = -11;// 文件不存在
    public static final int INSTALL_NO_PERMISSON = -12;// 没有INSTALL权限
    public static final int INSTALL_FAILED = -13;// 安装失败
    public static final int INSTALL_BAD_APKPATH = -14;// apkPath有错误

    public static boolean isExistNewestApk(Context context, String apkPath, int versionCode) {
        if (TextUtils.isEmpty(apkPath)) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return versionCode <= packageInfo.versionCode;
        }
        return false;
    }

    public static boolean isInstalledNewestApk(Context context, String pkg, int versionCode) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                return versionCode <= packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            File file = new File(path);
            return file.isFile() && file.exists();
        }
    }

    public static boolean isAppInstalled(Context context, String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static ApplicationInfo getApkApplicationInfo(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            // ApplicationInfo appInfo = info.applicationInfo;
            // String appName = pm.getApplicationLabel(appInfo).toString();
            // String packageName = appInfo.packageName; // 得到安装包名称
            // String version = info.versionName; // 得到版本信息
            return info.applicationInfo;
        }
        return null;
    }


    public static class InstallResult {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void add(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public static InstallResult install(Context context, String apkPath, String pkgName) {
        InstallResult installResult = new InstallResult();
        if (TextUtils.isEmpty(apkPath)) {
            NLog.w("ContextUtils", "download complete intent has no path param");
            installResult.add(INSTALL_BAD_APKPATH, "bad apk path");
            return installResult;
        }

        File file = new File(apkPath);
        if (!file.exists()) {
            NLog.d("ContextUtils", "file %s not exists", apkPath);
            installResult.add(INSTALL_FILE_NOT_EXIT, "apk file not exit");
            return installResult;
        }

        // 没有安装权限
        if (!hasInstallPermission(context)) {
            NLog.w(TAG, "install failed: no install permission granted");
            installResult.add(INSTALL_NO_PERMISSON, "no install permission granted");
            return installResult;
        }

        if (Build.VERSION.SDK_INT < 24) {
            installResult = sysInstall(apkPath);
            NLog.w(TAG, "install message:  " + installResult.message + "   " + apkPath);
            return installResult;
        }

        try {
            Uri uri = Uri.fromFile(new File(apkPath));
            BlockPackageInstallObserver observer = new BlockPackageInstallObserver(pkgName);
            PackageManager pm = context.getPackageManager(); // 得到pm对象
            // 通过反射，获取到PackageManager隐藏的方法installPackage
            Method installPackage = pm.getClass().getDeclaredMethod("installPackage", Uri.class,
                    IPackageInstallObserver.class, int.class, String.class);
            installPackage.invoke(pm, uri, observer, 2, pkgName);
            int result = observer.waitResult();
            if (result == 1) {
                installResult.add(INSTALL_SUCCESS_24, "install success");
            } else {
                installResult.add(INSTALL_FAILED, "the install returnCode:" + result);
            }

        } catch (Exception e) {
            NLog.printStackTrace(e);
        }

        return installResult;
    }

    /**
     * 系统级自动安装
     */
    private static InstallResult sysInstall(String filePath) {
        InstallResult installResult = new InstallResult();
        String[] args = {"pm", "install", "-r", filePath};
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();

        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            installResult.add(INSTALL_FAILED, e.getMessage());
        } catch (Exception e) {
            installResult.add(INSTALL_FAILED, e.getMessage());
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {}

            if (process != null) {
                process.destroy();
            }
        }

        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            installResult.add(INSTALL_SUCCESS, "install success");
        } else if (!errorMsg.equals(null)) {
            installResult.add(INSTALL_FAILED, errorMsg.toString());
        }
        return installResult;
    }

    static class BlockPackageInstallObserver implements IPackageInstallObserver {

        private String mPackageName;
        private AtomicInteger mResult = new AtomicInteger(0);
        private volatile boolean mResultGot;

        BlockPackageInstallObserver(String packageName) {
            mPackageName = packageName;
        }

        private void setResult(int result) {
            synchronized (this) {
                mResult.set(result);
                mResultGot = true;
                notifyAll();
            }
        }

        public int waitResult() throws InterruptedException {
            synchronized (this) {
                while (!mResultGot) {
                    wait();
                }
            }

            return mResult.get();
        }

        @Override
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            if (mPackageName.equals(packageName)) {
                setResult(returnCode);
            } else {
                setResult(INSTALL_FAILED);
            }

        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }


    /**
     * 是否拥有安装权限
     */
    public static boolean hasInstallPermission(Context context) {
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            if (PackageManager.PERMISSION_GRANTED == packageManager
                    .checkPermission(Manifest.permission.INSTALL_PACKAGES, context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否为系统应用
     */
    public static boolean isSystemApp(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return true;
        } else {
            return false;
        }
        // return ((context.getApplicationInfo()).flags & ApplicationInfo.FLAG_SYSTEM) > 0;
    }


    /**
     * 静默安装APK， 需要ROOT权限
     *
     * @param apkPath APK的文件路径
     * @return
     */
    public static boolean installSilent(String apkPath) {
        int result = -1;
        DataOutputStream dos = null;
        String cmd = "pm install -r " + apkPath;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            NLog.printStackTrace(e);
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    NLog.printStackTrace(e);
                }
            }
        }
        return result == 0;
    }

    private final static int kSystemRootStateUnknown = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int sRootState = kSystemRootStateUnknown;

    /**
     * 判断系统是否已经ROOT
     *
     * @return
     */
    public static boolean hasSystemRooted() {

        if (sRootState == kSystemRootStateEnable) {
            return true;
        } else if (sRootState == kSystemRootStateDisable) {
            return false;
        }

        File f;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f.exists()) {
                    sRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
            NLog.e("ApkUtil", e);
        }

        sRootState = kSystemRootStateDisable;
        return false;
    }

    public static void deleteApkFileWithUnUIThread(final String path, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                        // 广播给FileManager通知刷新
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 需要系统权限
     *
     * @return
     */
    public static boolean isApkRunning(Context context, String pkg) {
        LogUtils.showLog(context, "judge " + pkg + " is or not Running!!!");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0) {
            ActivityManager.RunningTaskInfo cinfo = runningTasks.get(0);
            if (cinfo != null) {
                ComponentName component = cinfo.topActivity;
                if (component != null && pkg.equals(component.getPackageName())) {
                    LogUtils.showLog(context, pkg + "is running!!!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取应用名称
     * 
     * @param context
     * @return
     */
    public static String getAppTitle(Context context) {
        String appName = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                appName = (String) appInfo.loadLabel(packageManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appName;
    }

}
