package com.sensorweb.sossensorservice.entity.sos;


public class Procedure {
    private String id;
    private String descriptionFormat;
    private String name;
    private String description;
    private int isPlatform;//0代表非平台，1代表平台
    private String descriptionFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescriptionFormat() {
        return descriptionFormat;
    }

    public void setDescriptionFormat(String descriptionFormat) {
        this.descriptionFormat = descriptionFormat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsPlatform() {
        return isPlatform;
    }

    public void setIsPlatform(int isPlatForm) {
        this.isPlatform = isPlatForm;
    }

    public String getDescriptionFile() {
        return descriptionFile;
    }

    public void setDescriptionFile(String descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

}
