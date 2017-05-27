package com.flying.test.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flying.test.R;
import com.flying.test.utils.ConstantUtils;
import com.flying.test.utils.LogUtils;

import java.lang.reflect.Method;


/**
 * Created by cbf on 17/2/22.
 */

public class NotificationDebugActvity extends Activity  {

    private static String TAG = "NotificationDebugActvity";
    TextView tvShowContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notfication_debug);
        initViews();
    }

    private void initViews(){
        Button goSetting = (Button)findViewById(R.id.go_setting);
        goSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoNotificationAccess();
            }
        });

        Button showContext = (Button)findViewById(R.id.show_context);
        TextView showContent = (TextView)findViewById(R.id.show_content);
        showContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Class<?> debugClz = Class.forName("android.os.Debug");
                    Class<?> paramClz = Class.forName("android.app.ContextImpl");
                    LogUtils.loge("flying","debugClz: "+debugClz);
                    LogUtils.loge("flying","paramClz: "+paramClz);

                    Method[] methods = debugClz.getDeclaredMethods();
                    for(Method method:methods){
                        LogUtils.loge("flying","method: "+method.getName()+ "  paramType: "+method.getParameterTypes());
                    }

                    Method countInstancesOfClass = debugClz.getDeclaredMethod("countInstancesOfClass",paramClz.getClass());
                    countInstancesOfClass.setAccessible(true);
                    long value = (long) countInstancesOfClass.invoke(null,paramClz);
                    LogUtils.loge("flying","value:"+value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ConstantUtils.SYSTEM_LOCKSCREEN_RESULT == requestCode) {
            LogUtils.loge(TAG, "requestCode_lock: " + requestCode);

        } else if (ConstantUtils.SYSTEM_NOTIFICATION_RESULT == requestCode) {
            LogUtils.loge(TAG, "requestCode_NoTI: " + requestCode);
        }
    }

    public  void gotoNotificationAccess(){
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivityForResult(intent,ConstantUtils.SYSTEM_NOTIFICATION_RESULT);
    }
}
