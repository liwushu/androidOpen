package com.flying.test.progress;

import com.flying.test.utils.LogUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by liwu.shu on 2017/5/11.
 */

public class ProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private ProgressListener progressListener;
    private BufferedSource bufferedSource;
    long bytesRead;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        LogUtils.logd("flying","contentType: "+responseBody.contentType());
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            LogUtils.logd("flying","contentLength: "+responseBody.contentLength());
            return responseBody.contentLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            try {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {

            long totalBytesRead = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {

                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                LogUtils.logd("flying","totalBytesRead: "+totalBytesRead);
                if(progressListener != null)
                    progressListener.progress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bytesRead = super.read(sink, byteCount);
                return bytesRead;
            }
        };
    }
}