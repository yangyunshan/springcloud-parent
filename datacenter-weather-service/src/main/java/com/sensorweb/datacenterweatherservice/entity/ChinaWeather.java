package com.sensorweb.datacenterweatherservice.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class ChinaWeather {
    private int id;
    private String stationId;
    private Instant queryTime;
    private String precipitation;
    private Instant updateTime;
    private String pressure;
    private String windD;
    private String windP;
    private String humidity;
    private String temperature;
    private String wp;
    private String qs;
}
