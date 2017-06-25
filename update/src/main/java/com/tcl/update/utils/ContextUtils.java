package com.tcl.update.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.tcl.update.db.PackageNames;
import com.tcl.update.framework.log.NLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ContextUtils {


    public static String getMetaData(Context context, String name) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        Object value = null;
        try {

            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }

        } catch (NameNotFoundException e) {
            NLog.printStackTrace(e);
            NLog.w("ContextUtils", "Could not read the name(%s) in the manifest file.", name);
            return null;
        }

        return value == null ? null : value.toString();
    }

    /**
     * 获取手机上安装的所有应用包名、版本信息等
     */
    public static JSONArray getAppList(Context context) {
        PackageManager pm = context.getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        JSONArray jsonArray = new JSONArray();

        for (PackageInfo packageInfo : packages) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("pkg", packageInfo.packageName);
                jsonObject.put("vname", packageInfo.versionName);
                jsonObject.put("vcode", packageInfo.versionCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static String getAppList(Context context, PackageNames packageNames) {
        PackageManager pm = context.getPackageManager();
        String[] pkgNames = packageNames.getPkgNames();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < pkgNames.length; i++) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(pkgNames[i], PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("pkg", packageInfo.packageName);
                    jsonObject.put("vname", packageInfo.versionName);
                    jsonObject.put("vcode", packageInfo.versionCode);
                    jsonArray.put(jsonObject);
                }
            } catch (PackageManager.NameNotFoundException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (jsonArray.length() > 0) {
            return jsonArray.toString();
        }

        return "";
    }

    // 读取手机system路径下的build.prop文件获得手机的model
    public static String getSystemModel() {
        String model = "";
        try {
            File file = new File(Environment.getRootDirectory().getPath() + File.separator + "build.prop");
            if (!file.exists()) return null;
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            model = properties.get("ro.product.device").toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return model;
        }
    }
}
