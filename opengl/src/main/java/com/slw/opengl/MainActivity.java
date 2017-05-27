package com.slw.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.slw.opengl.pojo.MyOpenglRenderer;

import com.slw.opengl.pojo.MyFirstRenderer;
import com.slw.opengl.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView glSurfaceView;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!checkGLSurfaceViewSupport()){
            ToastUtils.show(R.string.can_not_support_opengl);
            return;
        }
        initOpenGl();
        setContentView(glSurfaceView);
    }

    private void initOpenGl(){
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(new MyFirstRenderer());

        setContentView(glSurfaceView);

    }



    private boolean checkGLSurfaceViewSupport(){
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        if(0x20000 <= configurationInfo.reqGlEsVersion){

            return true;
        }
        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(glSurfaceView != null)
            glSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (glSurfaceView != null)
            glSurfaceView.onPause();

    }

    private void showMessage(int resId){
        showMessage(getString(resId));
    }

    private void showMessage(String msg){
        if(mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        mToast.show();
    }

}
