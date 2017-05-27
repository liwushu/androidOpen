package com.slw.opengl.pojo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liwu.shu on 2017/4/14.
 */

public class Square {
    private float[] vertexArray = new float[]{
            -1.0f,1.0f,0.0f,
            -1.0f,-1.0f,0.0f,
            1.0f,-1.0f,0.0f,
            1.0f,1.0f,0.0f
    };

    private short[] incadies = new short[]{
            0,1,2,0,2,3
    };

    FloatBuffer floatBuffer ;
    ShortBuffer shortBuffer;

    public Square(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexArray.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(vertexArray);
        floatBuffer.position(0);

        ByteBuffer sbb = ByteBuffer.allocateDirect(incadies.length *2);
        sbb.order(ByteOrder.nativeOrder());
        shortBuffer = sbb.asShortBuffer();
        shortBuffer.put(incadies);
        shortBuffer.position(0);
    }

    public void draw(GL10 gl){
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,floatBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES,incadies.length,GL10.GL_UNSIGNED_SHORT,shortBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
    }
}
