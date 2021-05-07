package com.sensorweb.datacenterlaadsservice;

import com.sensorweb.datacenterlaadsservice.service.InsertLAADSService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SpringBootTest
class DatacenterLaadsServiceApplicationTests {

    @Autowired
    private InsertLAADSService insertLAADSService;
    @Test
    void contextLoads() throws Exception {
    }

}
