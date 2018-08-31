package com.hm.retrofitrxjavademo.network;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by dumingwei on 2017/3/2.
 */
public interface UpLoadFileApi {

    // TODO: 2017/3/2 这个方法如何改进呢？
    @Multipart
    @POST("http://ylbook.xun-ao.com/api/upload.php")
    Observable<ResponseBody> uploadFile(@Part("file[]") File file);

    //上传单个文件
    @Multipart
    @POST("https://test.youshikoudai.com/1bPlus-web/api/file/uploadFile")
    Observable<String> uploadFile(@Part("file") RequestBody file);

    //上传单个文件
    @Multipart
    @POST("https://test.youshikoudai.com/1bPlus-web/api/file/uploadFile")
    Observable<String> uploadFileWithRealName(@Part MultipartBody.Part file);

    //上传参数和单个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadSingleFile(@Part("description") RequestBody description, @Part MultipartBody.Part file);

    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadMultiFile(@Part MultipartBody.Part... file);

    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadMultiFile(@Part List<MultipartBody.Part> partList);

    //上传多个文件
    @Multipart
    @POST("upload")
    Observable<String> uploadManyFile(@PartMap Map<String, RequestBody> map);

}
