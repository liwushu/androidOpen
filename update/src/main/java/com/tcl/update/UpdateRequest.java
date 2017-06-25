package com.tcl.update;

import android.content.Context;
import android.text.TextUtils;

import com.tcl.update.context.ConfigUrl;
import com.tcl.update.db.PackageNamesProvider;
import com.tcl.update.debug.LogUtils;
import com.tcl.update.httpurlconnection.HttpUtil;
import com.tcl.update.httpurlconnection.HttpsRequest;
import com.tcl.update.httpurlconnection.HttpsResponse;
import com.tcl.update.middleman.PackageManagerUtil;
import com.tcl.update.utils.AndroidUtil;
import com.tcl.update.utils.ContextUtils;
import com.tcl.update.utils.MD5Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanyang.sz on 2016/12/5.
 */

public class UpdateRequest {
    private Context mContext;
    private String signKey = "#87&$#1@99";
    private PackageNamesProvider mPackageNamesProvider;

    public UpdateRequest(Context context) {
        this.mContext = context;
        mPackageNamesProvider = new PackageNamesProvider(context);
    }

    public HttpsResponse run() {
        return HttpsRequest.httpRequestAddParams(mContext,
                ConfigUrl.configUrl(mContext) + "/apk/version/getUpgradeApkList", "POST", getParams());
    }

    public Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("pkg", mContext.getPackageName());// "pkg"确定是Diagnostics还是MiddleMan或者其他应用，目前仅支持Diagnostics还是MiddleMan
        map.put("apks", ContextUtils.getAppList(mContext, mPackageNamesProvider.getData()));

        if (!Config.isDebug()) {
            map.put("imsi", PackageManagerUtil.getSubscriberId(mContext));// "724556789123456789",
            map.put("model", ContextUtils.getSystemModel());// Build.MODEL"RAV4_EMEA");
        } else if (!HttpUtil.getTestParamsMap(map)) {
            map.put("imsi", PackageManagerUtil.getSubscriberId(mContext));// "724556789123456789",
            map.put("model", ContextUtils.getSystemModel());// Build.MODEL"RAV4_EMEA");
        }
        map.put("imei", PackageManagerUtil.getDeviceId(mContext));
        map.put("CU", AndroidUtil.getCU());// 手机CU
        map.put("screen_size",
                AndroidUtil.getDisplayMetricsHeight(mContext) + "#" + AndroidUtil.getDisplayMetricsWidth(mContext));// 手机屏幕分辨率
        map.put("sign", getSignValue(map));


        LogUtils.showLog(mContext, "CheckUpdateRequest map:" + map.toString(), true);
        return map;
    }

    /**
     * 参数sign的值，sign=MD5(参数值&参数值......&参数值&key，"UTF-8");//若参数值是null或""空字符串则跳过
     */
    private String getSignValue(Map<String, String> paramsMap) {

        String[] keys = {"pkg", "apks", "imsi", "imei", "model", "CU", "screen_size"};
        String sign = "";
        for (String key : keys) {
            if (paramsMap.containsKey(key) && !TextUtils.isEmpty(paramsMap.get(key))) {
                if (key.equals("apks")) {
                    try {
                        JSONArray jsonArray = new JSONArray(paramsMap.get("apks"));
                        sign += getApksValue(jsonArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    sign += paramsMap.get(key) + "&";
                }
            }
        }
        sign += signKey;
        return MD5Util.MD5Encode(sign, "UTF-8");
    }

    /**
     * 从JSONArray获得apks的拼接
     */
    private String getApksValue(JSONArray jsonArray) {
        String[] keys = {"pkg", "vname", "vcode"};
        String apks = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (String key : keys) {
                    if (jsonObject.has(key) && !TextUtils.isEmpty(jsonObject.get(key).toString()))
                        apks += jsonObject.get(key) + "&";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return apks;
    }

}
