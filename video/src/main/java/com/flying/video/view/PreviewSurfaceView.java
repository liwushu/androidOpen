package com.flying.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by liwushu on 2017/7/25.
 */

public class PreviewSurfaceView  extends SurfaceView implements SurfaceHolder.Callback{
    public PreviewSurfaceView(Context context) {
        this(context,null);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
