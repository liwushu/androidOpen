package com.flying.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flying.test.R;

public class TestMeasureActivity extends Activity {

    TextView tvContent;
    View parentView;
    int maxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_measure);
        initViews();
    }

    private void initViews(){
        Button click = (Button)findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) parentView.getLayoutParams();
                layoutParams.height = maxHeight;
                parentView.requestLayout();
            }
        });
        tvContent = (TextView)findViewById(R.id.content);
        parentView = findViewById(R.id.parent);
    }
}
