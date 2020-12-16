package com.sensorweb.datacenterproductservice.service;

import com.sensorweb.datacenterproductservice.dao.ProductMapper;
import com.sensorweb.datacenterproductservice.entity.Product;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductMapper productMapper;

    @Value("${datacenter.path.product}")
    private String filePath;

    @Value("${datacenter.domain}")
    private String domain;

    public List<Product> getproductByServiceAndTime(String service, String time) {
        return productMapper.selectByServiceAndTime(service, time);
    }

    public void InsertProduct(Product info) {
        String url = info.getDownloadAddress();
        String serviceName = info.getServiceName();
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String wholePath = "";
        try {
            if (serviceName.equals("PMStation") && url.endsWith("Interpolated.tif")) {
                File temp = new File(filePath + "Interpolated/");
                if (!temp.exists()) {
                    temp.mkdirs();
                }
                DataCenterUtils.downloadHttpUrl(url, filePath + "Interpolated/", fileName);
                wholePath = filePath + "Interpolated/" + fileName;
                info.setDownloadAddress(domain + "/" + filePath.substring(10) + "Interpolated/" + fileName);
            } else {
                File file = new File(filePath + serviceName + "/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                DataCenterUtils.downloadHttpUrl(url, filePath + serviceName + "/", fileName);
                wholePath = filePath + serviceName + "/" + fileName;
                info.setDownloadAddress(domain + "/" + filePath.substring(10) + serviceName + "/" + fileName);
            }
            productMapper.insertSelective(info);
            System.out.println("LAADSProduct: " + fileName + "Registry Success!" + "Path: " + wholePath);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
