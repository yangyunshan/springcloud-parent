package com.sensorweb.datacenterairservice.entity;

import io.swagger.models.auth.In;

import java.time.Instant;

public class ChinaAirQualityHour {
    private Integer id;

    private String aqi;

    private String area;

    private String co;

    private String co24h;

    private String no2;

    private String no224h;

    private String o3;

    private String o324h;

    private String o38h;

    private String o38h24h;

    private String pm10;

    private String pm1024h;

    private String pm25;

    private String pm2524h;

    private String positionName;

    private String primaryPollutant;

    private String quality;

    private String so2;

    private String so224h;

    private String stationCode;

    private Instant timePoint;

    public ChinaAirQualityHour(Integer id, String aqi, String area, String co, String co24h, String no2, String no224h, String o3, String o324h, String o38h, String o38h24h, String pm10, String pm1024h, String pm25, String pm2524h, String positionName, String primaryPollutant, String quality, String so2, String so224h, String stationCode, Instant timePoint) {
        this.id = id;
        this.aqi = aqi;
        this.area = area;
        this.co = co;
        this.co24h = co24h;
        this.no2 = no2;
        this.no224h = no224h;
        this.o3 = o3;
        this.o324h = o324h;
        this.o38h = o38h;
        this.o38h24h = o38h24h;
        this.pm10 = pm10;
        this.pm1024h = pm1024h;
        this.pm25 = pm25;
        this.pm2524h = pm2524h;
        this.positionName = positionName;
        this.primaryPollutant = primaryPollutant;
        this.quality = quality;
        this.so2 = so2;
        this.so224h = so224h;
        this.stationCode = stationCode;
        this.timePoint = timePoint;
    }

    public ChinaAirQualityHour() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi == null ? null : aqi.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co == null ? null : co.trim();
    }

    public String getCo24h() {
        return co24h;
    }

    public void setCo24h(String co24h) {
        this.co24h = co24h == null ? null : co24h.trim();
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2 == null ? null : no2.trim();
    }

    public String getNo224h() {
        return no224h;
    }

    public void setNo224h(String no224h) {
        this.no224h = no224h == null ? null : no224h.trim();
    }

    public String getO3() {
        return o3;
    }

    public void setO3(String o3) {
        this.o3 = o3 == null ? null : o3.trim();
    }

    public String getO324h() {
        return o324h;
    }

    public void setO324h(String o324h) {
        this.o324h = o324h == null ? null : o324h.trim();
    }

    public String getO38h() {
        return o38h;
    }

    public void setO38h(String o38h) {
        this.o38h = o38h == null ? null : o38h.trim();
    }

    public String getO38h24h() {
        return o38h24h;
    }

    public void setO38h24h(String o38h24h) {
        this.o38h24h = o38h24h == null ? null : o38h24h.trim();
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10 == null ? null : pm10.trim();
    }

    public String getPm1024h() {
        return pm1024h;
    }

    public void setPm1024h(String pm1024h) {
        this.pm1024h = pm1024h == null ? null : pm1024h.trim();
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25 == null ? null : pm25.trim();
    }

    public String getPm2524h() {
        return pm2524h;
    }

    public void setPm2524h(String pm2524h) {
        this.pm2524h = pm2524h == null ? null : pm2524h.trim();
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName == null ? null : positionName.trim();
    }

    public String getPrimaryPollutant() {
        return primaryPollutant;
    }

    public void setPrimaryPollutant(String primaryPollutant) {
        this.primaryPollutant = primaryPollutant == null ? null : primaryPollutant.trim();
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality == null ? null : quality.trim();
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2 == null ? null : so2.trim();
    }

    public String getSo224h() {
        return so224h;
    }

    public void setSo224h(String so224h) {
        this.so224h = so224h == null ? null : so224h.trim();
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode == null ? null : stationCode.trim();
    }

    public Instant getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(Instant timePoint) {
        this.timePoint = timePoint;
    }
}