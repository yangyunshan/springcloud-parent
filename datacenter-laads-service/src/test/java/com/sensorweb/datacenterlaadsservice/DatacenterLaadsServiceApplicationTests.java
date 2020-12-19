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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String start = format.format(calendar.getTime());
        String stop = format.format(calendar.getTime()).replace("00:00:00", "23:59:59");
        String bbox = "90.55,24.5,112.417,34.75";//长江流域经纬度范围
        insertLAADSService.insertData("AM1M", start, stop, bbox);
        System.out.println("!!!");
    }

}
