package com.clock.systemui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.zip.Inflater;


public class MainActivity_new extends FragmentActivity {

    private Button mButton;
    ShapedImageView shapedImageView;
    private Button fullButton;
    private Button noFullButton;
    private View mainView;
    private LinearLayout layout_main;
    private int keyboardHeight;
    private int softButtonsBarHeight;
    private int statusBarHeight;
    private TextView text;
    private boolean isShowKeyboard;
    private EditText mEditText;
    private ImageView play;
    private int editScreenY;
    int screenHeight;
    int rootBottom;
    View scrollView;
    Button show;
    Button hide;
    Button trans;
    Button full;
    Button noFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        statusBarHeight = getStatusBarHeight(getApplicationContext());
        softButtonsBarHeight = getSoftButtonsBarHeight(this);
        mainView = findViewById(R.id.main_view);
        initViews();
        mButton.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

    }

    private void initViews() {
        play = (ImageView) findViewById(R.id.play);
        mButton = (Button) findViewById(R.id.start);
        shapedImageView = (ShapedImageView) findViewById(R.id.image1);
        text = (TextView) findViewById(R.id.text);
        show = (Button) findViewById(R.id.show);
        hide = (Button) findViewById(R.id.hide);
        trans = (Button) findViewById(R.id.trans);
        full = (Button) findViewById(R.id.full);
        noFull = (Button) findViewById(R.id.nofull);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //contentPop.showPopWindow(mainView);
//                Intent intent = new Intent();
//                intent.setClass(MainActivity_new.this,MainActivityTest.class);
//                startActivity(intent);
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.showSoftInput(mButton, 0);
//                }

//                //mButton.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        showKey(mEditText);
//                    }
//                });
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(play, 0);
                }
            }
        });

        mEditText = (EditText) findViewById(R.id.edit);
        Log.e("Tag","mButton: "+mButton+"  mEditText: "+mEditText);
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                int[] position = new int[2];
                mEditText.getLocationInWindow(position);
                editScreenY = position[1];
            }
        });
        scrollView = findViewById(R.id.scroll_View);
        play = (ImageView) findViewById(R.id.image1);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                transparencyBar(MainActivity_new.this,Color.BLACK);
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mainView.setSystemUiVisibility();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                transparencyBar(MainActivity_new.this,Color.WHITE);
            }
        });

        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transparencyBar(MainActivity_new.this,Color.TRANSPARENT);
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });

        noFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            // 应用可以显示的区域。此处包括应用占用的区域，
            // 以及ActionBar和状态栏，可能会包含设备底部的虚拟按键。

            Rect r = new Rect();
            mainView.getWindowVisibleDisplayFrame(r);
            Log.e("Tag","onGlobalLayout_r: "+r.toString());

            getAttachInfo(mainView);
            // 屏幕高度
            screenHeight = mainView.getRootView().getHeight();
            Log.e("Tag","screenHeight: "+screenHeight);
            int heightDiff = screenHeight - (r.bottom - r.top);
            Log.e("tag","heightDiff: "+heightDiff);
            // 在不显示软键盘时，heightDiff等于 状态栏 + 虚拟按键 的高度
            // 在显示软键盘时，heightDiff会变大，等于 软键盘 + 状态栏 + 虚拟按键 的高度。
            // 所以heightDiff大于 状态栏 + 虚拟按键 高度时表示软键盘出现了，
            // 这时可算出软键盘的高度，即heightDiff减去 状态栏 + 虚拟按键 的高度
            if (keyboardHeight == 0 && heightDiff > statusBarHeight + softButtonsBarHeight) {
                keyboardHeight = heightDiff - statusBarHeight - softButtonsBarHeight;
            }

            if (isShowKeyboard) {
                // 如果软键盘是弹出的状态，并且heightDiff小于等于 状态栏 + 虚拟按键 高度，
                // 说明这时软键盘已经收起
                if (heightDiff <= statusBarHeight + softButtonsBarHeight) {
                    isShowKeyboard = false;
                    onHideKeyboard();
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                }
            } else {
                // 如果软键盘是收起的状态，并且heightDiff大于 状态栏 + 虚拟按键 高度，
                // 说明这时软键盘已经弹出
                if (heightDiff > statusBarHeight + softButtonsBarHeight) {
                    isShowKeyboard = true;
                    onShowKeyboard();
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                }
            }
        }
    };


    private void onShowKeyboard() {
        // 在这里处理软键盘弹出的回调
        //text.setText("onShowKeyboard : keyboardHeight = " + keyboardHeight);
        int deltaY = editScreenY+mEditText.getHeight()+keyboardHeight+softButtonsBarHeight-screenHeight+statusBarHeight;
        Log.e("Tag","editScreenY: "+editScreenY+" keyboardHeight: "+keyboardHeight+"  soft: "+softButtonsBarHeight+"  screenHeight: "+screenHeight+" statusBar: "+statusBarHeight);
        Log.e("tag","deltaY: "+deltaY+"  height: "+play.getHeight()+"  scrollView: "+scrollView.getHeight());
        //mainView.scrollTo(mainView.getScrollX(),-deltaY);
        //scrollView.scrollTo(scrollView.getScrollX(),deltaY);
        mainView.requestLayout();

    }

    private void onHideKeyboard() {
        Log.e("tag","deltaY:   height: "+play.getHeight()+"  scrollView: "+scrollView.getHeight());
        // 在这里处理软键盘收回的回调
        //text.setText("onHideKeyboard");
        //scrollView.scrollTo(scrollView.getScrollX(),0);
        //mainView.requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            layout_main.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
        } else {
            layout_main.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        }
    }


    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取底部虚拟按键的高度
     */
    public static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    /**
     * 修改状态栏为全透明
     * @param activity
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity,int color){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);

        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void getAttachInfo(View view) {
        Class<?> viewClz = view.getClass();
        Class<?> viewParent = viewClz.getSuperclass();
        while(viewClz.getSuperclass()!= null) {
            viewParent = viewClz;
            viewClz = viewClz.getSuperclass();
            android.util.Log.e("getAttachInfo","viewClz: "+viewClz.getSimpleName());
        }
        try {
            Field attachField = viewParent.getDeclaredField("mAttachInfo");
            if(attachField != null) {
                attachField.setAccessible(true);
                Object object = attachField.get(view);
                android.util.Log.e("getAttachInfo","object: "+object);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showKey(EditText editText) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


}
