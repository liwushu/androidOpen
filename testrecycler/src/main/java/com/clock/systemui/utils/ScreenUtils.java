package com.clock.systemui.utils;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import java.lang.reflect.Field;

public class ScreenUtils {
    private static final String TAG = "WXTBUtil";

    public static int getDisplayWidth(Activity activity){
        int width=0;
        if (activity != null && activity.getWindowManager() != null && activity.getWindowManager().getDefaultDisplay() != null) {
            Point point=new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(point);
            width = point.x;
        }
        return width;
    }

    public static int getDisplayHeight(Activity activity) {
        int height = 0;
        if (activity != null && activity.getWindowManager() != null && activity.getWindowManager().getDefaultDisplay() != null) {
            Point point=new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(point);
            height=point.y;
        }

        int status = getStatusBarHeight(activity);
        Log.d(TAG, "status:" + status);
        height -= status;

        Log.d(TAG,"height:"+height);
        return height;
    }

    private static int getStatusBarHeight(Activity activity) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    public static boolean hideNavigationBar(Activity activity, boolean hasFocus) {
        return hideNavigationBar(activity, hasFocus, true);
    }

    /**
     * 隐藏导航栏（底部虚拟按键）decorView层
     * @param activity 要隐藏的页面
     * @param hasFocus 在该页面的onWindowFocusChanged回调方法调用此方法
     * @param hideStatusBar 是否隐藏状态栏
     * @return 是否成功隐藏
     */
    public static boolean hideNavigationBar(Activity activity, boolean hasFocus, boolean hideStatusBar) {
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView == null) {
                return false;
            }
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            if (hideStatusBar) {
                flag |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            decorView.setSystemUiVisibility(flag);

            return true;
        }

        return false;
    }

    public static boolean hideWindowNavigationBar(Activity activity) {
        return hideWindowNavigationBar(activity, true);
    }

    /**
     * 隐藏导航栏（底部虚拟按键）window层
     * @param activity 要隐藏的页面
     * @param hideStatusBar 是否隐藏状态栏
     * @return 是否成功隐藏
     */
    public static boolean hideWindowNavigationBar(Activity activity, boolean hideStatusBar) {
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            if (hideStatusBar) {
                flag |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            params.systemUiVisibility = flag;
            window.setAttributes(params);

            return true;
        }

        return false;
    }

    public static void hideNavigtionStep1(Activity activity) {
        hideNavigtionStep1(activity, true);
    }

    public static void hideNavigtionStep1(Activity activity, boolean hideStatusBar) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        int flag =0;
        if (hideStatusBar) {
           flag = View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }else {
            flag =0;
        }

        params.systemUiVisibility = flag;
        window.setAttributes(params);
    }

    public static void hideNavigtionStep2(Activity activity, boolean hasFocus) {
        int flag = 0;
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    |View.SYSTEM_UI_FLAG_IMMERSIVE;
            flag |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(flag);
        }else {
            flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(flag);
        }
    }

    public static void clearWindowFlag(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = 0;
        window.setAttributes(params);
    }

    public static void clearViewFlag(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(0);
        }
    }
}
