package com.flying.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flying.test.R;
import com.flying.test.utils.LogUtils;

/**
 * Created by liwu.shu on 2017/5/23.
 */

public class WrapRelativeLayout extends RelativeLayout {
    final String STR= "adbadaadfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasd" +
            "adfasdfasdfasdfasdfadfadsfasdf" +
            "adfasdfasdfasdfasdfa" +
            "asdfasdfasdfasdf" +
            "asdfasdfasdf" +
            "asdfadsfasd" +
            "adfasdfasdf" +
            "asdfasdfasdfasdfadfaf" +
            "asdfadfasdfadfadfasdfasdfadasaasasaasasasaasj" +
            "adasdajsasasjaasasjasasdajasasajasasjasasjaasajasaj" +
            "111111111aaaaaaaaaaaaaaaaaaaa111111111111111111111" +
            "222222222bbbbbbbbbbbbbbbbbbbbb22222222222222222222" +
            "333333333cccccccccccccccccccc33333333333333333333"+
            "adasdajsasasjaasasjasasdajasasajasasjasasjaasajasaj" +
            "111111111aaaaaaaaaaaaaaaaaaaa111111111111111111111" +
            "222222222bbbbbbbbbbbbbbbbbbbbb22222222222222222222" +
            "333333333cccccccccccccccccccc33333333333333333333" ;
    TextView contentView;
    int maxHeight =500,minHeight;
    int mHeight = 0;
    int downY,lastDownY;
    int contentHeight =0;
    int touchSlop = 5;
    boolean isScrollUp = false;
    public WrapRelativeLayout(Context context) {
        this(context,null);
    }

    public WrapRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WrapRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        contentView = (TextView)findViewById(R.id.content);
        contentView.setText(STR);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                int width= contentView.getWidth();
                int height = contentView.getHeight();
                LogUtils.logd("flying","width: "+width+"   height: "+height);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.AT_MOST);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(1024,View.MeasureSpec.UNSPECIFIED);
                contentView.measure(widthMeasureSpec,heightMeasureSpec);
                int measureWidth = contentView.getMeasuredWidth();
                int measureHeight = contentView.getMeasuredHeight();
                LogUtils.logd("flying","measureWidth: "+measureWidth+"   measureHeight: "+measureHeight);
                contentHeight = measureHeight;
                minHeight = mHeight = getHeight();
            }
        });
        setClickable(true);

    }


    private void adjustHeight(int targetHeight){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height =layoutParams.height - targetHeight;
        LogUtils.logd("flying","layoutParams.height: "+layoutParams.height);
        setLayoutParams(layoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
       // LogUtils.logd("flying","onTouchEvent:mHeight "+mHeight+"  maxHeight: "+maxHeight);
        if(mHeight<maxHeight){
            int action = event.getAction()& MotionEvent.ACTION_MASK;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastDownY = (int) event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    downY = (int) event.getY();
                    int deltaY = downY - lastDownY;

                    LogUtils.logd("flying","deltaY: "+deltaY+"  touchSlop: "+touchSlop);

                    if (deltaY > 0 && Math.abs(deltaY) > touchSlop) {
                        isScrollUp = false;
                    }
                    if (deltaY < 0 && Math.abs(deltaY) > touchSlop)
                        isScrollUp = true;
                    //上滑
                    if(deltaY <0 && isScrollUp) {
                        invokeUpLogic(deltaY);
                    }else if(deltaY>0 && !isScrollUp){
                        invokeDownLogic(deltaY);
                    }else{
                        return true;
                    }
                    lastDownY = downY;
                    return true;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void invokeUpLogic(int deltaY){
        //LogUtils.logd("flying","up: "+deltaY+"  height: "+getHeight()+" maxHeight: "+maxHeight);
        //调整parent 的高度
        if(getCurrentHeight()<maxHeight){
            if(deltaY<getCurrentHeight()-maxHeight){
                deltaY = getCurrentHeight()-maxHeight;
            }
            //LogUtils.logd("flying","up adjust:"+deltaY);
            adjustHeight(deltaY);
        }else{
            //滚动到底部
            int contentScrollY = contentView.getScrollY();
            int contentCurrentHeight = contentView.getHeight();
            //LogUtils.logd("flying","up: "+"contentScrolY："+contentScrollY+" height: "+contentCurrentHeight+" contentH: "+contentHeight);
            if(contentScrollY+contentCurrentHeight>=contentHeight)
                return;
            if(deltaY<contentScrollY+contentCurrentHeight-contentHeight) //检查边界
                deltaY = contentScrollY+contentCurrentHeight-contentHeight;
            scrollSubView(deltaY);
        }
    }

    private void invokeDownLogic(int deltaY){
        int contentScrollY = contentView.getScrollY();
        int contentCurrentHeight = contentView.getHeight();
        //LogUtils.logd("flying","down: "+"contentScrolY："+contentScrollY+" height: "+contentCurrentHeight+" contentH: "+contentHeight);
        //content 滚动到top
        if(contentScrollY ==0){
            //不需要调整parent 的高度
            if(getCurrentHeight() == minHeight)
                return ;
            if(deltaY>getCurrentHeight()-minHeight) //检查边界
                deltaY = getCurrentHeight() - minHeight;
            adjustHeight(deltaY);
        }else{
            //滚动 content
            if(deltaY > contentScrollY) //检查边界
                deltaY = contentScrollY;
            scrollSubView(deltaY);
        }
    }

    private void scrollSubView(int offset){
        contentView.scrollBy(0,-offset);
    }

    private  int getCurrentHeight(){
       //return getLayoutParams().height;
        return getHeight();
    }
}
