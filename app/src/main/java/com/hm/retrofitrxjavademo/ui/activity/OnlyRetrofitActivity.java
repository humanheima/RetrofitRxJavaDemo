package com.hm.retrofitrxjavademo.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;
import com.hm.retrofitrxjavademo.network.API;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 单独使用 Retrofit
 */
@SuppressLint("AutoDispose")
public class OnlyRetrofitActivity extends BaseActivity {

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

    /*public void simpleUse(View view) {

        GitHubService service = retrofit.create(GitHubService.class);

        Call<List<Repo>> listCall = service.listRepos("octocat");

        listCall.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                List<Repo> repoList = response.body();
                for (Repo repo : repoList) {
                    Log.d(TAG, "onResponse: " + repo.getName());
                }
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {

            }
        });
    }
*/

    public void simpleUse(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().build())
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
