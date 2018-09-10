package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.network.API;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 单独使用 Retrofit
 */
public class OnlyRetrofitActivity extends BaseActivity {

    private static final String TAG = "OnlyRetrofitActivity";
    private static final String BASE_URL = "http://api.k780.com";
    private Retrofit retrofit;

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
       /* Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("app", "weather.today");
        map.put("weaid", 1);
        map.put("appkey", 10003);
        map.put("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4");
        map.put("format", "json");
        Call<NowWeatherBean> call = api.retrofitGetNowWeather(map);
        call.cancel();*/
        /*call.enqueue(new Callback<NowWeatherBean>() {
            @Override
            public void onResponse(Call<NowWeatherBean> call, Response<NowWeatherBean> response) {
                Log.d(TAG, response.body().getResult().getCitynm());
            }

            @Override
            public void onFailure(Call<NowWeatherBean> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });*/

        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.k780.com:88")
                .client(new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build())
                .build();
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

        API service = retrofit.create(API.class);
       /* HashMap<String, Object> map = new HashMap<>();
        map.put("app", "weather.today");
        map.put("weaid", 1);
        map.put("appkey", 10003);
        map.put("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4");
        map.put("format", "json");*/
        List list=new ArrayList();

        Call<ResponseBody> listCall = service.getNowWeather(list);

        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

            }
        });
    }


}
