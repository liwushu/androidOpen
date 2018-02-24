package com.android.task;

import android.widget.TextView;

/**
 * @author shuliwu
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    protected void init(){
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("I am "+this.getClass().getSimpleName());
    }
}
