package com.android.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author shuliwu
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    String str = "faceu://main/share?platform=weibo&share_type=2&share_url=http://static-test.faceu.net/women-day/resultPage?resultImg=https://static-u2.faceu.mobi/over_hot_nl/20180301/f8c0ad5b43e6acab93ddb74dab3974c2ae93c8ad-890f-492d-8be3-85aff539444b1519828931787&from=weibo&channel=default&ref=H5_women_day&share_title=#活出你的女子力#我的女子力原型是undefined,快来扫脸测测你的吧~";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate: "+Log.getStackTraceString(new Throwable()));
    }


    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart: "+Log.getStackTraceString(new Throwable()));
    }

    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume: "+Log.getStackTraceString(new Throwable()));
    }

    protected void init(){
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("I am "+this.getClass().getSimpleName());
        initTextView();
        if(TextUtils.isEmpty(str))
            return;

        String parameters = str.substring(str.indexOf("?"));
        String[] values1 = parameters.split("&");
        for(String value: values1) {
            String[] paras = value.split("=");
            paras[0] = "key";
            paras[1] = "value";
        }
    }

    private void initTextView() {
        TextView textView = new TextView(this);
        FrameLayout frameLayout = (FrameLayout) getWindow().getDecorView();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        textView.setText("test");
        //textView.bringToFront();
        frameLayout.addView(textView,layoutParams);
    }
}
