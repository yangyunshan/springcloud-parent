package com.sensorweb.datacenterproductservice.service;

import com.sensorweb.datacenterproductservice.dao.GenProductMapper;
import com.sensorweb.datacenterproductservice.entity.GenProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
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
