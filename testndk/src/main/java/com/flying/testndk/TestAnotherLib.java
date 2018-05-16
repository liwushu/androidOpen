package com.example.testndk;

public class TestAnotherLib {


    static {
        System.loadLibrary("one-lib");
        MainActivity.init("libone-lib.so");
    }

    public static void init() {

    }

    public static native void callMalloc();

    public static native void callFree();
}
