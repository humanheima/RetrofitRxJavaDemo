package com.hm.retrofitrxjavademo.network;

import com.google.gson.annotations.SerializedName;

/**
 * 用来统一对返回结果预处理
 *
 * @param <T>
 */

public class HttpResult<T> {

    @SerializedName(value = "subjects", alternate = "result")
    public T data;
    @SerializedName("msg")
    private String resultMessage;
    private int success;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
