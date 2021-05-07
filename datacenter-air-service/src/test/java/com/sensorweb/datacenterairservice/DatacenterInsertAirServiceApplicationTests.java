package com.sensorweb.datacenterairservice;

import com.sensorweb.datacenterairservice.dao.TWEPAMapper;
import com.sensorweb.datacenterairservice.entity.ChinaAirQualityHour;
import com.sensorweb.datacenterairservice.entity.TWEPA;
import com.sensorweb.datacenterairservice.service.InsertChinaAirService;
import com.sensorweb.datacenterairservice.service.InsertTWEPA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
class DatacenterInsertAirServiceApplicationTests {

    @Test
    void contextLoads() throws Exception {
    }


}
