package com.ubt.alaph2.download;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.ubt.alaph2.download.core.DownloadResponseImpl;
import com.ubt.alaph2.download.core.DownloadStatusDeliveryImpl;
import com.ubt.alaph2.download.core.DownloaderImpl;
import com.ubt.alaph2.download.db.DataBaseManager;
import com.ubt.alaph2.download.db.ThreadInfo;
import com.ubt.alaph2.download.impl.IDownloadResponse;
import com.ubt.alaph2.download.impl.IDownloadStatusDelivery;
import com.ubt.alaph2.download.impl.IDownloader;
import com.ubt.alaph2.download.util.LogUtils;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: liwushu
 * @description: public interface for client
 * @created: 2017/6/21
 * @version: 1.0
 * @modify: liwushu
*/
public class DownloadManager implements IDownloader.OnDownloaderDestroyedListener {

    public static final String TAG = DownloadManager.class.getSimpleName();

    /**
     * singleton of DownloadManager
     */
    private static DownloadManager sDownloadManager;

    private DataBaseManager mDBManager;

    private Map<String, IDownloader> mDownloaderMap;

    private DownloadConfiguration mConfig;

    private ExecutorService mExecutorService;

    private IDownloadStatusDelivery mDelivery;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Context mApplicationContext;

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public static DownloadManager getInstance() {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (sDownloadManager == null) {
                    sDownloadManager = new DownloadManager();
                }
            }
        }
        return sDownloadManager;
    }

    /**
     * private construction
     */
    private DownloadManager() {
        mDownloaderMap = new LinkedHashMap<String, Downloader>();
    }

    public void init(Context context) {
        init(context, new DownloadConfiguration());
    }

    public void init(Context context, DownloadConfiguration config) {
        if (config.getThreadNum() > config.getMaxThreadNum()) {
            throw new IllegalArgumentException("thread num must < max thread num");
        }
        mConfig = config;
        mDBManager = DataBaseManager.getInstance(context);
        mExecutorService = Executors.newFixedThreadPool(mConfig.getMaxThreadNum());
        mDelivery = new DownloadStatusDeliveryImpl(mHandler);
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void onDestroyed(final String key, Downloader downloader) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mDownloaderMap.containsKey(key)) {
                    mDownloaderMap.remove(key);
                }
            }
        });
    }

    public void download(DownloadRequest request, String tag, CallBack callBack) {
        final String key = createKey(tag);
        if (check(key)) {
            IDownloadResponse response = new DownloadResponseImpl(mDelivery, callBack);
            IDownloader downloader =
                    new DownloaderImpl(request, response, mExecutorService, mDBManager, key, mConfig, this);
            mDownloaderMap.put(key, downloader);
            downloader.start();
        }
    }

    public void pause(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            IDownloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
            mDownloaderMap.remove(key);
        }
    }

    public void cancel(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            IDownloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                downloader.cancel();
            }
            mDownloaderMap.remove(key);
        }
    }

    public void pauseAll() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IDownloader downloader : mDownloaderMap.values()) {
                    if (downloader != null) {
                        if (downloader.isRunning()) {
                            downloader.pause();
                        }
                    }
                }
            }
        });
    }

    public void cancelAll() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IDownloader downloader : mDownloaderMap.values()) {
                    if (downloader != null) {
                        if (downloader.isRunning()) {
                            downloader.cancel();
                        }
                    }
                }
            }
        });
    }

    public void delete(String tag) {
        String key = createKey(tag);
        mDBManager.delete(key);
    }

    public boolean isRunning(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            IDownloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                return downloader.isRunning();
            }
        }
        return false;
    }

    public DownloadInfo getDownloadInfo(String tag) {
        String key = createKey(tag);
        List<ThreadInfo> threadInfos = mDBManager.getThreadInfos(key);
        DownloadInfo downloadInfo = null;
        if (!threadInfos.isEmpty()) {
            int finished = 0;
            int progress = 0;
            int total = 0;
            for (ThreadInfo info : threadInfos) {
                finished += info.getFinished();
                total += (info.getEnd() - info.getStart());
            }
            progress = (int) ((long) finished * 100 / total);
            downloadInfo = new DownloadInfo();
            downloadInfo.setFinished(finished);
            downloadInfo.setLength(total);
            downloadInfo.setProgress(progress);
        }
        return downloadInfo;
    }

    private boolean check(String key) {
        if (mDownloaderMap.containsKey(key)) {
            IDownloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    LogUtils.w("Task has been started!");
                    return false;
                } else {
                    throw new IllegalStateException("Downloader instance with same tag has not been destroyed!");
                }
            }
        }
        return true;
    }

    private static String createKey(String tag) {
        if (tag == null) {
            throw new NullPointerException("Tag can't be null!");
        }
        return String.valueOf(tag.hashCode());
    }


    public boolean containsWifiDownload() {
        for (IDownloader downloader : mDownloaderMap.values()) {
            if (downloader != null) {
                if (downloader.isOnlyWifi()) {
                    if (downloader.isRunning()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Map<String, IDownloader> getDownloaderMap() {
        return mDownloaderMap;
    }

    public static boolean isWifiConnect() {
        ConnectivityManager manager = (ConnectivityManager) DownloadManager.getInstance().getApplicationContext()
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isConnected()) {
            return true;
        }
        return false;
    }


}
