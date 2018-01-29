package com.hm.retrofitrxjavademo.network.convert;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by dumingwei on 2017/3/2.
 */
public class FileRequestBodyConvertFactory extends Converter.Factory {

    @Override
    public Converter<File, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FileRequestBodyConvert();
    }

  /*  @Override
    public Converter<ResponseBody, FileResult> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FileResponseBodyConvert();
    }*/

    private static class FileRequestBodyConvert implements Converter<java.io.File, RequestBody> {
        @Override
        public RequestBody convert(java.io.File file) throws IOException {
            //application/otcet-stream 表示任意的二进制类型
            return RequestBody.create(MediaType.parse("multipart/form-data"), file);
        }
    }

    private static class FileResponseBodyConvert implements Converter<ResponseBody, FileResult> {
        @Override
        public FileResult convert(ResponseBody responseBody) throws IOException {
            FileResult fileResult = new Gson().fromJson(responseBody.string(), FileResult.class);
            return fileResult;
        }
    }

    private static class FileResult<T> {
        public int resultCode;
        public String resultMessage;
        @SerializedName("body")
        public T data;
    }

}
