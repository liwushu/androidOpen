package com.flying.camera.holder;


import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.flying.camera.utils.LaunchUtil;
import com.flying.camera.utils.LogUtils;

/**
 * Created by liwushu on 2017/8/13.
 */

public class CameraHolder implements SurfaceHolder.Callback {

    Camera mCamera;
    boolean isFirstFrame = true;

    public CameraHolder(){
        SensorController sensorController = SensorController.getInstance();
        sensorController.setCameraFocusListener(new SensorController.CameraFocusListener() {
            @Override
            public void onFocus() {
//                initCameraParam();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera = CameraManager.getCamera();
            mCamera.setPreviewDisplay(surfaceHolder); //camera关联到SurfaceView
            mCamera.setDisplayOrientation(90); //旋转90度
            //initCameraParam();

        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //自动聚焦
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    LogUtils.e("auto focus success");
                    openCameraParam();
                }
                else {
                    LogUtils.e("auto focus failed");
                }

            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        CameraManager.realease(mCamera);
        mCamera = null;
    }


    public void openCameraParam() {
        if (mCamera == null) {
            return;
        }
        LaunchUtil.logTime("openCameraParam");
        Camera.Parameters params = mCamera.getParameters();
        params.setPictureFormat(ImageFormat.JPEG);
        //params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        mCamera.setParameters(params);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                if(isFirstFrame) {
                    LaunchUtil.logTime("onPreviewFrame");
                    isFirstFrame = false;
                }
            }
        });
    }
}
