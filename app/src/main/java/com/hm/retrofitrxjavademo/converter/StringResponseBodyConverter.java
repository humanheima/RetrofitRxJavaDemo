package com.hm.retrofitrxjavademo.converter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by dumingwei on 2018/4/8 0008.
 */
public class StringResponseBodyConverter implements Converter<ResponseBody, String> {

    static final StringResponseBodyConverter INSTANCE = new StringResponseBodyConverter();

    @Override
    public String convert(ResponseBody value) throws IOException {
        return value.string();
    }
}
