package com.flying.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestManager;
import com.flying.test.utils.ImageLoadUtils;
import com.flying.test.utils.LogUtils;
import com.flying.test.view.ProgressImageView;

import java.lang.ref.WeakReference;

public class TestDownloadImage extends Activity implements View.OnClickListener {
    private static final String URL = "http://10.128.208.10/tools/test.png";
    private ImageView showImage;
    private ProgressImageView progressImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View main = LayoutInflater.from(this).inflate(R.layout.activity_test_download_image,null);
        final int flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        System.out.print("flag: "+flag);
        main.setSystemUiVisibility(flag);
        main.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                main.setSystemUiVisibility(flag);
            }
        });
        setContentView(main);
        initViews();
    }

    private void initViews(){
        Button button = (Button)findViewById(R.id.load);
        button.setOnClickListener(this);
        Button button1 = (Button)findViewById(R.id.save);
        button1.setOnClickListener(this);
        Button button2 = (Button)findViewById(R.id.clear);
        button2.setOnClickListener(this);
        showImage = (ImageView)findViewById(R.id.show_img);
        progressImageView = (ProgressImageView)findViewById(R.id.progress_view);
    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.load:
                invokeLoad();
                break;
            case R.id.save:
                invokeSave();
                break;
            case R.id.clear:
                clear();
                break;
        }
    }

    private void invokeLoad(){
        ImageLoadUtils.loadImage(this.getApplicationContext(),showImage,R.mipmap.ic_launcher
        ,R.drawable.ic_joy_big,URL,new ProgressHandler(this,progressImageView));
    }

    private static class ProgressHandler extends Handler {

        private final WeakReference<Activity> mActivity;
        private final ProgressImageView mProgressImageView;

        public ProgressHandler(Activity activity, ProgressImageView progressImageView) {
            super(Looper.getMainLooper());
            mActivity = new WeakReference<>(activity);
            mProgressImageView = progressImageView;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        int percent = msg.arg1*100/msg.arg2;
                        mProgressImageView.setProgress(percent);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void invokeSave(){
        Drawable bitmapDrawable = showImage.getDrawable();
        int width= bitmapDrawable.getIntrinsicWidth();
        int height = bitmapDrawable.getIntrinsicHeight();
        LogUtils.logd("flying","width: "+width+"  height: "+height);

    }

    private void clear(){
        ImageLoadUtils.clearCacheDiskSelf(this);
        ImageLoadUtils.clearCacheMemory(this);
    }
}
