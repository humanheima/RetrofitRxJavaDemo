package com.hm.retrofitrxjavademo.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityOnlyRetrofitBinding;
import com.hm.retrofitrxjavademo.intercepter.HttpLoggingInterceptor;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;
import com.hm.retrofitrxjavademo.network.API;
import com.hm.retrofitrxjavademo.network.GitHubService;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 单独使用 Retrofit
 */
@SuppressLint("AutoDispose")
public class OnlyRetrofitActivity extends BaseActivity<ActivityOnlyRetrofitBinding> {

    private static final String TAG = "OnlyRetrofitActivity";
    private static final String BASE_URL = "http://api.k780.com";

    public static void launch(Context context) {
        Intent starter = new Intent(context, OnlyRetrofitActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_only_retrofit;
    }

    @Override
    protected void initData() {

    }

    public void simpleUse(View view) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitHubService service = retrofit.create(GitHubService.class);

        Call<ResponseBody> call = service.userInfo("humanheima");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "onResponse: current thread = " + Thread.currentThread().getName());
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try {
                        String string = responseBody.string();
                        Log.d(TAG, "onResponse: " + string);
                        viewBind.tvResult.setText(string);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "onResponse: ");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onResponse: current thread = " + Thread.currentThread().getName());
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void simpleUse1(View view) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("app", "weather.today");
        map.put("weaid", 1);
        map.put("appkey", 10003);
        map.put("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4");
        map.put("format", "json");
        api.getNowWeather(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NowWeatherBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NowWeatherBean nowWeatherBeanHttpResult) {
                        Log.e(TAG, "onNext: " + nowWeatherBeanHttpResult.getResult().toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
