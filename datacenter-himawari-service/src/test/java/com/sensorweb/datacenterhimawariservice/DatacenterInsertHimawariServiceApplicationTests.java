package com.sensorweb.datacenterhimawariservice;

import com.sensorweb.datacenterhimawariservice.service.InsertHimawariService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootTest
class DatacenterInsertHimawariServiceApplicationTests {

    @Autowired
    private InsertHimawariService insertHimawariService;
    @Test
    void contextLoads() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("UTC")).minusHours(5);
        insertHimawariService.insertData(dateTime);
        System.out.println("!");
    }

}
