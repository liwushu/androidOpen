package com.flying.test.progress;

/**
 * Created by liwu.shu on 2017/5/11.
 */

import com.flying.test.utils.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;


public class ProgressInterceptor implements Interceptor {

    private ProgressListener progressListener;

    public ProgressInterceptor(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        LogUtils.loge("flying","intercept: ");
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
    }

}