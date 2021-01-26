package com.sensorweb.datacenterhimawariservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sos-sensor-service")
public interface SensorFeignClient {
    @GetMapping(value = "isExist/{id}")
    boolean isExist(@PathVariable("id") String procedureId);
}
