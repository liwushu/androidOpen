package com.flying.camera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.flying.camera.holder.CameraHolder;
import com.flying.camera.holder.CameraManager;

public class MainActivity extends AppCompatActivity {

    CameraHolder mCameraHolder;

    SurfaceView mSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        initViews();
        initListener();
    }

    private void initValues(){
        CameraManager.checkCamera();
        mCameraHolder = new CameraHolder();
    }

    private void initViews() {
        mSurfaceView = (SurfaceView)findViewById(R.id.camera_preview);
        mSurfaceView.getHolder().addCallback(mCameraHolder);
    }

    private void initListener() {

    }
}
