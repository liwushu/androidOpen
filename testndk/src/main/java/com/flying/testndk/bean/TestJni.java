package com.flying.testndk.bean;

/**
 * Created by liwushu on 2017/6/9.
 */

public class TestJni {

    public static native String getString();
    public static native void invokeClick();

    public void invokeTest(){
        //String str = TestJni.getString();
        System.out.println("call from jni ");
    }

    static {
        System.loadLibrary("mytest");
    }
}
