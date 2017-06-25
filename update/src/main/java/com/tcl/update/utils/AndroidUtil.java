package com.tcl.update.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.tcl.update.framework.log.NLog;
import com.tcl.update.middleman.PackageManagerUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static com.tcl.update.utils.IpSourceUtil.ipExistsInRange;
import static com.tcl.update.utils.IpSourceUtil.ips;

@SuppressLint("NewApi")
public class AndroidUtil {
    private static final int MIN_STAR = 1;
    private static final int MAX_STAR = 5;
    private static final int DEFAULT_STAR = 3;
    private static final String TAG = "AndroidUtil";


    @SuppressWarnings("deprecation")
    public static int getDisplayMetricsWidth(Context mContext) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        return wm.getDefaultDisplay().getWidth();
    }

    @SuppressWarnings("deprecation")
    public static int getDisplayMetricsHeight(Context mContext) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    // /**
    // * 打开应用
    // *
    // * @param packageName
    // */
    // public static boolean openApp(Context mContext, String packageName) {
    // try {
    // ResolveInfo resolveInfo = CurrentAppsInstallData.getInstance().getResolveInfo(packageName);
    // if (resolveInfo == null) {
    // return false;
    // }
    // mContext.startActivity(getOpenIntent(resolveInfo, packageName));
    // } catch (Exception e) {
    // }
    // return true;
    // }

    /**
     * 获取打开应用的Intent
     */
    public static Intent getOpenIntent(ResolveInfo resolveInfo, String packageName) {
        ComponentName componet = new ComponentName(packageName, resolveInfo.activityInfo.name);
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setComponent(componet);
        return i;
    }

    /**
     * 获取安装应用的Intent
     */
    public static Intent getInstallIntent(Context mContext, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 是否为系统应用
     *
     * @param context
     * @return
     */
    public static boolean isSystemApp(Context context) {
        return ((context.getApplicationInfo()).flags & ApplicationInfo.FLAG_SYSTEM) > 0;
    }


    /**
     * 系统级自动安装
     *
     * @param apkPath
     * @return
     */
    public static String sysInstall(String apkPath) {
        String[] args = {"pm", "install", "-r", apkPath};
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write("/n".getBytes("UTF-8"));
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /**
     * root手机的自动安装
     *
     * @param apkPath
     * @return
     */
    public static boolean execRootCmdSilent(String apkPath) {
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
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result == 0;
    }

    /**
     * 判断网络是否连接
     *
     * @param mContext
     * @return
     */
    public static boolean isNetConnect(Context mContext) {
        ConnectivityManager manager =
                (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isConnected()) {
            return true;
        }
        if (gprs != null && gprs.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否wifi网络
     *
     * @param mContext
     * @return
     */
    public static boolean isWifiConnect(Context mContext) {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            return true;
        }
        return false;
    }

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {

            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    /*
     * 获取当前程序包名的名字
     */
    public static String getPackAgeName(Context context) {
        String packageNames = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageNames = info.packageName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return packageNames;

    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        try {
            if (runningTaskInfos != null)
                return (runningTaskInfos.get(0).topActivity).toString();
            else
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * String 时间传入 转换出 yyyy/MM/dd
     *
     * @param time
     * @return
     */
    public static String time2Date(String time) {
        String dateString = "";
        if (!TextUtils.isEmpty(time) && TextUtils.isDigitsOnly(time)) {
            Date currentTime = new Date(Long.parseLong(time));
            dateString = DateFormat.getDateTimeInstance().format(currentTime);
        }
        return dateString;
    }

    /**
     * 从middleman和elable获取imsi
     */
    private static String getSubscriberIdByMiddleMan(Context context, Uri uri) {
        Cursor cursor = null;
        String subscriberId = "";
        try {

            cursor = context.getContentResolver().query(uri, null, "getSubscriberId", null, null);

            if (cursor != null) {
                Bundle bundle = cursor.getExtras();
                if (bundle.getInt("result_code") == 1) {
                    subscriberId = bundle.getString("result");

                    if (subscriberId != null) {
                        NLog.d(TAG, "get SubscriberID successful");
                        return subscriberId;
                    } else {
                        NLog.d(TAG, "get SubscriberID error");
                        subscriberId = "0";
                    }
                } else {
                    if (bundle.getInt("result_code") == 2) {
                        NLog.d(TAG, "dont exist \"getSubscriberID\" method");
                    } else if (bundle.getInt("result_code") == 3) {
                        NLog.d(TAG, "SubscriberID return null");
                    } else if (bundle.getInt("result_code") == 4) {
                        NLog.d(TAG, "PackageName is null");
                    } else if (bundle.getInt("result_code") == 5) {
                        NLog.d(TAG, "application has not been registered");
                    }
                    if (context.checkCallingOrSelfPermission(
                            "android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
                        subscriberId = TelephonyManagerUtil.getInstance(context).getSubscriberId();
                    }
                    return subscriberId;
                }
            } else {
                if (context.checkCallingOrSelfPermission(
                        "android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
                    subscriberId = TelephonyManagerUtil.getInstance(context).getSubscriberId();
                }
                return subscriberId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return "";
    }

    /**
     * 根据IMSI判断是否是国内
     */
    public static boolean isChinaByIMSI(Context context) {
        String IMSI = PackageManagerUtil.getSubscriberId(context);
        if (!TextUtils.isEmpty(IMSI)) {
            IMSI = IMSI.substring(0, 2);
            return IMSI.equals("460");
        } else {
            return false;
        }
    }

    /**
     * 根据IP判断是否是国内
     */
    public static boolean isChinaByIP(Context context) {
        String ip = "";
        try {
            // 获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            // 判断wifi是否开启
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                ip = intToIp(ipAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < ips.length; i++) {
            if (ipExistsInRange(ip, ips[i])) return true;
        }
        return false;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    /**
     * 高通芯片 通过反射获取CU,PTS,PTH,BSN参数
     *
     * @return
     */
    public static String getCU() {
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getMethod = systemPropertiesClass.getMethod("get", String.class);
            Object object = new Object();
            Object obj = getMethod.invoke(object, "ro.tct.curef");
            return (obj == null ? "" : (String) obj);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 通过middleman获取手机运营商
     */
    public static String getSPN(Context context) {

        if (context == null) {
            return null;
        }

        Cursor cursor = null;
        try {
            final Uri uri = Uri.parse("content://com.tct.gapp.middleman/" + getPackAgeName(context));
            cursor = context.getContentResolver().query(uri, null, "getNetworkOperatorName", null, null);
            String SPN = "";
            if (cursor != null) {
                Bundle bundle = cursor.getExtras();
                if (bundle.getInt("result_code") == 1) {
                    SPN = bundle.getString("result");
                    if (SPN != null) {
                        NLog.d(TAG, "get SPN successful");
                        return SPN;
                    } else {
                        NLog.d(TAG, "get SPN error");
                        SPN = "0";
                    }
                } else {
                    if (bundle.getInt("result_code") == 2) {
                        NLog.d(TAG, "dont exist \"SPN\" method");
                    } else if (bundle.getInt("result_code") == 3) {
                        NLog.d(TAG, "SPN return null");
                    } else if (bundle.getInt("result_code") == 4) {
                        NLog.d(TAG, "PackageName is null");
                    } else if (bundle.getInt("result_code") == 5) {
                        NLog.d(TAG, "application has not been registered");
                    }
                    if (context.checkCallingOrSelfPermission(
                            "android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
                        SPN = TelephonyManagerUtil.getInstance(context).getSPN();
                    }
                    return SPN;
                }
            } else {
                if (context.checkCallingOrSelfPermission(
                        "android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
                    SPN = TelephonyManagerUtil.getInstance(context).getSPN();
                }
                return SPN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return "";
    }
}
