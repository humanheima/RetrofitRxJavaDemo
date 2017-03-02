package com.hm.retrofitrxjavademo.network;

import com.google.gson.annotations.SerializedName;

/**
 * 用来统一对返回结果预处理
 *
 * @param <T>
 */

public class HttpResult<T> {

    public int resultCode;
    public String resultMessage;
    @SerializedName("body")
    public T data;

    public boolean isSuccess() {
        return resultCode == 1;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
