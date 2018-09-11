package com.hm.retrofitrxjavademo.network;


import com.hm.retrofitrxjavademo.model.NowWeatherBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
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

    @GET
    Observable<HttpResult<Object>> getData(@Url String url, @QueryMap Map<String, Object> map);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("/")
    Call<NowWeatherBean> retrofitGetNowWeather(@QueryMap Map<String, Object> map);

    @GET("/")
    Observable<HttpResult<NowWeatherBean>> getNowWeather( @QueryMap Map<String, Object> map);

    /*@GET("/")
    Call<ResponseBody> getNowWeather(@Query("name") List lists);*/

    /**添加头信息
     @GET("/")
     //添加多个头部
     @Headers({"Accept-Encoding: gzip, deflate", "Accept-Language: zh-CN"})
     Call<NowWeatherBean> retrofitGetNowWeather(@QueryMap Map<String, Object> map);

     //添加一个头部信息
     @GET("/")
     @Header("Accept-Encoding: gzip, deflate")
     Call<NowWeatherBean> retrofitGetNowWeather(@QueryMap Map<String, Object> map);

     @GET("/")
     //动态添加头部信息
     Call<NowWeatherBean> retrofitGetNowWeather(@QueryMap Map<String, Object> map, @Header("Accept-Language") String lang);
     */
}

