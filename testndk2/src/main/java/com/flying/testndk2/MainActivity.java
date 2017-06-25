package com.flying.testndk2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flying.testndk2.jni.TestJni;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvTest;
    private TextView tvCall;
    private TestJni testJni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        initViews();
        bindListener();
    }

    private void initValues(){
        testJni = new TestJni();
    }

    private void initViews(){
        tvCall = (TextView)findViewById(R.id.call);
        tvTest = (TextView)findViewById(R.id.test);
    }

    private void bindListener(){
        tvCall.setOnClickListener(this);
        tvTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.test:
                invokeTest();
                break;
            case R.id.call:
                invokeCall();
                break;
        }
    }

    private void invokeTest(){
        String string = testJni.getJniString();
        Toast.makeText(this,"str: "+string,Toast.LENGTH_SHORT).show();
    }

    private void invokeCall(){
        Object object = testJni.testClassJava();
        String str = object.toString();
        String clx = object.getClass().getName();
        Toast.makeText(this,"value: "+str+"   clx: "+clx,Toast.LENGTH_SHORT).show();
    }
}
