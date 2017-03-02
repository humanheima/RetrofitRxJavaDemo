package com.hm.retrofitrxjavademo.network;


import com.hm.retrofitrxjavademo.model.MovieEntity;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by dmw on 2016/9/9.
 */
public interface API {

    @GET("https://api.github.com/users/{user}/repos")
    Observable<String> getMyRepos(@Path("user") String user, @Query("page") String page, @Query("per_page") String per_page);

    Observable<String> getMyRepos(@Path("user") String user, @QueryMap Map<String, String> queryMap);

    @FormUrlEncoded
    @POST("index.php")
    Observable<String> post(@Field("name") String name, @Field("age") String age);

    @FormUrlEncoded
    @POST("index.php")
    Observable<String> post(@FieldMap Map<String, String> fieldMap);


    //@GET("weather?cityid=CN101020100&key=fcaa02b41e9048e7aa5854b1e279e1c6")
    @GET("weather")
    Observable<String> getWeather(@Query("cityid") String cityId, @Query("key") String key);

    @GET("/")
    Observable<NowWeatherBean> getNowWeather(@QueryMap Map<String, Object> map);

    @GET("/")
    Observable<HttpResult<NowWeatherBean>> testNowWeather(@QueryMap Map<String, Object> map);

    @GET("https://api.douban.com/v2/movie/top250")
    Observable<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}

