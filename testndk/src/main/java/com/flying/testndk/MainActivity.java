package com.example.testndk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private TextView tvTest;
    private Button tvCall;
    private Button hookBtn;
    private Button freeBtn;
    private Button btnMalloc1;
    private Button btnFree1;
    private static Handler mUIHandler;
    private static ArrayList<MallocInfo> mallocInfoArrayList = new ArrayList<>();
    public static long mallocSize = 0;
    public static long freeSize = 0;
    private static MainActivity mInstane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstane = this;
        TestAnotherLib.init();
        setContentView(R.layout.activity_main);
        initViews();
        bindListener();
        mUIHandler = new Handler(Looper.myLooper());
    }

    private void initViews(){
        tvTest = (TextView)findViewById(R.id.test);
        tvCall = (Button)findViewById(R.id.call);
        hookBtn = (Button)findViewById(R.id.hook);
        freeBtn = (Button)findViewById(R.id.free);
        btnMalloc1 = (Button)findViewById(R.id.malloc1);
        btnFree1 = (Button) findViewById(R.id.free1);
    }

    private void bindListener(){
        tvTest.setOnClickListener(this);
        tvCall.setOnClickListener(this);
        hookBtn.setOnClickListener(this);
        freeBtn.setOnClickListener(this);
        btnMalloc1.setOnClickListener(this);
        btnFree1.setOnClickListener(this);
    }

    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.test:
                invokeTest1();
                break;
            case R.id.call:
                invokeCall();
                break;
            case R.id.hook:
                invokeHook();
                break;
            case R.id.free:
                invokeFree();
                break;
            case R.id.malloc1:
                callMalloc1();
                break;
            case R.id.free1:
                callFree1();
                break;
        }
    }

    private void invokeHook() {
        hook("libone-lib.so");
    }

    private void invokeFree() {
        free();
    }

    public void invokeTest1(){
        String str = getString();
        Toast.makeText(this,"str: "+ str,Toast.LENGTH_SHORT).show();
    }

    private void invokeCall(){
        Object object = invokeClick(this);
        System.out.println("object: "+object);
        System.out.println("type: "+object.getClass().getName());
    }


    public static void printStack() {
        Log.e(TAG,Log.getStackTraceString(new Throwable()));
    }

    public static void mallocCallback(final int addr, final int size) {
        Log.e(TAG,"===== malloc addr: "+addr);
        MallocInfo mallocInfo = new MallocInfo(addr,size);
        Log.e(TAG,"===== mallocInfo: "+mallocInfo);
        mallocInfoArrayList.add(mallocInfo);
        mallocSize+= size;
        Log.e(TAG,"======mallocCallback mallocSize: "+mallocSize+"   freeSize: "+freeSize);
        printStack();
    }

    public static void freeCallback(final int addr) {
        Iterator<MallocInfo> it = mallocInfoArrayList.iterator();
        Log.e(TAG,"===== free addr: "+addr);
        while(it.hasNext()) {
            MallocInfo mallocInfo = it.next();
            if(mallocInfo.addr == addr) {
                Log.e(TAG,"===== freeSize: "+mallocInfo.size);
                freeSize += mallocInfo.size;
                it.remove();
            }
        }
        Log.e(TAG,"======freeCallback mallocSize: "+mallocSize+"   freeSize: "+freeSize);
        printStack();
    }

    public native String getString();
    public native Object invokeClick(com.example.testndk.MainActivity act);
    public native void hook(String soName);
    public native void free();


    static class MallocInfo {
        int addr;
        int size;

        public MallocInfo(int addr,int size) {
            this.addr = addr;
            this.size = size;
        }

        @Override
        public String toString() {
            return "MallocInfo{" +
                    "addr=" + addr +
                    ", size=" + size +
                    '}';
        }
    }

    private void callMalloc1() {
        TestAnotherLib.callMalloc();
    }

    private void callFree1() {
        TestAnotherLib.callFree();
    }

    public static void init(String soName) {
        mInstane.hook(soName);
        TestAnotherLib.callMalloc();
    }


    static {
        System.loadLibrary("Hello-ndk");
    }
}
