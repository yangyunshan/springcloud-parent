package com.sensorweb.sosobsservice.controller;

import com.sensorweb.sosobsservice.feign.FeignClientTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    @Autowired
    private FeignClientTest feignClientTest;

    @GetMapping("/test")
    public String getAllProcedure() {
        String info = feignClientTest.getAllProcedure();
        System.out.println(info);
        return info;
    }
}
