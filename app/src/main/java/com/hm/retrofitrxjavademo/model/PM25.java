package com.hm.retrofitrxjavademo.model;

/**
 * Created by dumingwei on 2018/1/30 0030.
 */

public class PM25 {

    /**
     * weaid : 1
     * cityno : beijing
     * citynm : 北京
     * cityid : 101010100
     * aqi : 31
     * aqi_scope : 0-50
     * aqi_levid : 1
     * aqi_levnm : 优
     * aqi_remark : 参加户外活动呼吸清新空气
     */

    private String weaid;
    private String cityno;
    private String citynm;
    private String cityid;
    private String aqi;
    private String aqi_scope;
    private String aqi_levid;
    private String aqi_levnm;
    private String aqi_remark;

    public String getWeaid() {
        return weaid;
    }

    public void setWeaid(String weaid) {
        this.weaid = weaid;
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

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getAqi_scope() {
        return aqi_scope;
    }

    public void setAqi_scope(String aqi_scope) {
        this.aqi_scope = aqi_scope;
    }

    public String getAqi_levid() {
        return aqi_levid;
    }

    public void setAqi_levid(String aqi_levid) {
        this.aqi_levid = aqi_levid;
    }

    public String getAqi_levnm() {
        return aqi_levnm;
    }

    public void setAqi_levnm(String aqi_levnm) {
        this.aqi_levnm = aqi_levnm;
    }

    public String getAqi_remark() {
        return aqi_remark;
    }

    public void setAqi_remark(String aqi_remark) {
        this.aqi_remark = aqi_remark;
    }

    @Override
    public String toString() {
        return "{+" + "aqi_levnm:" + aqi_levnm + ",aqi_remark:" + aqi_remark + "+}";
    }

}
