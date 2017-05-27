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
import android.view.animation.LinearInterpolator;

import com.android.event.R;

/**
 * Created by tangyangkai on 2016/12/8.
 */

public class CircleSmileView extends View {


    private int firstColor;
    private int mWidth, mHeight;
    private Paint mPaint;
    private ValueAnimator startAnimator, sweepAnimator, degreeAnimation, pointXAnimation, pointYAnimation, pointAnimation;
    private float startAngle, sweepAngle, degree, pointX, pointY, point;
    private AnimatorSet animatorSet;
    private boolean isShowSmile;


    public CircleSmileView(Context context) {
        this(context, null);
    }

    public CircleSmileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleSmileView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        startAngle = -180;
        sweepAngle = 270;
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
        pointX = mHeight / 3;
        point = mHeight / 3;
        pointY = 0;
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth / 2, mHeight / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(firstColor);
        mPaint.setStrokeWidth(10);
        float r = mHeight / 3;

        if (isShowSmile) {
            canvas.drawPoint(-pointX, -pointY, mPaint);
            canvas.drawPoint(point, -pointY, mPaint);
        }


        canvas.rotate(degree);
        RectF rectf = new RectF(-r, -r, r, r);
        canvas.drawArc(rectf, startAngle, sweepAngle, false, mPaint);

    }


    public void startAnimation() {

        isShowSmile = false;
        startAngle = -180;
        sweepAngle = 270;
        degree = 0;
        startAnimator = ValueAnimator.ofFloat(-180, 0);
        startAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle = (float) animation.getAnimatedValue();
                invalidate();
            }

        });


        sweepAnimator = ValueAnimator.ofFloat(270, 180);
        sweepAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = (float) animation.getAnimatedValue();
                invalidate();
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

        animatorSet = new AnimatorSet();
        animatorSet.play(startAnimator).with(sweepAnimator).after(degreeAnimation);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.setDuration(1000);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isShowSmile = true;
                pointXAnimation.start();
                pointYAnimation.start();
                pointAnimation.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        pointXAnimation = ValueAnimator.ofFloat(mHeight / 3, mHeight / 4);
        pointXAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pointX = (float) animation.getAnimatedValue();
                invalidate();
            }

        });
        pointXAnimation.setDuration(1000);

        pointAnimation = ValueAnimator.ofFloat(mHeight / 3, mHeight / 4);
        pointAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                point = (float) animation.getAnimatedValue();
                invalidate();
            }

        });
        pointAnimation.setDuration(1000);

        pointYAnimation = ValueAnimator.ofFloat(0, mHeight / 4);
        pointYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pointY = (float) animation.getAnimatedValue();
                invalidate();
            }

        });
        pointYAnimation.setDuration(1000);

    }

    public void endAnimation() {
        animatorSet.end();
        pointXAnimation.end();
        pointYAnimation.end();
        pointAnimation.end();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            endAnimation();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE) {
            endAnimation();
        }
    }
}
