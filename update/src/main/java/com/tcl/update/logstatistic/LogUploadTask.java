package com.tcl.update.logstatistic;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.tcl.taskflows.Task;
import com.tcl.update.BuildConfig;
import com.tcl.update.context.ConfigUrl;
import com.tcl.update.debug.LogUtils;
import com.tcl.update.httpurlconnection.HttpsRequest;
import com.tcl.update.httpurlconnection.HttpsResponse;
import com.tcl.update.middleman.PackageManagerUtil;
import com.tcl.update.utils.AndroidUtil;
import com.tcl.update.utils.ContextUtils;
import com.tcl.update.utils.DeviceUtils;
import com.tcl.update.utils.MD5Util;
import com.tcl.update.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanyang.sz on 2016/12/9.
 */

public class LogUploadTask extends Task {

    private LogStatisticsProvider mProvider;
    private LogStatisticsItem mItem;
    private String signKey = "#87&$#1@99";

    public LogUploadTask(Context context, LogStatisticsItem item) {
        super(context);
        this.mProvider = new LogStatisticsProvider(mContext);
        this.mItem = item;
    }

    @Override
    protected void onExecute() throws Exception {

        if (AndroidUtil.isWifiConnect(mContext)) {
            // 先把这一条存到本地，防止服务器出错，没有任何返回
            if (mItem != null) {
                mProvider.saveOrUpdate(mItem);
            }
            // 上传log先检查本地有没有log
            List<LogStatisticsItem> localItems = mProvider.getAllData();
            if (localItems == null) {
                return;
            }
            if (localItems.size() > 0) {
                Map<String, String> data = requestCommonParams(mContext);
                String itemsString = LogStatisticsItem.toJSON(localItems);
                data.put("items", itemsString);

                LogUtils.showLog(mContext, "上传操作日志:" + data.toString());

                HttpsResponse response =
                        HttpsRequest.httpsRequest(ConfigUrl.configUrl(mContext) + "/apk/operLog", "POST", data);
                parseCheckUpdateResult(response);

            }
        } else {
            if (mItem != null) mProvider.saveOrUpdate(mItem);
        }
    }

    private void parseCheckUpdateResult(HttpsResponse result) {
        if (result.getCode() == HttpsResponse.SUCCESS) {
            if (!TextUtils.isEmpty(result.getMsg())) {
                try {
                    JSONObject resultJson = new JSONObject(result.getMsg());
                    // 获取成功
                    if ("1".equals(resultJson.getString("code"))) {
                        // 删除日志
                        mProvider.deleteAll();
                        LogUtils.showLog(mContext, "上传操作日志成功");
                    } else {
                        LogUtils.showLog(mContext, "上传操作日志失败：" + resultJson.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LogUtils.showLog(mContext, "上传操作日志失败!");
            }
        } else {
            LogUtils.showLog(mContext, "上传操作日志失败!" + result.getMsg());
        }

    }

    public Map<String, String> requestCommonParams(Context context) {
        Map<String, String> data = new HashMap<>();
        try {
            data.put("imei", PackageManagerUtil.getDeviceId(mContext));// IMEI号
            data.put("imsi", PackageManagerUtil.getSubscriberId(mContext));// imsi号
            data.put("mac", DeviceUtils.getMacAddress(mContext));// MAC地址
            data.put("model", ContextUtils.getSystemModel());
            data.put("CU", AndroidUtil.getCU());// 手机CU
            data.put("os_version", Build.VERSION.RELEASE);// 系统版本
            data.put("os_version_code", String.valueOf(android.os.Build.VERSION.SDK_INT));// android系统版本代码
            data.put("screen_size",
                    AndroidUtil.getDisplayMetricsHeight(context) + "#" + AndroidUtil.getDisplayMetricsWidth(context));// 手机屏幕分辨率
            data.put("req_from", "phone");
            data.put("network", NetUtils.getConnectType(mContext));// 网络类型
            data.put("spn", AndroidUtil.getSPN(mContext)); // 运营商
            data.put("androidId", DeviceUtils.getAndroidId(mContext));// androidID
            data.put("serial", Build.SERIAL);// 序列号
            data.put("language", mContext.getResources().getConfiguration().locale.toString());// 语言
            data.put("country", DeviceUtils.getCountryCode());// 国家代码
            data.put("timeZone", DeviceUtils.getTimeZone());// 获取时区

            data.put("uuid", DeviceUtils.getUUID(mContext));
            data.put("SDK_vn", BuildConfig.SDK_VERSIONNAME);
            data.put("SDK_vc", BuildConfig.VERSION_CODE + "");
            // 缺一个签名
            data.put("sign", getSignValue(data)); // 区域

            data.put("enDevId", PackageManagerUtil.getEncryptDeviceId(context));
            data.put("enDevUId", PackageManagerUtil.getEncryptDeviceUId(context));
            data.put("pkgName", context.getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 参数sign的值，sign=MD5(参数值&参数值......&参数值&key，"UTF-8");//若参数值是null或""空字符串则跳过
     */
    private String getSignValue(Map<String, String> paramsMap) {

        String[] keys = {"imei", "imsi", "model", "CU", "screen_size", "language", "country"};
        String sign = "";
        for (String key : keys) {
            if (paramsMap.containsKey(key) && !TextUtils.isEmpty(paramsMap.get(key))) {
                sign += paramsMap.get(key) + "&";
            }
        }
        sign += signKey;
        return MD5Util.MD5Encode(sign, "UTF-8");
    }


}
