package com.hm.retrofitrxjavademo.network;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.text.ParseException;

import retrofit2.HttpException;


/**
 * Created by 12262 on 2016/5/30.
 */
public class ExceptionHandler {

    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int UN_KNOWN_ERROR = -400;
    private static final int JSON_PARSE_ERROR = -401;
    private static final int CONNECT_ERROR = -402;
    public static final int NO_DATA_ERROR = -403;

    public static APIException handleException(Throwable e) {
        APIException ex;
        if (e instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex = new APIException(httpException.code(), httpException.getMessage());
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {    //服务器返回的错误
            ServerException resultException = (ServerException) e;
            ex = new APIException(resultException.getCode(), resultException.getMessage());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new APIException(JSON_PARSE_ERROR, e.getMessage());
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new APIException(CONNECT_ERROR, e.getMessage());
            return ex;
        } else {
            ex = new APIException(UN_KNOWN_ERROR,e.getMessage());
            return ex;
        }
    }
}
