package com.hm.retrofitrxjavademo.network.api_entity;

import java.util.HashMap;

/**
 * Created by dumingwei on 2018/1/30 0030.
 */

public abstract class BaseEntity {

    protected HashMap<String, Object> map;
    protected String url;

    public BaseEntity() {
        map = new HashMap<>();
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, Object> getParams() {
        return map;
    }
}
