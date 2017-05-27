package com.slw.opengl.pojo;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.slw.opengl.utils.LogUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liwu.shu on 2017/4/14.
 */

public class MyOpenglRenderer implements GLSurfaceView.Renderer {

    Square square;

    public MyOpenglRenderer(){
        square = new Square();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.loge("onSurfaceCreate");
        gl.glClearColor(0.5f,0.5f,0.5f,1);
        gl.glShadeModel(GL10.GL_SMOOTH);// OpenGL docs.
        // Depth buffer setup.
        gl.glClearDepthf(1.0f);// OpenGL docs.
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.
        // The type of depth testing to do.
        gl.glDepthFunc(GL10.GL_LEQUAL);// OpenGL docs.
        // Really nice perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, // OpenGL docs.
                GL10.GL_NICEST);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.loge("onSurfaceChanged");
        gl.glViewport(0,0,width,height);
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
        // Reset the projection matrix
        gl.glLoadIdentity();// OpenGL docs.
        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 45.0f,
                (float) width / (float) height,
                0.1f, 100.0f);
        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
        // Reset the modelview matrix
        gl.glLoadIdentity();// OpenGL docs.
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.loge("onDrawFrame");
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -8);
        gl.glPushMatrix();
        square.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(0,2,0);
        gl.glScalef(0.5f,0.5f,0.5f);
        gl.glRotatef(45,0,0,2);
        square.draw(gl);
        gl.glPopMatrix();
        gl.glTranslatef(0,-2,0);
        gl.glScalef(0.5f,0.5f,0.5f);
        gl.glRotatef(45,0,2,0);
        square.draw(gl);
    }
}
