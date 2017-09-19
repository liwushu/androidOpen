package com.android.event;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.event.view.CircleColorView;
import com.android.event.view.CircleMoveView;
import com.android.event.view.CircleProgressView;
import com.android.event.view.CircleRotateView;
import com.android.event.view.CircleSmileView;

public class LoadingViewActivity extends Activity {
    private Button startBtn, endBtn;
    private CircleColorView circleColorView;
    private CircleMoveView circleMoveView;
    private CircleSmileView circleSmileView;
    private CircleRotateView circleRotateView;
    private CircleProgressView circleProgressView;
    private ToggleButton toggleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_view);
        initView();
    }

    private void initView() {
        circleColorView = (CircleColorView) findViewById(R.id.first_view);
        circleMoveView = (CircleMoveView) findViewById(R.id.second_view);
        circleSmileView = (CircleSmileView) findViewById(R.id.third_view);
        circleRotateView = (CircleRotateView) findViewById(R.id.four_view);
        circleProgressView = (CircleProgressView) findViewById(R.id.five_view);
        startBtn = (Button) findViewById(R.id.start_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleColorView.startAnimation();
                circleMoveView.startAnimation();
                circleSmileView.startAnimation();
                circleRotateView.startAnimation();
                circleProgressView.startAnimation();

            }
        });
        endBtn = (Button) findViewById(R.id.end_btn);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleColorView.endAnimation();
                circleMoveView.endAnimation();
                circleSmileView.endAnimation();
                circleRotateView.endAnimation();
                circleProgressView.endAnimation();
            }
        });

        toggleButton = (ToggleButton)findViewById(R.id.switch_view);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),"show",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"hide",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
