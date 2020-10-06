package com.sensorweb.sosobsservice.controller;

import com.sensorweb.sosobsservice.feign.FeignClientTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    @Autowired
    private FeignClientTest feignClientTest;

    public String getInfo(String name) {
        String info = feignClientTest.getInfo(name);
        return info;
    }
}
