package com.hm.retrofitrxjavademo.download;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by ljd on 3/29/16.
 */
public interface DownloadApi {

    @Streaming
    @GET
    Observable<ResponseBody> downLoad(@Url() String url);
}
