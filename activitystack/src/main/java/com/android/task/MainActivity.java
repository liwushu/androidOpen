package com.android.task;

import android.widget.TextView;

public class MainActivity extends BaseActivity {

    @Override
    public void init(){
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("I am "+this.getClass().getSimpleName());
    }
}
