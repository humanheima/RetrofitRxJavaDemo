package com.hm.retrofitrxjavademo.network;


import com.hm.retrofitrxjavademo.model.MovieEntity;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;

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


    //@GET("weather?cityid=CN101020100&key=fcaa02b41e9048e7aa5854b1e279e1c6")
    @GET("weather")
    Observable<String> getWeather(@Query("cityid") String cityId, @Query("key") String key);

    //@GET("/")
    //Observable<NowWeatherBean> getNowWeather(@QueryMap Map<String, Object> map);

    @GET("/")
    Observable<HttpResult<NowWeatherBean>> testNowWeather(@QueryMap Map<String, Object> map);

    @GET("https://api.douban.com/v2/movie/top250")
    Observable<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);

    /**
     * @param path 扫描用户二维码得到的信息
     * @return 获取参会签到所需的userId
     * 访问一个完整的路径，替代BaseUrl
     */
    @GET()
    Observable<HttpResult<Object>> getSignAttendUserId(@Url String path);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("/")
    Call<NowWeatherBean> getNowWeather(@QueryMap Map<String, Object> map);

    /**添加头信息
     @GET("/")
     //添加多个头部
     @Headers({"Accept-Encoding: gzip, deflate", "Accept-Language: zh-CN"})
     Call<NowWeatherBean> getNowWeather(@QueryMap Map<String, Object> map);

     //添加一个头部信息
     @GET("/")
     @Header("Accept-Encoding: gzip, deflate")
     Call<NowWeatherBean> getNowWeather(@QueryMap Map<String, Object> map);

     @GET("/")
     //动态添加头部信息
     Call<NowWeatherBean> getNowWeather(@QueryMap Map<String, Object> map, @Header("Accept-Language") String lang);
     */
}

