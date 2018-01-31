package com.android.flying;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;

public class MainActivity extends Activity {
    LottieAnimationView lottieAnimationView;
    TextView tvStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        bindListener();
    }


    private void initViews() {
        lottieAnimationView = findViewById(R.id.lottie_view);
        tvStart =  findViewById(R.id.start);
    }

    private void bindListener() {
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LottieComposition.Factory.fromAssetFileName(getApplicationContext(),"lottie/gift.json", new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(@Nullable LottieComposition composition) {
                        Rect bounds = composition.getBounds();
                        Log.e("TAG","bounds: "+bounds);
                        int width = lottieAnimationView.getWidth();
                        int height = lottieAnimationView.getHeight();
                        float scaleX = width*1.0f/bounds.width();
                        float scaleY = height*1.0f/bounds.height();
//                        lottieAnimationView.setScaleX(scaleX);
//                        lottieAnimationView.setScaleY(scaleY);

                        Log.e("TAG","scaleX: "+scaleX+"   scaleY: "+scaleY);
                        lottieAnimationView.setScale(Math.min(scaleX,scaleY));
                        lottieAnimationView.setComposition(composition);
                        lottieAnimationView.playAnimation();
                        lottieAnimationView.loop(true);
                    }
                });
            }
        });
    }
}
