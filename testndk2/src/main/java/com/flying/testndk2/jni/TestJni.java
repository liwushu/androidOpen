package com.flying.testndk2.jni;

/**
 * Created by liwushu on 2017/6/12.
 */

public class TestJni {

    public static native String getJniString();
    public static native Object testClassJava();

    static{
        System.loadLibrary("mytest");
    }
}
