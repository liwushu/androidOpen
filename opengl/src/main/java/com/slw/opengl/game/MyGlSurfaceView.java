package com.slw.opengl.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by liwushu on 2017/8/4.
 */

public class MyGlSurfaceView extends GLSurfaceView {
    public MyGlSurfaceView(Context context) {
        this(context,null);
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
    }
}
