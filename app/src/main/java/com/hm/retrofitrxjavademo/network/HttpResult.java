package com.hm.retrofitrxjavademo.network;

import com.google.gson.annotations.SerializedName;

/**
 * 用来统一对返回结果预处理
 *
 * @param <T>
 */

public class HttpResult<T> {

    //这里赋值为1，是为了获取豆瓣top250的接口能用，真实情况下应该为0
    private int resultCode = 1;
    private String resultMessage;
    @SerializedName(value = "subjects", alternate = "result")
    public T data;

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
