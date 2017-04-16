package com.slw.opengl;

import android.app.Application;
import android.content.Context;

/**
 * Created by liwushu on 2017/4/16.
 */

public class OpenglApplication extends Application{

    private static Context mc;

    @Override
    public void onCreate(){
        super.onCreate();
        initContext();
    }

    private void initContext(){
        mc = OpenglApplication.this;
    }

    public static Context getContext(){
        return mc;
    }
}
