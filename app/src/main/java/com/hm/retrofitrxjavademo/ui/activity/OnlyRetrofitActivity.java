package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;
import com.hm.retrofitrxjavademo.network.API;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 单独使用 Retrofit
 */
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
        Retrofit retrofit = new Retrofit.Builder()
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
    }
}
