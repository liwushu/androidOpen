/**
 * Copyright (c) 1998-2012 TENCENT Inc. All Rights Reserved.
 * 
 * FileName: QTHttpClient.java
 * 
 * Description: Http协议实现包装类文件
 * 
 * History: 1.0 devilxie 2012-09-07 Create
 */
package com.tcl.update.framework.network.http;

import android.os.Build;
import android.text.TextUtils;

import com.tcl.update.framework.log.NLog;
import com.tcl.update.framework.network.HostNameResolver;
import com.tcl.update.framework.util.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Http协议实现包装类，主要提供Get,Post以及Cancel方法 另外，将数据处理的逻辑进行了抽象，以便上层模块进行扩展
 * 
 * @author devilxie
 * @version 1.0
 */
public final class HttpClient {
    /**
     * 请求完成的异步回调接口，包括正常结束与异常结束
     * 
     * @author devilxie
     * @version 1.0
     */
    public interface OnCompletionListener {
        /**
         * 完成处理
         * 
         * @param client Http客户端
         * @param cause 完成原因，详见{@link NetworkError}
         * @param detail 具体详情
         */
        void onComplete(HttpClient client, int cause, int detail);
    }

    /**
     * 错误处理接口，请求过程中发生的任何错误将由该接口处理。重点关注连接超时、读写等错误
     * 
     * @author devilxie
     * @version 1.0
     */
    public interface OnErrorListener {
        /**
         * 错误处理
         * 
         * @param client Http客户端
         * @param error 错误原因，详见{@link NetworkError}
         * @param detail 具体详情
         */
        void onError(HttpClient client, int error, int detail);
    }

    /**
     * 连接异步回调接口
     * 
     * @author devilxie
     * @version 1.0
     */
    public interface OnConnectionListener {
        /**
         * 连接成功回调
         * 
         * @param client Http客户端
         */
        void onSuccess(HttpClient client);

        /**
         * 连接失败回调
         * 
         * @param client Http客户端
         */
        void onFail(HttpClient client);
    }

    /**
     * 上传进度跟踪回调
     * 
     * @author devilxie
     *
     */
    public interface OnProgressListener {
        /**
         * 进度回调
         * 
         * @param client 上传客户端
         * @param total 总长度
         * @param posted 目前上传长度
         */
        void onProgress(HttpClient client, int total, int posted);
    }

    private final static String TAG = "HttpClient";
    /* 数据处理缓存区大小 */
    private final static int MAX_BUFFER_LEN = 4096;
    /* 默认字符编码 */
    private final static String CHARSET = "utf-8";

    /* 客户端状态常量 */
    private final static int CS_INITILIZED = 0;
    private final static int CS_CONNECTING = 1;
    private final static int CS_CONNECTED = 2;
    private final static int CS_IOACCESS = 3;
    private final static int CS_STOPPED = 4;

    /* 连接超时时间值， 以毫秒为单位 */
    public static int CONNECT_TIMEOUT = 10000;
    /* 读写超时时间值， 以毫秒为单位 */
    public static int IO_TIMEOUT = 15000;

    protected int state = CS_INITILIZED;
    private String myURL = null;
    private volatile boolean mCancelled = false;
    private ContentHandler contentHandler = null;
    private final NetworkSensor sensor;
    private OnCompletionListener mOnCompletionListener = null;
    private OnErrorListener mOnErrorListener = null;
    private OnConnectionListener mOnConnectionListener = null;
    private OnProgressListener mOnProgressListener = null;
    private volatile HttpURLConnection mConnection = null;
    private String mUserAgent;

    public HttpClient() {
        this(null);
    }

    public HttpClient(NetworkSensor sensor) {
        if (sensor == null) {
            this.sensor = Network.getInstance();
        } else {
            this.sensor = sensor;
        }

    }

    public void setUserAgent(String ua) {
        this.mUserAgent = ua;
    }

    private static void addFormData(StringBuilder builder, String key, String val, String boudary) {
        builder.append("--");
        builder.append(boudary);
        builder.append("\r\n");
        builder.append("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n");
        try {
            builder.append(URLEncoder.encode(val, "utf-8"));
        } catch (UnsupportedEncodingException e) {}
        builder.append("\r\n");
    }

    private static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private HttpURLConnection openConnection(String url, InetSocketAddress proxy) {
        return openConnection(url, proxy, null);
    }

    private HttpURLConnection openConnection(String url, InetSocketAddress proxy, Collection<NameValuePair> headers) {
        disableConnectionReuseIfNecessary();
        if (url == null) {
            throw new NullPointerException("parameter url is null!");
        }

        HttpURLConnection conn = null;
        url = HostNameResolver.resovleURL(url);
        NLog.d(TAG, "resolved url: " + url);

        try {
            URL uri = new URL(url);
            if ("https".equalsIgnoreCase(uri.getProtocol())) {
                httpsSecurityAcceptAny();
            }

            if (proxy == null) {
                conn = (HttpURLConnection) uri.openConnection();
            } else {
                // 通过移动网络接入点访问
                Proxy localProxy = new Proxy(Proxy.Type.HTTP, proxy);
                conn = (HttpURLConnection) uri.openConnection(localProxy);
            }

            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(IO_TIMEOUT);


            if (!CollectionUtils.isEmpty(headers)) {
                for (NameValuePair pair : headers) {
                    conn.setRequestProperty(pair.getName(), pair.getValue());
                }
            }

            if (!TextUtils.isEmpty(mUserAgent)) {
                conn.setRequestProperty("User-Agent", mUserAgent);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
            conn = null;
        }

        return conn;
    }

    /**
     * 设置请求结束监听回调
     * 
     * @param listener 请求结束监听器
     */
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.mOnCompletionListener = listener;
    }

    /**
     * 设置错误监听回调
     * 
     * @param listener 错误监听器
     */
    public void setOnErrorListener(OnErrorListener listener) {
        this.mOnErrorListener = listener;
    }

    /**
     * 设置连接监听回调
     * 
     * @param listener 连接监听器
     */
    public void setOnConnectionListener(OnConnectionListener listener) {
        this.mOnConnectionListener = listener;
    }

    /**
     * 设置数据处理回调
     * 
     * @param handler 数据处理器
     */
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    /**
     * 设置上传进度跟踪监听器
     * 
     * @param l 进度跟踪监听器
     */
    public void setProgressListener(OnProgressListener l) {
        this.mOnProgressListener = l;
    }

    private void progress(int total, int posted) {
        final OnProgressListener listener = mOnProgressListener;
        if (listener != null) {
            listener.onProgress(this, total, posted);
        }
    }

    /**
     * 获取最近一次请求的URL
     * 
     * @return 返回最近一次请求的URL
     */
    public String getURL() {
        return myURL;
    }

    public int fetchInternetResource(String url, long pos, long len) {
        return fetchInternetResource(url, pos, len, null, null);
    }

    /**
     * 使用Http协议的GET方法获取指定资源, 这是同步方法
     * 
     * @param url 资源URI
     * @param pos 起始位置，大于等于0（<0，等同于=0)
     * @param len 所需要获取的数据长度, 负数表示到资源末尾.
     * @param headers 请求头扩展字段
     * @param rhh 响应请求拦截处理器，处理结果会影响到后续的数据下载阶段
     *
     * @return 返回操作结果，具体取值，请参考{@link NetworkError}
     */
    public synchronized int fetchInternetResource(String url, long pos, long len, Collection<NameValuePair> headers,
            HttpHeaderHandler rhh) {
        NLog.v(TAG, "fetchInternetResource  %s", url);
        if (!checkValidHttpUrl(url)) throw new InvalidHttpURIException("invalid http uri!");

        myURL = url;
        int ret = NetworkError.SUCCESS;
        HttpURLConnection conn = null;
        BufferedInputStream nis = null;
        long totalLength = 0;
        int status = 0;
        if (mCancelled) {
            return NetworkError.CANCEL;
        }
        start();

        if (pos < 0L) pos = 0L;

        do {
            try {
                // 网络检查
                if (!sensor.hasAvailableNetwork()) {
                    ret = NetworkError.NO_AVALIABLE_NETWORK;
                    if (mOnErrorListener != null) mOnErrorListener.onError(this, ret, 0);

                    break;
                }

                conn = openConnection(url, sensor.getProxy(), headers);
                // 开始下载前，检测一下是否要中断下载过程
                if (mCancelled) {
                    ret = NetworkError.CANCEL;
                    break;
                }

                mConnection = conn;
                // 进行断点续传的设置
                if (len > 0) {
                    conn.setRequestProperty("RANGE", "bytes=" + pos + "-" + (pos + len - 1));
                } else if (pos > 0) {
                    conn.setRequestProperty("RANGE", "bytes=" + pos + "-");
                }

                conn.setRequestMethod("GET");
                if (mCancelled) {
                    ret = NetworkError.CANCEL;
                    break;
                }
                NLog.d(TAG, "connecting....");
                conn.connect();
                NLog.d(TAG, "connected");
            } catch (Exception e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;

                if (mOnConnectionListener != null) mOnConnectionListener.onFail(this);

                if (mOnErrorListener != null) mOnErrorListener.onError(this, ret, 0);

                break;
            }

            setState(CS_CONNECTED);
            if (mOnConnectionListener != null) mOnConnectionListener.onSuccess(this);

            try {

                NLog.d(TAG, "fetchInternetResource  request response code");
                if (mCancelled) {
                    ret = NetworkError.CANCEL;
                    break;
                }

                setState(CS_IOACCESS);
                /* 发送并等待响应 */
                status = conn.getResponseCode();
                boolean isOK = (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_PARTIAL);
                NLog.d(TAG, "fetchInternetResource  response code: %d", status);

                if (!isOK) {
                    NLog.w(TAG, "fetchInternetResource  failed!");
                    // 资源未找到，正常结束
                    if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                        ret = NetworkError.FAIL_NOT_FOUND;
                        if (mOnErrorListener != null) mOnErrorListener.onError(this, ret, status);
                    }

                    else if (status == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        ret = NetworkError.SUCCESS;
                    }
                    // 否则当作网络未知错误处理
                    else {
                        ret = NetworkError.FAIL_UNKNOWN;
                        if (mOnErrorListener != null) mOnErrorListener.onError(this, ret, status);

                    }

                    break;
                }

                // 确保资源获取时发生越界获取时，重取整个资源的有效性
                if (status == HttpURLConnection.HTTP_OK) {
                    pos = 0L;
                }
            } catch (Exception e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_IO_ERROR;
                if (mOnErrorListener != null) mOnErrorListener.onError(this, ret, 0);

                break;
            }

            if (mCancelled) {
                ret = NetworkError.CANCEL;
                break;
            }

            // 处理响应头信息
            if (rhh != null) {
                ret = rhh.handle(conn);
                if (ret != NetworkError.SUCCESS) {
                    break;
                }
            }

            status = 0;
            // 开始下载前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                break;
            }

            if (contentHandler == null) {
                break;
            }

            totalLength = conn.getContentLength();
            NLog.d(TAG, "total length: %d", totalLength);
            if (totalLength == -1 && isChunked(conn)) {
                totalLength = conn.getHeaderFieldInt("Accept-Length", -1);
                NLog.d(TAG, "Accept-Length:" + totalLength);
            }

            if (totalLength > 0) totalLength += pos;

            // 准备处理响应数据
            if (!contentHandler.prepare(pos, totalLength)) {
                ret = NetworkError.FAIL_IO_ERROR;
                break;
            }

            // 开始下载前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                break;
            }

            int recvBytes = 0;
            int curpos = (int) pos;
            int buflen = MAX_BUFFER_LEN;
            byte[] buffer = new byte[MAX_BUFFER_LEN];
            // 开始下载
            try {
                nis = new BufferedInputStream(conn.getInputStream());
                while (!mCancelled && (recvBytes = nis.read(buffer, 0, buflen)) != -1) {

                    if (recvBytes == 0) {
                        continue;
                    } else {
                        // 数据处理
                        if (contentHandler.handle(buffer, 0, recvBytes)) {
                            curpos += recvBytes;
                        } else {
                            ret = NetworkError.FAIL_IO_ERROR;
                            break;
                        }
                    }
                }

                NLog.d(TAG, "current length: %d", curpos);
                ret = !mCancelled ? ret : NetworkError.CANCEL;
            } catch (IOException e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_IO_ERROR;
            } finally {
                try {
                    if (nis != null) nis.close();
                } catch (IOException e) {}
            }

            // 下载过程完成
            contentHandler.complete(ret, curpos);

        } while (false);
        if (conn != null) conn.disconnect();

        ret = !mCancelled ? ret : NetworkError.CANCEL;

        stop();
        // 下载结束处理
        if (mOnCompletionListener != null) mOnCompletionListener.onComplete(this, ret, status);

        return ret;
    }

    public int postInternetResource(String url, String charset, byte[] datas) {
        return postInternetResource(url, charset, null, datas, null);
    }

    /**
     * 使用Http协议的Post方法上传数据并获取指定资源, 同步方法
     * 
     * @param url 资源URI
     * @param charset 请求数据的字符编码， 为null时默认使用"utf-8"编码
     * @param formdatas post表单参数集，以名值对表示， 不能为空
     * @param rhh 响应头处理器，可为null
     * @return 返回操作结果，具体取值，请参考{@link NetworkError}
     */
    public synchronized int postFormDatas(String url, String charset, Collection<NameValuePair> formdatas,
            HttpHeaderHandler rhh) {
        NLog.v(TAG, "postFormDatas " + url);
        if (!checkValidHttpUrl(url)) throw new InvalidHttpURIException("invalid http uri!");

        if (CollectionUtils.isEmpty(formdatas)) throw new IllegalArgumentException("form datas empty!");

        int ret = NetworkError.SUCCESS;
        int rescode = 0;
        BufferedInputStream nis = null;
        boolean isOK = false;
        String boundary = null;
        StringBuilder sb = null;
        DataOutputStream out = null;
        int len = 0;
        byte[] ends = null;

        myURL = url;
        // 初始化相关信号
        start();
        do {
            if (!sensor.hasAvailableNetwork()) {
                ret = NetworkError.NO_AVALIABLE_NETWORK;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            // 建立连接
            HttpURLConnection conn = openConnection(url, sensor.getProxy());
            if (conn == null) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            try {
                mConnection = conn;
                conn.setRequestMethod("POST");
                if (TextUtils.isEmpty(charset)) {
                    charset = CHARSET;
                }
                conn.setRequestProperty("Charset", charset);
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                boundary = getBoundary();
                String end = "\r\n--" + boundary + "--\r\n";
                ends = end.getBytes();
                len += ends.length;

                if (!CollectionUtils.isEmpty(formdatas)) {
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    sb = new StringBuilder();
                    for (NameValuePair pair : formdatas) {
                        addFormData(sb, pair.getName(), pair.getValue(), boundary);
                    }
                }

            } catch (ProtocolException e1) {
                ret = NetworkError.FAIL_UNKNOWN;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            byte[] data = sb.toString().getBytes();
            try {
                len += data.length;
                conn.setRequestProperty("Content-Length", String.valueOf(len));
                conn.connect();
            } catch (IOException e) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            setState(CS_CONNECTED);
            if (mOnConnectionListener != null) {
                mOnConnectionListener.onSuccess(this);
            }

            // 开始发送前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }


            try {
                setState(CS_IOACCESS);
                out = new DataOutputStream(conn.getOutputStream());
                int posted = 0;
                progress(len, 0);
                if (data != null && data.length > 0) {
                    out.write(data);
                    progress(len, data.length);
                    posted = data.length;
                }

                int offset = 0;
                int left = ends.length;
                do {
                    int need = Math.min(left, 2048);
                    out.write(ends, offset, need);
                    left -= need;
                    offset += need;
                    posted += need;
                    progress(len, posted);
                } while (left > 0);

                out.flush();

                rescode = conn.getResponseCode();
                NLog.d(TAG, "postFormDatas  rescode = " + rescode);
                isOK = (rescode == HttpURLConnection.HTTP_OK);

                if (!isOK) {
                    NLog.w(TAG, "postFormDatas  failed!");
                    conn.disconnect();
                    // 资源未找到，正常结束
                    if (rescode == HttpURLConnection.HTTP_NOT_FOUND) {
                        ret = NetworkError.FAIL_NOT_FOUND;

                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }
                    // 未修改状态码，可当成功处理！
                    else if (rescode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        ret = NetworkError.SUCCESS;
                    }

                    // 否则当作网络未知错误处理
                    else {
                        ret = NetworkError.FAIL_UNKNOWN;
                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }

                    break;
                }

            } catch (IOException e) {
                NLog.printStackTrace(e);
                conn.disconnect();
                ret = NetworkError.FAIL_IO_ERROR;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            rescode = 0;
            // 处理响应头信息
            if (rhh != null) {
                ret = rhh.handle(conn);
                if (ret != NetworkError.SUCCESS) {
                    conn.disconnect();
                    break;
                }
            }

            // 开始下载前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            if (contentHandler == null) {
                conn.disconnect();
                break;
            }

            // 处理可接受的资源数据
            int totalLength = 0;
            totalLength = conn.getContentLength();
            NLog.d(TAG, "totalLength=" + totalLength);

            if (totalLength == -1 && isChunked(conn)) {
                totalLength = conn.getHeaderFieldInt("Accept-Length", -1);
                NLog.d(TAG, "Accept-Length: " + totalLength);
            }

            // 准备处理响应数据
            if (!contentHandler.prepare(0, totalLength)) {
                ret = NetworkError.FAIL_IO_ERROR;
                conn.disconnect();
                break;
            }

            int recvBytes = 0;
            int curpos = 0;
            int buflen = MAX_BUFFER_LEN;
            byte[] buffer = new byte[MAX_BUFFER_LEN];
            // 开始下载

            try {
                nis = new BufferedInputStream(conn.getInputStream());
                while (!mCancelled && (recvBytes = nis.read(buffer, 0, buflen)) != -1) {

                    if (recvBytes == 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            ret = NetworkError.CANCEL;
                            break; // 线程中断，可能是外面提前杀死该线程
                        }
                    } else {
                        // 数据处理
                        if (contentHandler.handle(buffer, 0, recvBytes)) {
                            curpos += recvBytes;
                        } else {
                            ret = NetworkError.FAIL_IO_ERROR;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_IO_ERROR;
            } finally {
                conn.disconnect();
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {}
                }

                try {
                    nis.close();
                } catch (IOException e) {}

                nis = null;
            }

            // 下载过程完成
            contentHandler.complete(ret, curpos);

        } while (false);

        stop();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onComplete(this, ret, rescode);
        }
        return ret;
    }

    private static String getBoundary() {
        long leastSigBits = UUID.randomUUID().getLeastSignificantBits();
        String lsbStr = Long.toHexString(leastSigBits);
        return lsbStr;
    }

    /**
     * 上传文件
     * 
     * @param url 指定URL
     * @param charset 字符集，null将采用utf-8
     * @param formdatas 表单数据集
     * @param reqprops 附加请求头集
     * @param fieldName 上传文件指定的字段名
     * @param file 指定文件
     * @param rhh 响应头处理器，可为null
     * @return 返回网络错误码。
     */
    public synchronized int postFile(String url, String charset, Collection<NameValuePair> formdatas,
            Collection<NameValuePair> reqprops, String fieldName, File file, HttpHeaderHandler rhh) {
        NLog.v(TAG, "postFile " + url);
        if (!checkValidHttpUrl(url)) throw new InvalidHttpURIException("invalid http uri!");

        if (TextUtils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("fieldName is invalid!");
        }

        if (file == null || !file.exists()) throw new IllegalArgumentException("file is invalid! null or not exists");

        int ret = NetworkError.SUCCESS;
        int rescode = 0;
        BufferedInputStream nis = null;
        boolean isOK = false;
        String boundary = null;
        StringBuilder sb = null;
        DataOutputStream out = null;
        int len = (int) file.length();
        int fileLen = len;
        String filename = file.getName();
        BufferedInputStream fStream = null;
        byte[] ends = null;

        myURL = url;
        // 初始化相关信号
        start();
        do {
            if (!sensor.hasAvailableNetwork()) {
                ret = NetworkError.NO_AVALIABLE_NETWORK;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            // 建立连接
            HttpURLConnection conn = openConnection(url, sensor.getProxy(), reqprops);
            if (conn == null) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            try {
                mConnection = conn;
                conn.setRequestMethod("POST");
                if (TextUtils.isEmpty(charset)) {
                    charset = CHARSET;
                }
                conn.setRequestProperty("Charset", charset);
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                boundary = getBoundary();
                String end = "\r\n--" + boundary + "--\r\n";
                ends = end.getBytes();
                len += ends.length;

                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                if (!CollectionUtils.isEmpty(formdatas)) {
                    sb = new StringBuilder();
                    for (NameValuePair pair : formdatas) {
                        addFormData(sb, pair.getName(), pair.getValue(), boundary);
                    }
                }

                // 发送文件信息
                sb.append("--");
                sb.append(boundary);
                sb.append("\r\n");
                sb.append(
                        "Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + filename + "\"\r\n");
                sb.append("Content-Type: application/octet-stream\r\n\r\n");
            } catch (ProtocolException e1) {
                ret = NetworkError.FAIL_UNKNOWN;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            byte[] data = sb.toString().getBytes();
            try {
                len += data.length;
                conn.setRequestProperty("Content-Length", String.valueOf(len));
                conn.connect();
            } catch (IOException e) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            setState(CS_CONNECTED);
            if (mOnConnectionListener != null) {
                mOnConnectionListener.onSuccess(this);
            }

            // 开始发送前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            try {
                setState(CS_IOACCESS);
                out = new DataOutputStream(conn.getOutputStream());
                int posted = 0;
                progress(len, posted);
                if (data != null) {
                    out.write(data);
                    posted += data.length;
                    progress(len, posted);
                }

                int left = fileLen;
                byte[] databuf = new byte[2048];
                fStream = new BufferedInputStream(new FileInputStream(file));

                do {
                    int need = fStream.read(databuf);
                    if (need < 0) break;

                    out.write(databuf, 0, need);
                    left -= need;
                    posted += need;
                    progress(len, posted);

                } while (left > 0);

                if (ends != null) {
                    out.write(ends);
                    posted += ends.length;
                    progress(len, posted);
                }

                out.flush();

                rescode = conn.getResponseCode();
                NLog.d(TAG, "postFile  rescode = " + rescode);
                isOK = (rescode == HttpURLConnection.HTTP_OK);

                if (!isOK) {
                    NLog.w(TAG, "postFile  failed!");
                    conn.disconnect();
                    // 资源未找到，正常结束
                    if (rescode == HttpURLConnection.HTTP_NOT_FOUND) {
                        ret = NetworkError.FAIL_NOT_FOUND;

                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }
                    // 未修改状态码，可当成功处理！
                    else if (rescode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        ret = NetworkError.SUCCESS;
                    }

                    // 否则当作网络未知错误处理
                    else {
                        ret = NetworkError.FAIL_UNKNOWN;
                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }

                    break;
                }

            } catch (IOException e) {
                NLog.printStackTrace(e);
                conn.disconnect();
                ret = NetworkError.FAIL_IO_ERROR;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            } finally {
                if (fStream != null) {
                    try {
                        fStream.close();
                    } catch (IOException e) {}
                }
            }

            rescode = 0;
            // 处理响应头信息
            if (rhh != null) {
                ret = rhh.handle(conn);
                if (ret != NetworkError.SUCCESS) {
                    conn.disconnect();
                    break;
                }
            }

            // 开始下载前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            if (contentHandler == null) {
                conn.disconnect();
                break;
            }

            // 处理可接受的资源数据
            int totalLength = 0;
            totalLength = conn.getContentLength();
            NLog.d(TAG, "totalLength=" + totalLength);

            if (totalLength == -1 && isChunked(conn)) {
                totalLength = conn.getHeaderFieldInt("Accept-Length", -1);
                NLog.d(TAG, "Accept-Length: " + totalLength);
            }

            // 准备处理响应数据
            if (!contentHandler.prepare(0, totalLength)) {
                ret = NetworkError.FAIL_IO_ERROR;
                conn.disconnect();
                break;
            }

            int recvBytes = 0;
            int curpos = 0;
            int buflen = MAX_BUFFER_LEN;
            byte[] buffer = new byte[MAX_BUFFER_LEN];
            // 开始下载

            try {
                nis = new BufferedInputStream(conn.getInputStream());
                while (!mCancelled && (recvBytes = nis.read(buffer, 0, buflen)) != -1) {

                    if (recvBytes == 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            ret = NetworkError.CANCEL;
                            break; // 线程中断，可能是外面提前杀死该线程
                        }
                    } else {
                        // 数据处理
                        if (contentHandler.handle(buffer, 0, recvBytes)) {
                            curpos += recvBytes;
                        } else {
                            ret = NetworkError.FAIL_IO_ERROR;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_IO_ERROR;
            } finally {
                conn.disconnect();
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {}
                }

                try {
                    nis.close();
                } catch (IOException e) {}

                nis = null;
            }

            // 下载过程完成
            contentHandler.complete(ret, curpos);

        } while (false);

        stop();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onComplete(this, ret, rescode);
        }
        return ret;
    }

    /**
     * 使用Http协议的Post方法上传文件, 同步方法
     * 
     * @param url 资源URI
     * @param charset 请求数据的字符编码， 为null时默认使用"utf-8"编码
     * @param formdatas post表单参数集，以名值对表示，可为空
     * @param reqprops 附带的请求头附加集，可为空
     * @param fieldName 上传文件指定的字段名
     * @param filename 待上传的文件名，可不带路径
     * @param filedata 待上传的文件数据
     * @param rhh 响应头处理器，可为null
     * @return 返回操作结果，具体取值，请参考{@link NetworkError}
     */
    public synchronized int postFile(String url, String charset, Collection<NameValuePair> formdatas,
            Collection<NameValuePair> reqprops, String fieldName, String filename, byte[] filedata,
            HttpHeaderHandler rhh) {
        NLog.v(TAG, "postFile " + url);
        if (!checkValidHttpUrl(url)) throw new InvalidHttpURIException("invalid http uri!");

        int ret = NetworkError.SUCCESS;
        int rescode = 0;
        BufferedInputStream nis = null;
        boolean isOK = false;
        String boundary = null;
        StringBuilder sb = null;
        DataOutputStream out = null;
        int len = filedata.length;
        byte[] ends = null;

        myURL = url;
        // 初始化相关信号
        start();
        do {
            if (!sensor.hasAvailableNetwork()) {
                ret = NetworkError.NO_AVALIABLE_NETWORK;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            // 建立连接
            HttpURLConnection conn = openConnection(url, sensor.getProxy(), reqprops);
            if (conn == null) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            try {
                mConnection = conn;
                conn.setRequestMethod("POST");
                if (TextUtils.isEmpty(charset)) {
                    charset = CHARSET;
                }
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", charset);
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                boundary = getBoundary();
                String end = "\r\n--" + boundary + "--\r\n";
                ends = end.getBytes();
                len += ends.length;

                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                if (!CollectionUtils.isEmpty(formdatas)) {
                    sb = new StringBuilder();
                    for (NameValuePair pair : formdatas) {
                        addFormData(sb, pair.getName(), pair.getValue(), boundary);
                    }
                }

                // 发送文件信息
                sb.append("--");
                sb.append(boundary);
                sb.append("\r\n");
                sb.append(
                        "Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + filename + "\"\r\n");
                sb.append("Content-Type: application/octet-stream\r\n\r\n");
            } catch (ProtocolException e1) {
                ret = NetworkError.FAIL_UNKNOWN;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            byte[] data = sb.toString().getBytes();
            try {
                len += data.length;
                conn.setRequestProperty("Content-Length", String.valueOf(len));
                conn.connect();
            } catch (IOException e) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            setState(CS_CONNECTED);
            if (mOnConnectionListener != null) {
                mOnConnectionListener.onSuccess(this);
            }

            // 开始发送前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            try {
                setState(CS_IOACCESS);
                out = new DataOutputStream(conn.getOutputStream());
                int posted = 0;
                progress(len, posted);
                if (data != null) {
                    out.write(data);
                    posted += data.length;
                    progress(len, posted);
                }

                int left = filedata.length;
                int offset = 0;
                do {
                    int need = Math.min(left, 2048);
                    if (need == 0) break;
                    out.write(filedata, offset, need);
                    offset += need;
                    left -= need;
                    posted += need;
                    progress(len, posted);

                } while (left > 0);

                if (ends != null) {
                    out.write(ends);
                    posted += ends.length;
                    progress(len, posted);
                }

                out.flush();

                rescode = conn.getResponseCode();
                NLog.d(TAG, "postFile  rescode = " + rescode);
                isOK = (rescode == HttpURLConnection.HTTP_OK);

                if (!isOK) {
                    NLog.w(TAG, "postFile  failed!");
                    conn.disconnect();
                    // 资源未找到，正常结束
                    if (rescode == HttpURLConnection.HTTP_NOT_FOUND) {
                        ret = NetworkError.FAIL_NOT_FOUND;

                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }
                    // 未修改状态码，可当成功处理！
                    else if (rescode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        ret = NetworkError.SUCCESS;
                    }

                    // 否则当作网络未知错误处理
                    else {
                        ret = NetworkError.FAIL_UNKNOWN;
                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }

                    break;
                }

            } catch (IOException e) {
                NLog.printStackTrace(e);
                conn.disconnect();
                ret = NetworkError.FAIL_IO_ERROR;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            rescode = 0;
            // 处理响应头信息
            if (rhh != null) {
                ret = rhh.handle(conn);
                if (ret != NetworkError.SUCCESS) {
                    conn.disconnect();
                    break;
                }
            }

            // 开始下载前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            if (contentHandler == null) {
                conn.disconnect();
                break;
            }

            // 处理可接受的资源数据
            int totalLength = 0;
            totalLength = conn.getContentLength();
            NLog.d(TAG, "totalLength=" + totalLength);

            if (totalLength == -1 && isChunked(conn)) {
                totalLength = conn.getHeaderFieldInt("Accept-Length", -1);
                NLog.d(TAG, "Accept-Length: " + totalLength);
            }

            // 准备处理响应数据
            if (!contentHandler.prepare(0, totalLength)) {
                ret = NetworkError.FAIL_IO_ERROR;
                conn.disconnect();
                break;
            }

            int recvBytes = 0;
            int curpos = 0;
            int buflen = MAX_BUFFER_LEN;
            byte[] buffer = new byte[MAX_BUFFER_LEN];
            // 开始下载

            try {
                nis = new BufferedInputStream(conn.getInputStream());
                while (!mCancelled && (recvBytes = nis.read(buffer, 0, buflen)) != -1) {

                    if (recvBytes == 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            ret = NetworkError.CANCEL;
                            break; // 线程中断，可能是外面提前杀死该线程
                        }
                    } else {
                        // 数据处理
                        if (contentHandler.handle(buffer, 0, recvBytes)) {
                            curpos += recvBytes;
                        } else {
                            ret = NetworkError.FAIL_IO_ERROR;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_IO_ERROR;
            } finally {
                conn.disconnect();
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {}
                }

                try {
                    nis.close();
                } catch (IOException e) {}

                nis = null;
            }

            // 下载过程完成
            contentHandler.complete(ret, curpos);

        } while (false);

        stop();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onComplete(this, ret, rescode);
        }
        return ret;
    }

    public synchronized int postInternetResource(String url, String charset, Collection<NameValuePair> reqprops,
            byte[] data, HttpHeaderHandler rhh) {
        NLog.v(TAG, "postInternetResource " + url);
        if (!checkValidHttpUrl(url)) throw new InvalidHttpURIException("invalid http uri!");

        int ret = NetworkError.SUCCESS;
        int rescode = 0;
        BufferedInputStream nis = null;
        boolean isOK = false;
        DataOutputStream out = null;

        myURL = url;
        // 初始化相关信号
        start();
        do {
            if (!sensor.hasAvailableNetwork()) {
                ret = NetworkError.NO_AVALIABLE_NETWORK;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            // 建立连接
            HttpURLConnection conn = openConnection(url, sensor.getProxy(), reqprops);
            if (conn == null) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            try {
                mConnection = conn;
                conn.setRequestMethod("POST");
                if (TextUtils.isEmpty(charset)) {
                    charset = CHARSET;
                }
                conn.setRequestProperty("Charset", charset);
                conn.setUseCaches(false);
            } catch (ProtocolException e1) {
                ret = NetworkError.FAIL_UNKNOWN;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            try {
                conn.connect();
            } catch (IOException e) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            setState(CS_CONNECTED);
            if (mOnConnectionListener != null) {
                mOnConnectionListener.onSuccess(this);
            }

            // 开始发送前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            try {
                setState(CS_IOACCESS);
                out = new DataOutputStream(conn.getOutputStream());
                int left = data.length;
                progress(left, 0);
                int offset = 0;
                do {
                    int need = Math.min(left, 2048);
                    if (need == 0) break;
                    out.write(data, offset, need);
                    offset += need;
                    left -= need;
                    progress(data.length, offset);
                } while (left > 0);

                out.flush();

                rescode = conn.getResponseCode();
                NLog.d(TAG, "postInternetResource  rescode = " + rescode);
                isOK = (rescode == HttpURLConnection.HTTP_OK);

                if (!isOK) {
                    NLog.w(TAG, "postInternetResource  failed!");
                    conn.disconnect();
                    // 资源未找到，正常结束
                    if (rescode == HttpURLConnection.HTTP_NOT_FOUND) {
                        ret = NetworkError.FAIL_NOT_FOUND;

                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }
                    // 未修改状态码，可当成功处理！
                    else if (rescode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        ret = NetworkError.SUCCESS;
                    }

                    // 否则当作网络未知错误处理
                    else {
                        ret = NetworkError.FAIL_UNKNOWN;
                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }

                    break;
                }

            } catch (IOException e) {
                NLog.printStackTrace(e);
                conn.disconnect();
                ret = NetworkError.FAIL_IO_ERROR;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            rescode = 0;
            // 处理响应头信息
            if (rhh != null) {
                ret = rhh.handle(conn);
                if (ret != NetworkError.SUCCESS) {
                    conn.disconnect();
                    break;
                }
            }

            // 开始下载前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            if (contentHandler == null) {
                conn.disconnect();
                break;
            }

            // 处理可接受的资源数据
            int totalLength = 0;
            totalLength = conn.getContentLength();
            NLog.d(TAG, "totalLength=" + totalLength);

            if (totalLength == -1 && isChunked(conn)) {
                totalLength = conn.getHeaderFieldInt("Accept-Length", -1);
                NLog.d(TAG, "Accept-Length: " + totalLength);
            }

            // 准备处理响应数据
            if (!contentHandler.prepare(0, totalLength)) {
                ret = NetworkError.FAIL_IO_ERROR;
                conn.disconnect();
                break;
            }

            int recvBytes = 0;
            int curpos = 0;
            int buflen = MAX_BUFFER_LEN;
            byte[] buffer = new byte[MAX_BUFFER_LEN];
            // 开始下载

            try {
                nis = new BufferedInputStream(conn.getInputStream());
                while (!mCancelled && (recvBytes = nis.read(buffer, 0, buflen)) != -1) {

                    if (recvBytes == 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            ret = NetworkError.CANCEL;
                            break; // 线程中断，可能是外面提前杀死该线程
                        }
                    } else {
                        // 数据处理
                        if (contentHandler.handle(buffer, 0, recvBytes)) {
                            curpos += recvBytes;
                        } else {
                            ret = NetworkError.FAIL_IO_ERROR;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_IO_ERROR;
            } finally {
                conn.disconnect();
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {}
                }

                try {
                    nis.close();
                } catch (IOException e) {}

                nis = null;
            }

            // 下载过程完成
            contentHandler.complete(ret, curpos);

        } while (false);

        stop();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onComplete(this, ret, rescode);
        }
        return ret;
    }

    /**
     * 使用Http协议的Post方法上传数据并获取指定资源, 同步方法
     * 
     * @param url 资源URI
     * @param charset 请求数据的字符编码， 为null时默认使用"utf-8"编码
     * @param is 需要上传的数据流
     * @param reqprops 具体上传数据的请求属性，可为null
     * @param rhh 响应头处理器，可为null
     * @return 返回操作结果，具体取值，请参考{@link NetworkError}
     */
    public synchronized int postInternetResource(String url, String charset, InputStream is,
            Collection<NameValuePair> reqprops, HttpHeaderHandler rhh) {
        NLog.v(TAG, "postInternetResource " + url);
        if (!checkValidHttpUrl(url)) throw new InvalidHttpURIException("invalid http uri!");

        int ret = NetworkError.SUCCESS;
        int rescode = 0;
        BufferedInputStream nis = null;
        boolean isOK = false;
        StringBuilder sb = null;
        DataOutputStream out = null;

        myURL = url;
        // 初始化相关信号
        start();
        do {
            if (!sensor.hasAvailableNetwork()) {
                ret = NetworkError.NO_AVALIABLE_NETWORK;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            // 建立连接
            HttpURLConnection conn = openConnection(url, sensor.getProxy(), reqprops);
            if (conn == null) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            try {
                mConnection = conn;
                conn.setRequestMethod("POST");
                if (TextUtils.isEmpty(charset)) {
                    charset = CHARSET;
                }
                conn.setRequestProperty("Charset", charset);
                conn.setUseCaches(false);
            } catch (ProtocolException e1) {
                ret = NetworkError.FAIL_UNKNOWN;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            try {
                conn.connect();
            } catch (IOException e) {
                ret = NetworkError.FAIL_CONNECT_TIMEOUT;
                if (mOnConnectionListener != null) {
                    mOnConnectionListener.onFail(this);
                }
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            setState(CS_CONNECTED);
            if (mOnConnectionListener != null) {
                mOnConnectionListener.onSuccess(this);
            }

            // 开始发送前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            try {
                setState(CS_IOACCESS);
                out = new DataOutputStream(conn.getOutputStream());
                if (sb != null) {
                    out.write(sb.toString().getBytes());
                }

                if (is != null) {
                    byte[] bytes = new byte[MAX_BUFFER_LEN];
                    int readBytes = 0;
                    do {

                        readBytes = is.read(bytes);
                        if (readBytes == -1) {
                            break;
                        }

                        out.write(bytes, 0, readBytes);

                    } while (true);
                }

                out.flush();

                rescode = conn.getResponseCode();
                NLog.d(TAG, "postInternetResource  rescode = " + rescode);
                isOK = (rescode == HttpURLConnection.HTTP_OK);

                if (!isOK) {
                    NLog.w(TAG, "postInternetResource  failed!");
                    conn.disconnect();
                    // 资源未找到，正常结束
                    if (rescode == HttpURLConnection.HTTP_NOT_FOUND) {
                        ret = NetworkError.FAIL_NOT_FOUND;

                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }
                    // 未修改状态码，可当成功处理！
                    else if (rescode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        ret = NetworkError.SUCCESS;
                    }

                    // 否则当作网络未知错误处理
                    else {
                        ret = NetworkError.FAIL_UNKNOWN;
                        if (mOnErrorListener != null) {
                            mOnErrorListener.onError(this, ret, rescode);
                        }
                    }

                    break;
                }

            } catch (IOException e) {
                NLog.printStackTrace(e);
                conn.disconnect();
                ret = NetworkError.FAIL_IO_ERROR;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(this, ret, 0);
                }
                break;
            }

            rescode = 0;
            // 处理响应头信息
            if (rhh != null) {
                ret = rhh.handle(conn);
                if (ret != NetworkError.SUCCESS) {
                    conn.disconnect();
                    break;
                }
            }

            // 开始下载前，检测一下是否要中断下载过程
            if (mCancelled) {
                ret = NetworkError.CANCEL;
                conn.disconnect();
                break;
            }

            if (contentHandler == null) {
                conn.disconnect();
                break;
            }

            // 处理可接受的资源数据
            int totalLength = 0;
            totalLength = conn.getContentLength();
            NLog.d(TAG, "totalLength=" + totalLength);

            if (totalLength == -1 && isChunked(conn)) {
                totalLength = conn.getHeaderFieldInt("Accept-Length", -1);
                NLog.d(TAG, "Accept-Length: " + totalLength);
            }

            // 准备处理响应数据
            if (!contentHandler.prepare(0, totalLength)) {
                ret = NetworkError.FAIL_IO_ERROR;
                conn.disconnect();
                break;
            }

            int recvBytes = 0;
            int curpos = 0;
            int buflen = MAX_BUFFER_LEN;
            byte[] buffer = new byte[MAX_BUFFER_LEN];
            // 开始下载

            try {
                nis = new BufferedInputStream(conn.getInputStream());
                while (!mCancelled && (recvBytes = nis.read(buffer, 0, buflen)) != -1) {

                    if (recvBytes == 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            ret = NetworkError.CANCEL;
                            break; // 线程中断，可能是外面提前杀死该线程
                        }
                    } else {
                        // 数据处理
                        if (contentHandler.handle(buffer, 0, recvBytes)) {
                            curpos += recvBytes;
                        } else {
                            ret = NetworkError.FAIL_IO_ERROR;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                NLog.printStackTrace(e);
                ret = NetworkError.FAIL_IO_ERROR;
            } finally {
                conn.disconnect();
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {}
                }

                try {
                    nis.close();
                } catch (IOException e) {}

                nis = null;
            }

            // 下载过程完成
            contentHandler.complete(ret, curpos);

        } while (false);

        stop();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onComplete(this, ret, rescode);
        }
        return ret;
    }

    public void cancel() {
        NLog.v(TAG, "cancel");
        if (mCancelled) return;

        mCancelled = true;
        final HttpURLConnection conn = mConnection;
        if (conn != null) {
            conn.disconnect();
        }
    }

    public void clearLast() {
        mCancelled = false;
        myURL = null;
        mConnection = null;
        setState(CS_INITILIZED);
    }

    private boolean isChunked(HttpURLConnection conn) {

        String value = conn.getHeaderField("Transfer-Encoding");
        return "chunked".equalsIgnoreCase(value);
    }

    protected void setState(int s) {
        this.state = s;
    }

    private boolean checkValidHttpUrl(String url) {
        if (TextUtils.isEmpty(url)) return false;

        int index = url.indexOf("://");
        if (index == -1) {
            return false;
        }

        try {
            URL uri = new URL(url);
            if (!"http".equalsIgnoreCase(uri.getProtocol())) return false;
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }

    private void start() {
        if (state != CS_INITILIZED) {
            throw new IllegalStateException(
                    "HttpClient cannot start in state " + state + ", you should call clearLast() first!");
        }
        setState(CS_CONNECTING);
    }

    private void stop() {
        mCancelled = false;
        mConnection = null;
        setState(CS_STOPPED);
    }

    static boolean httpsSecurityAccepted = false;

    synchronized static void httpsSecurityAcceptAny() {
        if (httpsSecurityAccepted) return;

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            ETrustManager etm = new ETrustManager();
            X509TrustManager[] xtmArray = new X509TrustManager[] {etm};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            httpsSecurityAccepted = true;
        } catch (NoSuchAlgorithmException e) {
            NLog.printStackTrace(e);
        } catch (KeyManagementException e) {
            NLog.printStackTrace(e);
        }
    }

    private static class ETrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        public void checkServerTrusted(X509Certificate[] chain, String authType) {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
