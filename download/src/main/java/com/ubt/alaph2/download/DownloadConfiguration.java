package com.ubt.alaph2.download;

/**
 * @author: liwushu
 * @description:  configuration for download
 * @created: 2017/6/21
 * @version: 1.0
 * @modify: liwushu
*/
public class DownloadConfiguration {

    public static final int DEFAULT_MAX_THREAD_NUMBER = 10;

    public static final int DEFAULT_THREAD_NUMBER = 1;

    /**
     * thread number in the pool
     */
    private int maxThreadNum;

    /**
     * thread number for each download
     */
    private int threadNum;


    /**
     * init with default value
     */
    public DownloadConfiguration() {
        maxThreadNum = DEFAULT_MAX_THREAD_NUMBER;
        threadNum = DEFAULT_THREAD_NUMBER;
    }

    public int getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(int maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
}
