package com.hm.retrofitrxjavademo.network;


import com.hm.retrofitrxjavademo.model.HttpResult;
import com.hm.retrofitrxjavademo.model.MovieEntity;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
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

    @GET("/")
    Observable<HttpResult<NowWeatherBean>> testNowWeather(@QueryMap Map<String, Object> map);

    @GET("https://api.douban.com/v2/movie/top250")
    Observable<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);

    //上传单个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadSingleFile(@Part("image") RequestBody file);

    //上传单个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadSingleFile(@Part("description") RequestBody description, @Part MultipartBody.Part file);   //上传单个文件

    //上传文件和参数
    @Multipart
    @POST("upload")
    Observable<String> uploadSingleFileAndParams(@Part MultipartBody.Part file, @Query("name") String name);

    //上传单个文件
    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadSingleFile(@Part("description") String description, @Part(value = "image", encoding = "8-bit") RequestBody requestBody);


    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadMultiFile(@Part("description") RequestBody description, @Part MultipartBody.Part... file);

    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadManyFile(@PartMap Map<String, RequestBody> map);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}

