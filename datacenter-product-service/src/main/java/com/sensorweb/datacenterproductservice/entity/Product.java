package com.sensorweb.datacenterproductservice.entity;

import lombok.Data;

@Data
public class Product {
    private int id;

    private String productId;

    private String productName;

    private String productDescription;

    private String productKeyword;

    private String manufactureDate;

    private String organizationName;

    private String serviceName;

    private String downloadAddress;

    private String productType;

    private String timeResolution;

    private String spatialResolution;

    private int dimension;

    private String serviceTarget;
}