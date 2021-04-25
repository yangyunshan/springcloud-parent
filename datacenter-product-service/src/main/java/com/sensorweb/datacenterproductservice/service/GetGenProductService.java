package com.sensorweb.datacenterproductservice.service;

import com.sensorweb.datacenterproductservice.dao.GenProductMapper;
import com.sensorweb.datacenterproductservice.entity.GenProduct;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GetGenProductService {

    @Autowired
    private GenProductMapper genProductMapper;

    public GenProduct getGenProductById(int id) {
        return genProductMapper.selectById(id);
    }

    public GenProduct getGenProductByType(String type) {
        return genProductMapper.selectByType(type);
    }

    public List<GenProduct> getGenProducts() {
        return genProductMapper.selectAll();
    }
}
