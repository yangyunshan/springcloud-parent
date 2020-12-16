package com.sensorweb.datacenterhimawariservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "sos-sensor-service")
public interface SensorFeignClient {
    @GetMapping(path = "sensor/getAllProcedureInfo")
    Map<String, Object> getAllProcedure();

    @GetMapping(value = "sensor/isExist/{id}")
    boolean isExist(@PathVariable("id") String procedureId);
}
