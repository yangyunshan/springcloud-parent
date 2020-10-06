package com.sensorweb.sosobsservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "sos-sensor-service", fallback = FeignClientFallback.class)
public interface FeignClientTest {

    @GetMapping(path = "/getAllProcedureInfo")
    String getAllProcedure();
}
