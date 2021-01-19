package com.sensorweb.datacentergeeservice;

import com.google.auth.http.HttpTransportFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.collect.Lists;
import com.sensorweb.datacentergeeservice.service.GoogleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
class DatacenterGeeServiceApplicationTests {

    @Autowired
    private GoogleService googleService;
    @Test
    void contextLoads() throws IOException {
//        String path = "D:\\OneDrive\\GoogleEarthEngine\\GetGeoInfo-d670c0a18233.json";
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream("src/main/resources/GetGeoInfo-d670c0a18233.json"));
//        GoogleCredentials googleCredentials = googleService.getCredentials();
        System.out.println("");
    }

}
