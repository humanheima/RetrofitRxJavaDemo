package com.hm.retrofitrxjavademo.network.api_entity;

/**
 * Created by dumingwei on 2018/1/30 0030.
 */

public class HistoryWeatherEntity extends BaseEntity {

    public HistoryWeatherEntity(String interfaceName, int weaid, String date,String appkey, String sign, String format) {
        url = "http://api.k780.com:88/";
        map.put("app", interfaceName);
        map.put("date", date);
        map.put("weaid", weaid);
        map.put("appkey", appkey);
        map.put("sign", sign);
        map.put("format", format);
    }
}
