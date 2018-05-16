package com.example.inject;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
    private static final String PROC_NAME = "com.flying.inject";
    //private static final String PROC_NAME = "zygote";
    //private static final String LIB = "/system/lib64/libc.so";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnHook = (Button) this.findViewById(R.id.btn_hook);
        btnHook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Log.e(TAG,"exec11111");
                    Process process = Runtime.getRuntime().exec("su");
                    Log.e(TAG,"exec22222");
                    DataOutputStream writer = new DataOutputStream(process
                            .getOutputStream());

                    String inject = MainActivity.this.getFilesDir().toString() + "/inject";
                    File file = new File(inject);
                    Log.e(TAG,"file.exists: "+file.exists());
                    String proc = PROC_NAME;
                    String lib = MainActivity.this.getFilesDir().toString()
                            + "/libInjectso.so";


                    String strCmd = inject + " " + proc + " " + lib + "\n";
                    Log.e(TAG,"strCmd: "+strCmd);
                    writer.writeBytes(strCmd);
                    writer.flush();
                    writer.writeBytes("exit\n");
                    writer.flush();
                    try {
                        process.waitFor();
                        process.destroy();
                        Log.e(TAG,"process end");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Log.e(TAG,"InterruptedException");
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e(TAG,"IOException");
                }

            }
        });

    }
}
