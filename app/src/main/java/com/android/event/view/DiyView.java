package com.android.event.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.android.event.utils.LogUtils;

/**
 * Created by liwu.shu on 2016/12/30.
 */

public class DiyView extends TextView {
    private String tag="";

    public DiyView(Context context) {
        this(context,null);
    }

    public DiyView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DiyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                LogUtils.d(tag,"onTouchEvent_ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d(tag,"onTouchEvent_ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d(tag,"onTouchEvent_ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtils.d(tag,"onTouchEvent_ACTION_CANCEL");
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setTag(String tag){
        this.tag = tag;
    }
}
