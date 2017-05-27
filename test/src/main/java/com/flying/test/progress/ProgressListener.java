package com.flying.test.progress;

/**
 * Created by liwu.shu on 2017/5/11.
 */

public interface ProgressListener {
    void progress(long bytesRead, long contentLength, boolean done);
}