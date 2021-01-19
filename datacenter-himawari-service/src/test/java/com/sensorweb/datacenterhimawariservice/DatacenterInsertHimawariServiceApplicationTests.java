package com.sensorweb.datacenterhimawariservice;

import com.sensorweb.datacenterhimawariservice.service.InsertHimawariService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatacenterInsertHimawariServiceApplicationTests {

    @Autowired
    private InsertHimawariService insertHimawariService;
    @Test
    void contextLoads() {
        insertHimawariService.insertDataByHour();
    }

}
