package com.hm.retrofitrxjavademo.network;


import com.hm.retrofitrxjavademo.model.NowWeatherBean;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by dmw on 2016/9/9.
 */
public interface API {
    //@GET("weather?cityid=CN101020100&key=fcaa02b41e9048e7aa5854b1e279e1c6")
    @GET("weather")
    Observable<String> getWeather(@Query("cityid") String cityId, @Query("key") String key);

    @GET("/")
    Observable<NowWeatherBean> getNowWeather(@QueryMap Map<String, Object> map);
}

