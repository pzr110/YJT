package com.linkflow.cpe.net;

import android.util.Log;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.linkflow.cpe.Constans;
import com.linkflow.cpe.TokenUtils;
import com.zhy.http.okhttp.utils.L;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import app.library.linkflow.Constant;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class Api {
    public static Retrofit mRetrofit;
    private static ApiService mApiService;
    private static OkHttpClient okHttpClient = null;
    public static int TIME_OUT = 20;

    private static boolean checkNull() {
        return mRetrofit == null;
    }

    private static void init() {
        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(okLogInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {


                        Log.e("TAGPZR","Bool："+ TokenUtils.hasToken());

                        if (TokenUtils.hasToken()) {
                            Request newRequest = chain.request().newBuilder()
                                    .header("x-access-token", TokenUtils.getToken())
//                                    .header("x-access-appid", "ty9fd2848a039abbbb")
                                    .header("x-access-user-id", TokenUtils.hasUserId()? TokenUtils.getUserId():"0")
                                    .build();
                            Log.e("TAGPZR","HRarAA");
                            L.e("X-Access-Token:"+TokenUtils.getToken());
                            Response respnse = chain.proceed(newRequest);
                            return respnse;
                        } else {
                            return chain.proceed(chain.request());
                        }

                    }
                })
                .addInterceptor(new HttpLoggingInterceptor()  //默认tag -- OkHttp    打印请求到的json字符串和查看log等信息
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constans.BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(CustomGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }

    public static ApiService getRetrofit() {
        if (checkNull()) {
            init();
        }
        return mApiService;
    }

    public static void resetRetrofit() {
        mRetrofit = null;
    }
}