package com.sensorweb.sosobsservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "sos-sensor-service")
public interface FeignClientTest {

    @RequestMapping("/getInfo")
    String getInfo(String name);
}
