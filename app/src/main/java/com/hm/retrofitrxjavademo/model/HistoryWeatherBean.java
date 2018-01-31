package com.hm.retrofitrxjavademo.model;

/**
 * Created by dumingwei on 2018/1/30 0030.
 */

public class HistoryWeatherBean {

    @Override
    public String toString() {
        return "{uptime:" + uptime + ",citynm:" + citynm + ",weather:" + weather + "}";
    }

    /**
     * weaid : 100
     * week : 星期二
     * cityno : zjtaizhou
     * citynm : 台州
     * cityid : 101210601
     * uptime : 2018-01-30 00:40:00
     * temperature : 0℃
     * humidity : 63%
     * aqi : 159
     * weather : 多云
     * weather_icon : http://api.k780.com/upload/weather/d/1.gif
     * wind : 西北风
     * winp : 3级
     * temp : 0
     * weatid : 2
     * windid : 15
     * winpid : 202
     */

    private String weaid;
    private String week;
    private String cityno;
    private String citynm;
    private String cityid;
    private String uptime;
    private String temperature;
    private String humidity;
    private String aqi;
    private String weather;
    private String weather_icon;
    private String wind;
    private String winp;
    private String temp;
    private String weatid;
    private String windid;
    private String winpid;

    public String getWeaid() {
        return weaid;
    }

    public void setWeaid(String weaid) {
        this.weaid = weaid;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getCityno() {
        return cityno;
    }

    public void setCityno(String cityno) {
        this.cityno = cityno;
    }

    public String getCitynm() {
        return citynm;
    }

    public void setCitynm(String citynm) {
        this.citynm = citynm;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeather_icon() {
        return weather_icon;
    }

    public void setWeather_icon(String weather_icon) {
        this.weather_icon = weather_icon;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWinp() {
        return winp;
    }

    public void setWinp(String winp) {
        this.winp = winp;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWeatid() {
        return weatid;
    }

    public void setWeatid(String weatid) {
        this.weatid = weatid;
    }

    public String getWindid() {
        return windid;
    }

    public void setWindid(String windid) {
        this.windid = windid;
    }

    public String getWinpid() {
        return winpid;
    }

    public void setWinpid(String winpid) {
        this.winpid = winpid;
    }
}
