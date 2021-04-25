package com.sensorweb.datacentergeeservice.entity;

import lombok.Data;

import java.time.Instant;
@Data
public class Img {

    private String imageID;
    private String sensorID;
    private String spacecraftID;
    private String Coordinates;
    private String Date;
    private String Time;
    private String imageSize;
    private String Ellipsoid;
    private String Cloudcover;
    private String Thumburl;
    private String imageType;
    private String filePath;


    public String getImageID() {
        return imageID;
    }
    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getSpacecraftID() {
        return spacecraftID;
    }

    public void setSpacecraftID(String spacecraftID) {
        this.spacecraftID = spacecraftID;
    }

    public String getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(String coordinates) {
        Coordinates = coordinates;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getEllipsoid() {
        return Ellipsoid;
    }

    public void setEllipsoid(String ellipsoid) {
        Ellipsoid = ellipsoid;
    }

    public String getCloudcover() {
        return Cloudcover;
    }

    public void setCloudcover(String cloudcover) {
        Cloudcover = cloudcover;
    }

    public String getThumburl() {
        return Thumburl;
    }

    public void setThumburl(String thumburl) {
        Thumburl = thumburl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
