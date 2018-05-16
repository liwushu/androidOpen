package com.example.camera;

import android.app.Application;
import android.content.Context;

/**
 * Created by liwushu on 2017/8/13.
 */

public class CameraApplication  extends Application{

    private  static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
