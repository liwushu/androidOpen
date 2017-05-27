package com.flying.test.compount;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flying.test.R;
import com.flying.test.bean.HttpBean;
import com.flying.test.bean.News;
import com.flying.test.bean.NewsList;
import com.flying.test.http.NewsService;
import com.flying.test.utils.AppUtil;
import com.flying.test.utils.ConstantUtils;
import com.flying.test.utils.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;

public class TestActivity extends Activity {

    //String url = "https://kyfw.12306.cn/";
    String BAIDU_URL = "https://www.baidu.com";
    TextView tvTest;
    Button startBtn,netBtn;
    int height = 0;
    Retrofit mRetrofit ;
    private static final int TIMEOUT_IN_SECOND = 10;
    private final String from = "5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initViews();
        initRetrofit();
    }

    private void initRetrofit(){
        Retrofit.Builder builder = new Retrofit.Builder();
        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String imei = AppUtil.getIMEI();
                String androidId = AppUtil.getANDROIDID();
                String mac = AppUtil.getMac();
                String uuid = AppUtil.getUUID();
                String model = Build.MODEL;
                String versionCode = Integer.toString(AppUtil.getVersionCode());
                Request oldRequest = chain.request();
                Request newRequest =
                        oldRequest.newBuilder().addHeader("Content-Type", "application/json")
                                .addHeader("country", getCountry()).addHeader("language", getLanguage())
                                .addHeader("imei", imei)
                                .addHeader("androidId", androidId).addHeader("mac", mac).addHeader("uuid", uuid)
                                .addHeader("model","aaa").addHeader("from", from)
                                .addHeader("versionCode", versionCode)
                                .build();
                return chain.proceed(newRequest);
            }
        };
        builder.baseUrl(ConstantUtils.BASE_URL)
        .client(getHttpClient(requestInterceptor))
                .addConverterFactory(GsonConverterFactory.create());
        mRetrofit = builder.build();

    }

    private String getCountry() {
        return "mx";
    }

    private String getLanguage(){
        return "es";
    }

    private OkHttpClient getHttpClient(Interceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor).connectTimeout(TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_SECOND, TimeUnit.SECONDS);

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        builder.addInterceptor(logging);

        return builder.build();
    }

    private void initViews(){
        tvTest = (TextView)findViewById(R.id.test_view);
        startBtn = (Button)findViewById(R.id.start);
        tvTest.post(new Runnable() {
            @Override
            public void run() {
                height = tvTest.getHeight();
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(tvTest.getHeight() >height/2) {
                    invokeVisibleAnimator(tvTest.getHeight(),height/2);
                }else{
                    invokeVisibleAnimator(tvTest.getHeight(),height);
                }
            }
        });

        netBtn = (Button)findViewById(R.id.net_test);
        netBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NewsService newsService =  mRetrofit.create(NewsService.class);
                Call<HttpBean<NewsList>> call = newsService.loadRecommendNews(ConstantUtils.QUERY_TYPE_DROP_DOWN,20, String.valueOf(News.TYPE_PICTURE_TEXT));
                call.enqueue(new Callback<HttpBean<NewsList>>() {
                    @Override
                    public void onResponse(Call<HttpBean<NewsList>> call, Response<HttpBean<NewsList>> response) {
                        LogUtils.loge("flying","request: "+call.request());

                        LogUtils.loge("flying","request headers111: "+call.request().headers().toString());
                        LogUtils.loge("flying","code: "+response.code()+"  message: "+response.message()+"   head: "+response.headers());
                        LogUtils.loge("flying","response: "+response.body());
                    }

                    @Override
                    public void onFailure(Call<HttpBean<NewsList>> call, Throwable t) {
                        LogUtils.loge("flying","onFailure");
                    }
                });
            }
        });
    }

    private void invokeVisibleAnimator(int startValue,int endValue){

        ObjectAnimator animator = ObjectAnimator.ofInt(tvTest, "height",startValue,endValue);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(2000);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LogUtils.loge("flying","value: "+animation.getAnimatedValue());
                ViewGroup.LayoutParams params = tvTest.getLayoutParams();
                params.height = (int) animation.getAnimatedValue();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LogUtils.loge("flying","onAnimationEnd");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
