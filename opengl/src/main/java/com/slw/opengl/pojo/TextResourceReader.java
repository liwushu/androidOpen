package com.slw.opengl.pojo;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ubt on 2017/8/2.
 */

public class TextResourceReader {

    public static String readTextFileFromResource(Context context,int resId){
        InputStream inputStream = context.getResources().openRawResource(resId);
        if(inputStream != null){
            StringBuilder builder = new StringBuilder();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readLine = null;
            try {
                while((readLine = bufferedReader.readLine()) != null){
                    builder.append(readLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }
        return null;
    }
}
