package com.sensorweb.datacenterproductservice.entity;

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

    private String serviceTarget;

    public Product(String productId, String productName, String productDescription, String productKeyword, String manufactureDate, String organizationName, String serviceAddress, String downloadAddress, String productType, String serviceTarget) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productKeyword = productKeyword;
        this.manufactureDate = manufactureDate;
        this.organizationName = organizationName;
        this.serviceName = serviceAddress;
        this.downloadAddress = downloadAddress;
        this.productType = productType;
        this.serviceTarget = serviceTarget;
    }

    public Product() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId == null ? null : productId.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription == null ? null : productDescription.trim();
    }

    public String getProductKeyword() {
        return productKeyword;
    }

    public void setProductKeyword(String productKeyword) {
        this.productKeyword = productKeyword == null ? null : productKeyword.trim();
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate == null ? null : manufactureDate.trim();
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName == null ? null : organizationName.trim();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName == null ? null : serviceName.trim();
    }

    public String getDownloadAddress() {
        return downloadAddress;
    }

    public void setDownloadAddress(String downloadAddress) {
        this.downloadAddress = downloadAddress == null ? null : downloadAddress.trim();
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType == null ? null : productType.trim();
    }

    public String getServiceTarget() {
        return serviceTarget;
    }

    public void setServiceTarget(String serviceTarget) {
        this.serviceTarget = serviceTarget == null ? null : serviceTarget.trim();
    }
}