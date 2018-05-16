package com.example.camera;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.example.camera.holder.CameraHolder;
import com.example.camera.holder.CameraManager;
import com.example.camera.utils.LaunchUtil;
import com.example.camera.utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    CameraHolder mCameraHolder;

    SurfaceView mSurfaceView;
    Handler mUiHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LaunchUtil.init();
        mUiHandler = new Handler();
        LaunchUtil.logTime("onCreate");
        setContentView(R.layout.activity_main);
        initValues();
        initViews();
        initListener();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mCameraHolder.openCameraParam();
            }
        });
    }

    private void initValues() {
        CameraManager.checkCamera();
        mCameraHolder = new CameraHolder();
    }

    private void initViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        mSurfaceView.getHolder().addCallback(mCameraHolder);
    }

    private void initListener() {

    }

    @Override
    public void onResume() {
        LaunchUtil.logTime("onResume");
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                LaunchUtil.logTime("onResume post 111111");
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LaunchUtil.logTime("onResume post 222222");
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    LaunchUtil.logTime("onResume post 333333");
                                    Thread.sleep(10*1000);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
        super.onResume();
    }
}

