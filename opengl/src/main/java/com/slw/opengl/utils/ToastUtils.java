package com.slw.opengl.utils;

import android.widget.Toast;

import com.slw.opengl.OpenglApplication;

/**
 * Created by liwushu on 2017/4/16.
 */

public class ToastUtils {

    private static Toast mToast;

    public static void show(int resId){
        show(OpenglApplication.getContext().getString(resId));
    }

    public static  void show(String msg){
        if(mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(OpenglApplication.getContext(),msg,Toast.LENGTH_SHORT);
        mToast.show();
    }
}
