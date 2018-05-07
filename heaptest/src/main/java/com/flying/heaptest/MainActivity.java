package com.flying.heaptest;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flying.heaptest.db.TestDb1;
import com.flying.heaptest.utils.InitUtils;
import com.flying.heaptest.utils.XLogImpl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int SIZE = 1024*1024;
    TextView tvService1;
    TextView tvService2;
    TextView tvSecond;
    Handler mUiHandler = new Handler(Looper.getMainLooper());
    TextView tvOpenDb;
    TextView tvBugly;
    Button tvXlog;
    XLogImpl mXLogImpl;
    ArrayList<ByteBuffer> byteBufferArrayList = new ArrayList<>();

    int clickCount = 0;
    ArrayList<SQLiteDatabase> sqLiteDatabaseArrayList = new ArrayList<>(12);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        bindListener();
    }

    private void initViews() {
        tvService1 = (TextView) findViewById(R.id.service1);
        tvService2 = (TextView) findViewById(R.id.service2);
        tvSecond = (TextView) findViewById(R.id.second_2);
        tvOpenDb = (TextView) findViewById(R.id.open_db);
        tvBugly = (TextView) findViewById(R.id.init_bugly);
        tvXlog = (Button) findViewById(R.id.init_xlog);
    }

    private void bindListener() {
        tvService1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Service1.class);
                startService(intent);
            }
        });

        tvService2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Service2.class);
                startService(intent);
            }
        });

        tvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        tvOpenDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                String dbName = "test"+clickCount;
                Log.e(TAG, "onClick_dbName: "+dbName);
                TestDb1 testDb1 = new TestDb1(MainActivity.this,dbName);
                SQLiteDatabase sqLiteDatabase = testDb1.getWritableDatabase();
                sqLiteDatabaseArrayList.add(sqLiteDatabase);
                testDb1.insert(sqLiteDatabase,10);
                testDb1.query(sqLiteDatabase);
            }
        });

        tvBugly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"bugly",Toast.LENGTH_SHORT).show();
                InitUtils.initBugly(getApplicationContext());
            }
        });

        tvXlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InitUtils.initXlog();
                //allocationTest();
                //print();
                try {
                    int a = 1/0;
                    throw new RuntimeException();
                }catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Xlog",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                Log.e(TAG,"InitUtils.initXlog()");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity","onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume() called");
        tvSecond.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"onResume post");
            }
        });
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"onResume post 111111");
            }
        });
    }

    private void print() {
        Looper.getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {
                InitUtils.log("TAG","x: "+x);
            }
        });
    }

    private void allocationTest() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(SIZE).order(ByteOrder.nativeOrder());
        //ByteBuffer byteBuffer1 = ByteBuffer.allocate(SIZE);
        //android.util.Log.e(TAG,"byteBuffer: "+byteBuffer+"\nbyteBuffer1: "+byteBuffer1);
        if(byteBuffer != null) {
            byteBufferArrayList.add(byteBuffer);
            for(int i=0;i<SIZE;i++) {
                byteBuffer.put((byte) 1);
            }
        }
//        if(byteBuffer1 != null) {
//            byteBufferArrayList.add(byteBuffer1);
//            for(int i=0;i<SIZE;i++) {
//              byteBuffer1.put((byte) 1);
//            }
//        }
    }

}
