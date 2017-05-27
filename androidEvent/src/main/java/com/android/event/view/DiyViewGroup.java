package com.android.event.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.android.event.utils.LogUtils;


/**
 * Created by liwu.shu on 2016/12/30.
 */

public class DiyViewGroup extends FrameLayout {

    private String tag="DiyViewGroup";
    private boolean isDownInterception;
    private boolean isRequestDisallowInterception;
    private boolean isUpInterception;
    private boolean isMoveinterception;

    public DiyViewGroup(Context context) {
        this(context,null);
    }

    public DiyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DiyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public void setDownInterception(boolean isDownInterception){
        this.isDownInterception = isDownInterception;
    }

    public void setMoveinterception(boolean isMoveinterception){
        this.isMoveinterception = isMoveinterception;
    }

    public void setRequestDisallowInterception(boolean requestDisallowInterception) {
        isRequestDisallowInterception = requestDisallowInterception;
    }

    public void setUpInterception(boolean isUpInterception){
        this.isUpInterception = isUpInterception;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                //LogUtils.logd(tag,"onInterceptTouchEvent_ACTION_DOWN");
                if(isDownInterception) {
                    return true;
                }
                if(isRequestDisallowInterception) {
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //LogUtils.logd(tag,"onInterceptTouchEvent_ACTION_MOVE："+isMoveinterception);
                if(isMoveinterception){
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //LogUtils.logd(tag,"onInterceptTouchEvent_ACTION_UP");
                if(isUpInterception)
                    return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                //LogUtils.logd(tag,"onTouchEvent_ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                //LogUtils.logd(tag,"onTouchEvent_ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
               // LogUtils.logd(tag,"onTouchEvent_ACTION_UP");
                break;
        }
        return super.onTouchEvent(ev);
    }

}
