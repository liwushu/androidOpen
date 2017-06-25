package com.tcl.update.httpurlconnection;


import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.tcl.update.BuildConfig;
import com.tcl.update.debug.LogUtils;
import com.tcl.update.middleman.PackageManagerUtil;
import com.tcl.update.utils.AndroidUtil;
import com.tcl.update.utils.DeviceUtils;
import com.tcl.update.utils.NetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by fanyang.sz on 2016/12/2.
 */

public class HttpsRequest {

    public static HttpsResponse httpRequestAddParams(Context context, String requestUrl, String requestMethod,
            Map<String, String> params) {
        if (params != null) {
            params.putAll(getBaseParams(context));
        } else {
            params = getBaseParams(context);
        }

        HttpsResponse response = httpsRequest(requestUrl, requestMethod, params);
        LogUtils.showLog(context, "requestUrl:" + requestUrl + "\n response:" + response, true);
        return response;
    }

    public static HttpsResponse httpsRequest(String requestUrl, String requestMethod, Map<String, String> params) {

        HttpsResponse response = new HttpsResponse();

        if (TextUtils.isEmpty(requestUrl)) {
            response.setCode(HttpsResponse.FAIL);
            response.setMsg("url is empty");
            return response;
        }

        HttpURLConnection httpUrlConn = null;
        StringBuilder buffer = new StringBuilder();
        OutputStream outputStream = null;

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {

            // 打开连接
            URL url = new URL(requestUrl);

            if (requestUrl.startsWith("https")) {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = {new MyX509TrustManager()};
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tm, new java.security.SecureRandom());

                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                httpUrlConn = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) httpUrlConn).setSSLSocketFactory(ssf);
                ((HttpsURLConnection) httpUrlConn).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });

            } else {
                httpUrlConn = (HttpURLConnection) url.openConnection();
            }

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setConnectTimeout(30 * 1000);
            httpUrlConn.setReadTimeout(30 * 1000);

            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }

            // 当有数据需要提交时
            if (params != null) {
                String outputStr = HttpUtil.encodeToURLParams(params);
                outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                // outputStream.close()
            }

            if (httpUrlConn.getResponseCode() != HttpURLConnection.HTTP_OK) throw new Exception();
            // 将返回的输入流转换成字符串
            inputStream = httpUrlConn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            /*
             * bufferedReader.close(); inputStreamReader.close(); // 释放资源 inputStream.close();
             */

            // httpUrlConn.disconnect();
            // return buffer.toString();
            response.setCode(HttpsResponse.SUCCESS);
            response.setMsg(buffer.toString());

        } catch (ConnectException ce) {
            ce.printStackTrace();
            response.setCode(HttpsResponse.TIMEOUT);
            response.setMsg(ce.getMessage());
        } catch (IOException ie) {
            ie.printStackTrace();
            response.setCode(HttpsResponse.IOFAIL);
            response.setMsg(ie.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(HttpsResponse.FAIL);
            response.setMsg(e.getMessage());
        } finally {

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }
        return response;
    }



    private static Map<String, String> getBaseParams(Context context) {
        Map<String, String> map = new HashMap<>();
        // 附加参数
        map.put("spn", AndroidUtil.getSPN(context)); // 运营商
        map.put("mac", DeviceUtils.getMacAddress(context)); // Mac地址
        map.put("language", context.getResources().getConfiguration().locale.toString());
        map.put("req_from", "phone");
        map.put("version_name", DeviceUtils.getVersionName(context));
        map.put("version_code", String.valueOf(DeviceUtils.getVersionCode(context)));
        map.put("network", NetUtils.getConnectType(context));// 当前使用网络
        map.put("os_version_code", String.valueOf(android.os.Build.VERSION.SDK_INT));// android系统版本
        map.put("os_version", Build.VERSION.RELEASE);// android系统版本

        map.put("uuid", DeviceUtils.getUUID(context));// uuid
        map.put("serial", Build.SERIAL);// 序列号
        map.put("androidId", DeviceUtils.getAndroidId(context));// androidId
        // 1.1.4新增的基础参数
        map.put("timeZone", DeviceUtils.getTimeZone());// 获取时区
        map.put("SDK_vn", BuildConfig.SDK_VERSIONNAME);
        map.put("SDK_vc", BuildConfig.VERSION_CODE + "");

        map.put("enDevId", PackageManagerUtil.getEncryptDeviceId(context));
        map.put("enDevUId", PackageManagerUtil.getEncryptDeviceUId(context));
        map.put("pkgName", context.getPackageName());

        return map;
    }



}
