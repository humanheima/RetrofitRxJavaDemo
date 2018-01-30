package com.hm.retrofitrxjavademo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by dumingwei on 2017/3/2.
 */
public class JsonUtil {

    private static JsonUtil jsonUtil;
    private static Gson gson;
    private static GsonBuilder gb;

    private JsonUtil() {
        gb = new GsonBuilder();
        gb.disableHtmlEscaping();
        gson = gb.create();
    }

    public static JsonUtil getInstance() {
        if (jsonUtil == null) {
            synchronized (JsonUtil.class) {
                if (jsonUtil == null) {
                    jsonUtil = new JsonUtil();
                }
            }
        }
        return jsonUtil;
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public <T> T toObject(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

}
