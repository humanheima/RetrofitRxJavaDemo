package com.hm.retrofitrxjavademo.model;

/**
 * Created by dumingwei on 2019/4/16.
 * Desc:
 */
public class NowWeatherPM25 {

    private NowWeather nowWeather;

    private PM25 pm25;

    public NowWeatherPM25() {
    }

    public NowWeatherPM25(NowWeather nowWeather, PM25 pm25) {
        this.nowWeather = nowWeather;
        this.pm25 = pm25;
    }

    public NowWeather getNowWeather() {
        return nowWeather;
    }

    public void setNowWeather(NowWeather nowWeather) {
        this.nowWeather = nowWeather;
    }

    public PM25 getPm25() {
        return pm25;
    }

    public void setPm25(PM25 pm25) {
        this.pm25 = pm25;
    }
}
