package com.tcl.update.context;

import android.content.Context;

import com.tcl.statistic.network.StaisticsAPI;
import com.tcl.update.Config;
import com.tcl.update.utils.AndroidUtil;

/**
 * Created by fanyang.sz on 2016/10/13.
 */

public class ConfigUrl {

    private static final String API_HOME_HOST_TEST = "https://upgradetest.tclclouds.com/apkupgrade-api";
    private static final String API_HOME_HOST = "https://upgrade-cn-api.tclclouds.com/sdk";
    private static final String API_OVERSEAS_HOST = "https://upgrade-us-api.tclclouds.com/sdk";

    /**
     * 根据国家，选择BaseUrl 1.首先通过手机SIM卡的IMSI来判断所处的国家 2.如果IMSI为空，则根据ip地址判断国家
     *
     * @return
     */
    public static String configUrl(Context context) {
        if (Config.isUrlDebug()) {
            return API_HOME_HOST_TEST;
        } else {
            if (AndroidUtil.isChinaByIMSI(context)) {
                return API_HOME_HOST;
            } else if (AndroidUtil.isChinaByIP(context)) {
                return API_HOME_HOST;
            } else {
                return API_OVERSEAS_HOST;
            }
        }
    }

    public static String configStaisticsUrl(Context context) {
        if (Config.isUrlDebug()) {
            return StaisticsAPI.HTTPS_TEST_SEVER;
        } else {
            if (AndroidUtil.isChinaByIMSI(context)) {
                return StaisticsAPI.SERVER_URL_CHINA;
            } else if (AndroidUtil.isChinaByIP(context)) {
                return StaisticsAPI.SERVER_URL_CHINA;
            }
            return StaisticsAPI.SERVER_URL_GLOBAL;
        }
    }
}
