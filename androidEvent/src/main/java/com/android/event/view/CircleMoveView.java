package com.android.event.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.android.event.R;


/**
 * Created by tangyangkai on 2016/12/8.
 */

public class CircleMoveView extends View {

    private int firstColor, secondColor;
    private int mWidth, mHeight;
    private Paint mPaint;
    private int number = 0;
    private int mRadius;
    private ValueAnimator valueAnimator, degreeAnimation;
    private int mStartWidth;
    private float degree;

    public CircleMoveView(Context context) {
        this(context, null);
    }

    public CircleMoveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取我们自定义的样式属性
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleColorView, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {

                case R.styleable.CircleColorView_firstColor:
                    // 默认颜色设置为黑色
                    firstColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CircleColorView_secondColor:
                    secondColor = array.getColor(attr, Color.BLACK);
                    break;

            }

        }
        init();
        array.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = widthSize * 1 / 2;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = heightSize * 1 / 2;
        }
        mWidth = width;
        mHeight = height;
        mRadius = mHeight / 10;
        mStartWidth = mHeight / 2;
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画布原点移到中心
        canvas.translate(mWidth / 2, mHeight / 2);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.rotate(degree);
        for (int i = 0; i < 3; i++) {
            if (i == 1) {
                if (number % 2 == 0) {
                    mPaint.setColor(firstColor);
                } else {
                    mPaint.setColor(secondColor);
                }
                canvas.drawCircle(0, 0, mRadius, mPaint);
            } else {
                if (number % 2 == 0) {
                    mPaint.setColor(secondColor);
                } else {
                    mPaint.setColor(firstColor);
                }
                canvas.drawCircle((i - 1) * mStartWidth, 0, mHeight / 10, mPaint);
            }
        }

    }


    public void startAnimation() {


        valueAnimator = ValueAnimator.ofInt(mHeight / 2, 0, -mHeight / 2);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartWidth = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }

        });
        //动画循环执行
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setDuration(1000);
        valueAnimator.start();
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                number++;
            }
        });
        degreeAnimation = ValueAnimator.ofFloat(0, 360);
        degreeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) animation.getAnimatedValue();
                invalidate();
            }

        });
        degreeAnimation.setRepeatCount(ValueAnimator.INFINITE);
        degreeAnimation.setDuration(1000);
        degreeAnimation.start();

    }

    public void endAnimation() {

        //cancel结束时保留当前动画值
        valueAnimator.cancel();

        //end结束时会计算最终值
        valueAnimator.end();
        degreeAnimation.end();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            endAnimation();
        }
    }
}
