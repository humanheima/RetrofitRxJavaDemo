package com.hm.retrofitrxjavademo.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用来统一对返回结果预处理
 *
 * @param <T>
 */
public class HttpResult<T> {

    public String cmd;
    public int resultCode;
    public String resultMessage;
    public
    @SerializedName("body")
    T data;

    public boolean isSuccess() {
        return resultCode == 1;
    }

}
