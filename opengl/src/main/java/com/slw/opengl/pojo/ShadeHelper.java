package com.slw.opengl.pojo;

import com.slw.opengl.utils.LogUtils;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by ubt on 2017/8/2.
 */

public class ShadeHelper {

    public static int compileVertexShader(String shaderCode){
        return compileShade(GL_VERTEX_SHADER,shaderCode);
    }

    public static int compileFragmentShader(String shaderCode){
        return compileShade(GL_FRAGMENT_SHADER,shaderCode);
    }

    private static int compileShade(int type,String shadeCode){
        final int shaderObjectId = glCreateShader(type);
        if(shaderObjectId == 0){
            LogUtils.logd("shaderObjectId == 0");
            return 0;
        }
        glShaderSource(shaderObjectId,shadeCode);
        glCompileShader(shaderObjectId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId,GL_COMPILE_STATUS,compileStatus,0);
        LogUtils.logd("Results of compiling source: shadeCode: "+glGetShaderInfoLog(shaderObjectId));
        if(compileStatus[0] == 0){
            glDeleteShader(shaderObjectId);
            LogUtils.logd(" compile shader failed");
            return 0;
        }

        return shaderObjectId;
    }

    private static int getShaderObjectId(int type){
        final int shaderObjectId = glCreateShader(type);
        if(shaderObjectId != 0)
            return shaderObjectId;
        return 0;
    }

    public static int linkProgram(int vertexShaderId,int fragmentShaderId){
        final int programId = glCreateProgram();
        if(programId == 0){
            LogUtils.logd("could not create program");
            return 0;
        }

        glAttachShader(programId,vertexShaderId);
        glAttachShader(programId,fragmentShaderId);
        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId,GL_LINK_STATUS,linkStatus,0);
        if(linkStatus[0] == 0){
            glDeleteProgram(programId);
            LogUtils.logd("link program failed");
            return 0;
        }

        return programId;
    }

    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId,GL_VALIDATE_STATUS,validateStatus,0);
        LogUtils.logd("Result of validating program :"+validateStatus[0]+"  log: "+glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

}
