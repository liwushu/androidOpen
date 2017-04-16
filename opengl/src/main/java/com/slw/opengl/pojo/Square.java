package com.slw.opengl.pojo;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liwushu on 2017/4/16.
 */

public class Square {

    private float[] vertexArray = {
            -1,1,0,
            -1,-1,0,
            1,-1,0,
            1,1,0};

    private short[] indicates = {0,1,2,0,2,3};
    private float[] vertexColors={
            1f,0f,0f,1f,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,1f,1f
    };

    private FloatBuffer vertexBuffer;
    private ShortBuffer indicateBuffer;
    private FloatBuffer vertexColorBuffer;


    public Square(){
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length*4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertexArray);
        vertexBuffer.position(0);

        ByteBuffer sbb = ByteBuffer.allocateDirect(indicates.length*2);
        sbb.order(ByteOrder.nativeOrder());
        indicateBuffer = sbb.asShortBuffer();
        indicateBuffer.put(indicates);
        indicateBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(vertexColors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        vertexColorBuffer = cbb.asFloatBuffer();
        vertexColorBuffer.put(vertexColors);
        vertexColorBuffer.position(0);
    }

    public void draw(GL10 gl){
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT,0,vertexBuffer);
        gl.glColorPointer(4,GL10.GL_FLOAT,0,vertexColorBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES,indicates.length,GL10.GL_UNSIGNED_SHORT,indicateBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}
