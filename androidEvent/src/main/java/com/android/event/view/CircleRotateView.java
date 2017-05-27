package com.android.event.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.android.event.R;


/**
 * Created by tangyangkai on 2016/12/8.
 */

public class CircleRotateView extends View {


    private int firstColor;
    private int mWidth, mHeight;
    private Paint mPaint;
    private ValueAnimator radiusAnimator, degreeAnimation;
    private float degree, radius;


    public CircleRotateView(Context context) {
        this(context, null);
    }

    public CircleRotateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleRotateView(Context context, AttributeSet attrs, int defStyleAttr) {
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

            }

        }
        init();
        array.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        degree = 0;

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
        radius = mHeight / 4;

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth / 2, mHeight / 2);
        mPaint.setColor(firstColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, radius, mPaint);
        canvas.rotate(degree);
        RectF rectf = new RectF(-radius * 5 / 3, -radius * 5 / 3, radius * 5 / 3, radius * 5 / 3);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        canvas.drawArc(rectf, 50, 80, false, mPaint);
        canvas.drawArc(rectf, -130, 80, false, mPaint);

    }


    public void startAnimation() {

        radius = mHeight / 4;
        degree = 0;
        radiusAnimator = ValueAnimator.ofFloat(mHeight / 4, mHeight / 8, mHeight / 4);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float) animation.getAnimatedValue();
                invalidate();
            }

        });
        radiusAnimator.setRepeatCount(ValueAnimator.INFINITE);
        radiusAnimator.setDuration(1500);
        radiusAnimator.start();

        degreeAnimation = ValueAnimator.ofFloat(0, 360);
        degreeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) animation.getAnimatedValue();
                invalidate();
            }

        });
        degreeAnimation.setRepeatCount(ValueAnimator.INFINITE);
        degreeAnimation.setDuration(1500);
        degreeAnimation.start();


    }

    public void endAnimation() {
        degreeAnimation.end();
        radiusAnimator.end();


    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            endAnimation();
        }
    }
}
