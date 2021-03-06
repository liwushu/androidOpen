package com.slw.opengl.game;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.slw.opengl.R;
import com.slw.opengl.pojo.ShadeHelper;
import com.slw.opengl.pojo.TextResourceReader;
import com.slw.opengl.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by ubt on 2017/8/3.
 */

public class AirHockeyRender implements GLSurfaceView.Renderer {
    private static final int FLOAT_BYTE_SIZE = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final String U_COLOR = "u_color";
    private static final String A_POSITION = "a_Position";
    public int uColorLocation;
    private int aPositionLocation;

    private FloatBuffer vertexData;
    private String vertexShaderCode;
    private String fragmentShaderCode;
    Context mContext;
    private int program;

    float[] tableVerteces = {
            0f,0f,
            0f,14f,
            9f,14f,
            9f,0f,
    };

    float[] tableVertexWithTriangles = {
        //triangle
            0f,0f,
            9f,14f,
            0f,14f,
        //triangle
            0f,0f,
            9f,0f,
            9f,14f
    };

    public AirHockeyRender(Context context){
        LogUtils.logd("AirHockeyRender");
        this.mContext = context;
        vertexShaderCode = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        fragmentShaderCode = TextResourceReader.readTextFileFromResource(context,R.raw.simple_fragment_shader);
        initVertexBuffer();
    }

    private void initVertexBuffer(){
        LogUtils.logd("initVertexBuffer");
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(tableVertexWithTriangles.length*FLOAT_BYTE_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexData = byteBuffer.asFloatBuffer();
        vertexData.put(tableVertexWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.logd("onSurfaceCreated");
        int vertexShaderId = ShadeHelper.compileVertexShader(vertexShaderCode);
        int fragmentShaderId = ShadeHelper.compileFragmentShader(fragmentShaderCode);
        LogUtils.logd("vertexShaderId: "+vertexShaderId+"  fragmentShaderId: "+fragmentShaderId);
        if(vertexShaderId != 0 && fragmentShaderId != 0){
            program =ShadeHelper.linkProgram(vertexShaderId,fragmentShaderId);

        }
        LogUtils.logd("program: "+program);
        if(program != 0){
            ShadeHelper.validateProgram(program);
            glUseProgram(program);
        }
        uColorLocation = glGetUniformLocation(program,U_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        LogUtils.logd("uColorLocation: "+uColorLocation+"  aPositionLocation: "+aPositionLocation);
        glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GL_FALSE,false,0,vertexData);
        glEnableVertexAttribArray(aPositionLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.logd("onSurfaceChanged");
        glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //glClear(GLES20.GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation,1.0f,1.0f,1.0f,1.0f);
        glDrawArrays(GL_TRIANGLES,0,6);
    }
}
