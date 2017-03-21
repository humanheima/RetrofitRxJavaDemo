package com.hm.retrofitrxjavademo.download;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by ljd on 3/29/16.
 */
public interface DownloadApi {

    @Streaming
    @GET("http://140.207.247.205/imtt.dd.qq.com/16891/20AD322F5D49B9F649A70C4A3083D8D2.apk?mkey=58758c694bc7812a&f=d588&c=0&fsname=com.xunao.wanfeng_1.1_4.apk&csr=4d5s&p=.apk")
    Observable<ResponseBody> downLoad(@Header("RANGE")String range);
}
