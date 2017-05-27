package com.flying.test.utils;

import android.app.Application;
import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flying.test.progress.ProgressDataFetcher;

import java.io.InputStream;


/**
 * Created by liwu.shu on 2017/5/11.
 */

public class ImageLoadUtils {

    public static void loadImage(Context mc, ImageView targetView,int placeHolder,int errorIcon,String url,Handler handler){
        Glide.with(mc)
                .using(new ProgressModelLoader(handler))
                .load(url)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                .placeholder(placeHolder)
                .error(errorIcon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(targetView);
    }

    public static class ProgressModelLoader implements StreamModelLoader<String> {

        private Handler handler;

        public ProgressModelLoader(Handler handler) {
            this.handler = handler;
        }

        @Override
        public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
            LogUtils.logd("flying","model: "+model);
            return new ProgressDataFetcher(model, handler);
        }
    }

    // 清除图片磁盘缓存，调用Glide自带方法
    public static boolean clearCacheDiskSelf(final Context mc) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(mc).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(mc).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 清除Glide内存缓存
    public static boolean clearCacheMemory(Context mc) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(mc).clearMemory();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
