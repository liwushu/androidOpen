package com.flying.testndk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener{

    private TextView tvTest;
    private Button tvCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        bindListener();
    }

    private void initViews(){
        tvTest = (TextView)findViewById(R.id.test);
        tvCall = (Button)findViewById(R.id.call);
    }

    private void bindListener(){
        tvTest.setOnClickListener(this);
        tvCall.setOnClickListener(this);
    }

    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.test:
                invokeTest1();
                break;
            case R.id.call:
                invokeCall();
        }
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

    public void invokeTest(){
        //String str = TestJni.getString();
        System.out.println("MainActivity_call from jni ");
    }

    public static native String getString();
    public static native Object invokeClick(com.flying.testndk.MainActivity act);


    static {
        System.loadLibrary("mytest");
    }
}
