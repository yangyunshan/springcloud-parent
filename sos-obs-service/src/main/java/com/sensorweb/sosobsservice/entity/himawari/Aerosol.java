package com.sensorweb.sosobsservice.entity.himawari;

import java.time.Instant;

public class Aerosol {
    private String name;
    private Instant time;
    private String area = "full-disk";
    private int pixelNum = 2401;
    private int lineNum = 2401;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getPixelNum() {
        return pixelNum;
    }

    public void setPixelNum(int pixelNum) {
        this.pixelNum = pixelNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}