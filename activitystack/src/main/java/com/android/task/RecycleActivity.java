package com.android.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 *
 * @author shuliwu
 * @date 2018/2/24
 */

public class RecycleActivity extends AppCompatActivity {
    private String TAG = "RecycleActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        Log.w(TAG,"onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(TAG,"onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG,"onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG,"onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG,"onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(TAG,"onNewIntent");
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        Log.w(TAG,"onActivityResult");
    }
}
