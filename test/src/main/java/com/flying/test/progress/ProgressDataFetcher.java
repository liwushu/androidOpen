package com.flying.test.progress;

/**
 * Created by liwu.shu on 2017/5/11.
 */

import android.os.Handler;
import android.os.Message;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.flying.test.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chenpengfei on 2016/11/9.
 */
public class ProgressDataFetcher implements DataFetcher<InputStream> {

    private String url;
    private Handler handler;
    private Call progressCall;
    private InputStream stream;
    private boolean isCancelled;

    public ProgressDataFetcher(String url, Handler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        LogUtils.logd("flying","loadData: "+url);
        Request request = new Request.Builder().url(url).build();
        LogUtils.logd("flying","request: "+request);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add( new ProgressInterceptor(getProgressListener()));
        OkHttpClient client = builder.build();

        LogUtils.logd("flying","client: "+client);

        List<Interceptor> interceptorList = client.interceptors();
        LogUtils.logd("flying","interceptorList: "+interceptorList);

        try {
            LogUtils.logd("flying","client.interceptors()222: "+client.interceptors());
            progressCall = client.newCall(request);
            LogUtils.logd("flying","progressCall: "+progressCall);
            Response response = progressCall.execute();
            if (isCancelled) {
                return null;
            }
            LogUtils.logd("flying","response: "+response);
            LogUtils.logd("flying","response.isSuccessful: "+response.isSuccessful());
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            stream = response.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        LogUtils.logd("flying","stream: "+stream);
        return stream;
    }

    private ProgressListener getProgressListener() {
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void progress(long bytesRead, long contentLength, boolean done) {
                if (handler != null && !done) {
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.arg1 = (int)bytesRead;
                    message.arg2 = (int)contentLength;
                    handler.sendMessage(message);
                }
            }
        };
        return progressListener;
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
                stream = null;
            }
        }
        if (progressCall != null) {
            progressCall.cancel();
        }
    }

    @Override
    public String getId() {
        return url;
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }
}