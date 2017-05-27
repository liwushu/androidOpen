package com.android.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import com.android.event.utils.LogUtils;
import com.android.event.view.DiyView;
import com.android.event.view.DiyViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestAndroidEventActivity extends AppCompatActivity {

    @BindView(R.id.red)
    DiyViewGroup redViewGroup;
    @BindView(R.id.green)
    DiyViewGroup greenViewGroup;
    @BindView(R.id.white)
    DiyView whiteView;

    boolean isRequestDisallowInteception = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_android_event);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        ButterKnife.bind(this);
        redViewGroup.setTag("RedView");
        greenViewGroup.setTag("greenView");
        whiteView.setTag("WhiteView");
        greenViewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isRequestDisallowInteception)
                    requestDisallowInterception(greenViewGroup,true);
                return false;
            }
        });
    }

    @OnClick({R.id.red,R.id.green,R.id.white,
            R.id.red_intercept,R.id.red_m_intercept,R.id.green_intercept,
            R.id.red_u_intercept,R.id.green_u_intercept,R.id.green_m_intercept,
            R.id.green_request,
            R.id.reset,R.id.anr})
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.red:

                showClick("redViewGroup: "+redViewGroup.getChildCount());
                break;
            case R.id.green:
                showClick("greenViewGroup: "+greenViewGroup.getChildCount());
                break;
            case R.id.white:
                showClick("whiteView");
                break;
            case R.id.red_intercept:
                setDownInterception(redViewGroup,true);
                break;
            case R.id.green_intercept:
                setDownInterception(greenViewGroup,true);
                break;
            case R.id.red_u_intercept:
                setUpInterception(redViewGroup,true);
                break;
            case R.id.green_u_intercept:
                setUpInterception(greenViewGroup,true);
                break;
            case R.id.green_request:
                requestDisallowInterception(greenViewGroup,true);
                break;
            case R.id.reset:
                reset();
                break;
            case R.id.anr:
                try{
                    Thread.sleep(10*1000);
                }catch (Exception e){

                }
                break;
            case R.id.green_m_intercept:
                setMoveInterception(greenViewGroup,true);
                break;
            case R.id.red_m_intercept:
                setMoveInterception(redViewGroup,true);
                break;
        }
    }

    private void reset(){
        setDownInterception(redViewGroup,false);
        setUpInterception(redViewGroup,false);
        setMoveInterception(redViewGroup,false);
        setDownInterception(greenViewGroup,false);
        setUpInterception(greenViewGroup,false);
        setMoveInterception(greenViewGroup,false);
        requestDisallowInterception(greenViewGroup,false);
        isRequestDisallowInteception = false;
    }

    private void setDownInterception(DiyViewGroup viewGroup,boolean isDownInterception){
        viewGroup.setDownInterception(isDownInterception);
    }

    private void setMoveInterception(DiyViewGroup viewGroup,boolean isMoveInterception){
        viewGroup.setDownInterception(false);
        viewGroup.setMoveinterception(isMoveInterception);
    }

    private void setUpInterception(DiyViewGroup viewGroup,boolean isUpInterception){
        viewGroup.setDownInterception(false);
        viewGroup.setMoveinterception(false);
        viewGroup.setUpInterception(isUpInterception);
    }

    private void showClick(String view){
        LogUtils.logd(view,"click");
    }

    private void requestDisallowInterception(DiyViewGroup view, boolean isDisallow){
        isRequestDisallowInteception = true;
        view.setRequestDisallowInterception(isDisallow);
        System.out.println("requestDisallowInterceptTouchEvent: "+isDisallow);
    }
}
