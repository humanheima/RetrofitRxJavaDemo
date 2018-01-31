package com.hm.retrofitrxjavademo.network.api_entity;

/**
 * Created by dumingwei on 2018/1/30 0030.
 * 豆瓣top250的请求参数
 */

public class MovieEntity extends BaseEntity {

    /**
     * @param start 起始数据位置
     * @param count 请求几条数据
     */
    public MovieEntity(int start, int count) {
        url = "https://api.douban.com/v2/movie/top250";
        map.put("start", start);
        map.put("count", count);
    }
}
