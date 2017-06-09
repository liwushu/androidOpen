package com.flying.testndk.bean;

/**
 * Created by liwushu on 2017/6/9.
 */

public class TestJni {

    public native String getString();

    static {
        System.loadLibrary("mytest");
    }
}
