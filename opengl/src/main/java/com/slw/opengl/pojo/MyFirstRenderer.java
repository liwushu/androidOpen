package com.slw.opengl.pojo;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liwushu on 2017/4/16.
 */

public class MyFirstRenderer implements GLSurfaceView.Renderer {

    Square square;

    public MyFirstRenderer(){
        square = new Square();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.5f,0.5f,0.5f,0.5f);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glClearDepthf(1.0f);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_NICEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0,0,width,height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl,45.0f,(float)width/(float)height,0.1f,100f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glColor4f(01f,1f,1f,0.5f);
        gl.glTranslatef(0f,0f,-8);
        square.draw(gl);
        gl.glPushMatrix();
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(0,-2,0);
        gl.glScalef(0.5f,0.5f,0.5f);
        gl.glColor4f(0.5f,0,0.5f,0.5f);
        square.draw(gl);
        gl.glPopMatrix();
        gl.glTranslatef(0,2,0);
        gl.glColor4f(0.5f,0.5f,0f,0.5f);
        gl.glScalef(0.5f,0.5f,0.5f);
        square.draw(gl);

    }
}
