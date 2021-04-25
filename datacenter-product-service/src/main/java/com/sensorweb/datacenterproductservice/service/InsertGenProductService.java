package com.sensorweb.datacenterproductservice.service;

import com.sensorweb.datacenterproductservice.dao.GenProductMapper;
import com.sensorweb.datacenterproductservice.entity.GenProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InsertGenProductService {

    @Autowired
    private GenProductMapper genProductMapper;

    public boolean insertGenProduct(GenProduct genProduct) {
        int status = genProductMapper.insertData(genProduct);
        return status > 0;
    }
}
