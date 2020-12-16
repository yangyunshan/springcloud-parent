package com.sensorweb.datacenterhimawariservice;

import com.sensorweb.datacenterhimawariservice.service.HimawariService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatacenterHimawariServiceApplicationTests {

    @Autowired
    private HimawariService himawariService;
    @Test
    void contextLoads() {
        himawariService.insertDataByHour();
    }

}
