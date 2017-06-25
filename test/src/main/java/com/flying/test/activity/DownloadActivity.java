package com.flying.test.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flying.test.R;
import com.ubt.alaph2.download.CallBack;
import com.ubt.alaph2.download.DownloadException;
import com.ubt.alaph2.download.DownloadManager;
import com.ubt.alaph2.download.DownloadRequest;
import com.ubt.alaph2.download.util.LogUtils;

import java.io.File;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TEST_URL ="http://gh-game.oss-cn-hangzhou.aliyuncs.com/1435814701749842.apk";
    DownloadManager downloadManager;
    TextView tvDownload;
    File fileTarget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initValues();
        initViews();
    }

    private void initValues(){
        downloadManager = DownloadManager.getInstance();
        downloadManager.init(this.getApplicationContext());
        fileTarget = Environment.getExternalStorageDirectory();
    }

    private void initViews(){
        tvDownload = (TextView)findViewById(R.id.download);
        tvDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.download:
                invokeDownload(TEST_URL);
                break;
        }
    }

    private void invokeDownload(String url){
        DownloadRequest.Builder builder = new DownloadRequest.Builder();
        builder.setUri(url)
                .setDescription("test download")
                .setFolder(fileTarget);
        DownloadRequest downloadRequest = builder.build();

        downloadManager.download(downloadRequest, url, new CallBack() {
            @Override
            public void onStarted() {
                LogUtils.e("flying","onStared");
            }

            @Override
            public void onConnecting() {
                LogUtils.e("flying","onConnecting");
            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {
                LogUtils.e("flying","onConnected: total: "+total);
            }

            @Override
            public void onProgress(long finished, long total, int progress) {
                LogUtils.e("flying","onProgress: total: "+total+"  finished: "+finished+"   progress: "+progress);
            }

            @Override
            public void onCompleted() {
                LogUtils.e("flying","onCompleted");
            }

            @Override
            public void onDownloadPaused() {
                LogUtils.e("flying","onDownloadPaused");
            }

            @Override
            public void onDownloadPaused() {
                LogUtils.e("flying","onDownloadPaused");
            }

            @Override
            public void onFailed(DownloadException e) {
                LogUtils.e("flying","onFailed");
            }

            @Override
            public void onDownloadRetry() {
                LogUtils.e("flying","onDownloadRetry");
            }
        });
    }
}
