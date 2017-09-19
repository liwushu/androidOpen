package com.flying.camera.holder;


import android.hardware.Camera;

import com.flying.camera.utils.LogUtils;

import static android.hardware.Camera.open;

/**
 * Created by liwushu on 2017/8/13.
 */

public class CameraManager {

    public static Camera getCamera() {
        Camera camera;

        try {
            camera = open(0);
        } catch (Exception e) {
            LogUtils.e("openCamera");
            e.printStackTrace();
            return null;
        }
        return camera;
    }


    public static boolean checkCamera() {
        int count = Camera.getNumberOfCameras();
        LogUtils.e("count: "+count);
        return count > 0;
    }

    public static void realease(Camera camera) {
        if (camera != null) {
            try {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
