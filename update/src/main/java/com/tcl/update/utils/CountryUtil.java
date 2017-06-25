package com.tcl.update.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by fanyang.sz on 2016/9/23.
 *
 * 获取国家区域代码 1.首先通过手机SIM卡的IMSI来判断所处的国家 2.如果IMSI为空，则根据ip地址判断国家
 */

public class CountryUtil {

    private static String IMSI;
    private static String ip;

    public static int getCountry(Context context) {
        // 首先获得IMSI号(由于WMD项目是内嵌到拥有系统权限的app中，如果是一般应用就要考虑android M对权限的申请了)
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            IMSI = telephonyManager.getSubscriberId();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // 如果IMSI为空，则根据WiFi的IP判断(同1：其它应用注意权限的申请)
        if (TextUtils.isEmpty(IMSI)) {
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

        }
        return 0;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

}
