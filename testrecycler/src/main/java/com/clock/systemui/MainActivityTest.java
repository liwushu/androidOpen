package com.clock.systemui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flying.libinject.ContentView;
import com.flying.libinject.EventInject;
import com.flying.libinject.ViewInject;
import com.flying.libinject.ViewInjetUtils;

@ContentView(R.layout.activity_main_test)
public class MainActivityTest extends Activity {

    private static final String TAG = "MainActivityTest";

    @ViewInject(R.id.test1)
    TextView test1;
    @ViewInject(R.id.test2)
    TextView test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjetUtils.inject(this);
        android.util.Log.e(TAG,"test1: "+test1);
        android.util.Log.e(TAG,"test2: "+test2);
    }

    @EventInject({R.id.test1,R.id.test2})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.test1:
                Toast.makeText(this,"test111 click",Toast.LENGTH_SHORT).show();
                break;
            case R.id.test2:
                Toast.makeText(this,"test222 click",Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
