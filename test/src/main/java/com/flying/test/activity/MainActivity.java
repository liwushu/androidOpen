package com.flying.test.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.flying.test.R;
import com.flying.test.TestDownloadImage;
import com.tcl.joylockscreen.IJoyLockScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainActivity extends Activity {
    //先定义 常量
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    IJoyLockScreen joyLockScreen;
    ServiceConnection serviceConnection;
    int id =0;

    @IntDef({SUNDAY, MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WeekDays {}

    @WeekDays int days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        //initService();
    }

    private void initViews(){
        TextView textView = (TextView)findViewById(R.id.click);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("flying", "joyLockScreen: " + joyLockScreen);
                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                showMemory(activityManager);
                checkWritePermission();
                File dir = new File("/sdcard/test/");
                if (!dir.exists())
                    dir.mkdirs();
                File file = new File(dir, "test.txt");
                Log.e("flying", "file.exists: " + file.exists());
                Log.e("flying", "file: " + file.getAbsolutePath());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[10];
                    int len = fileInputStream.read(buffer);
                    Log.e("flying","len: "+len);
                } catch (FileNotFoundException e) {
                    Log.e("flying","FileNotFoundException");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("flying","IOException");
                    e.printStackTrace();
                }
            }
        });

        TextView tvSwitch = (TextView)findViewById(R.id.tv_switch);
        tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TestDownloadImage.class);
                startActivity(intent);
            }
        });

        TextView tvSend = (TextView)findViewById(R.id.send);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        TextView start= (TextView)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, TestActivity.class);
//                startActivity(intent);
                 try{
                     Intent intent = new Intent();
                     intent.setAction("com.tcl.joylockscreen.tracker.action");
                     intent.setPackage("com.tcl.joylockscreen");
                     startService(intent);
                 }catch (Exception e){
                     //Toast.makeText(MainActivity.this,"service error",Toast.LENGTH_SHORT).show();
                     e.printStackTrace();
                 }
            }
        });

        Button button = (Button) findViewById(R.id.db);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, DbTestActivity.class);
//                startActivity(intent);
            }
        });
        TextView test_enum = (TextView)findViewById(R.id.test_enum);
        test_enum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        builder.setLargeIcon(bitmap)
                .setShowWhen(true)
                .setContentText("Joy Lockscreen Privacy Policy")
                .setContentTitle("Notification Demo")
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("demo");
        notificationManager.notify(id++,builder.build());
    }

    //在7.0的手机上，setPackage("com.tcl.joylockscreen")不加会导致无法绑定
    private void initService(){
        Intent intent = new Intent();
        intent.setAction("com.tcl.joylockscreen.action");
        intent.setPackage("com.tcl.joylockscreen");
        serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                     joyLockScreen = IJoyLockScreen.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    joyLockScreen = null;
                }
        };
        try{
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(serviceConnection != null)
            unbindService(serviceConnection);
    }

    private void showMessage(Exception e){
        String msg = e.getMessage();
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] elements =  e.getStackTrace();
        if(elements != null){
            for(StackTraceElement element:elements){
                sb.append("    at "+element.toString()+"\n");
            }
        }
        Throwable throwable = e.getCause();
        Log.e("flying","java.lang.Exception: "+msg);
        Log.e("flying",sb.toString());
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

    }


    private void showMemory(ActivityManager memoryInfo){
        Runtime runtime = Runtime.getRuntime();
        float total = (runtime.totalMemory()>>20);
        float max = (float) ((runtime.maxMemory()>>10)*1.0/1024);
        float free = (float) ((runtime.freeMemory()>>10)*1.0/1024);
        Log.e("flying","total: "+total);
        Log.e("flying","max: "+max);
        Log.e("flying","free: "+free);
        Log.e("flying","used: "+(total-free));

        Log.e("flying","info.totalMem:"+memoryInfo.getMemoryClass());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkWritePermission(){
        int result = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.e("flying","result: "+result);
        Log.e("flying","pid: "+ Process.myPid());
        Log.e("flying","uid: "+Process.myUid());
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Log.e("flying","deviceId: "+telephonyManager.getDeviceId());
        Log.e("flying","countryCode: "+telephonyManager.getSimCountryIso());
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
