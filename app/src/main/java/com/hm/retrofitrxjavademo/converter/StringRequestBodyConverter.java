package com.hm.retrofitrxjavademo.converter;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by dumingwei on 2018/4/8 0008.
 */
public class StringRequestBodyConverter<T> implements Converter<T, RequestBody> {

    final static StringRequestBodyConverter<Object> INSTANCE = new StringRequestBodyConverter<>();

    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain; charset=UTF-8");

    private StringRequestBodyConverter() {
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
    }
}
